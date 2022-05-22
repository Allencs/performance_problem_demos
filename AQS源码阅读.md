## AQS核心原理
- 自旋
- LockSupport(线程阻塞)
- CAS(并发设值)
- queue

## 前置知识
参考文章：[从ReentrantLock的实现看AQS的原理及应用](https://tech.meituan.com/2019/12/05/aqs-theory-and-apply.html)

### CLH队列中的Node对象属性
| 方法和属性       | 含义                    |
|-------------|-----------------------|
| waitStatus  | 当前节点在队列中的状态           |
| thread      | 表示处于该节点的线程            |
| prev        | 	前驱指针                 |
| predecessor | 返回前驱节点，没有的话抛出npe      |
| nextWaiter  | 指向下一个处于CONDITION状态的节点 |
| next        | 后继指针                  |

### CLH队列中node的waitStatus属性值含义

| 枚举         | 含义                                        |
|------------|-------------------------------------------|
| 0          | 当一个Node被初始化的时候的默认值                        |
| CANCELLED	 | 为1，表示线程获取锁的请求已经取消了                        |
| CONDITION	 | 为-2，表示节点在等待队列中，节点线程等待唤醒                   |
| PROPAGATE	 | 为-3，当前线程处在SHARED情况下，该字段才会使用，且只能设置在head节点上 |
| SIGNAL	    | 为-1，表示线程已经准备好了，就等资源释放了                    |

## 自旋
互斥锁有一个很大的缺点，即获取锁失败后线程会进入睡眠或阻塞状态。这个过程会涉及到用户态到内核态的调度，上下文切换等操作。
如果某个锁的锁定时间很短，此时如果锁获取失败则让它睡眠或阻塞，这种开销可能比自旋的开销更大。

AQS中的自旋应用大部分使用死循环的方式：
1. `addWaiter()`方法中的死循环
2. `acquireQueued()`方法中的死循环
3. `doAcquireInterruptibly`方法中的死循环
4. `doAcquireShared`等等
常见核心方法`addWaiter()`、`acquireQueued()`、`doAcquireShared`

## LockSupport(线程阻塞)
线程在CLH队列中通过调用`acquireQueued()`方法尝试获取锁失败后，会调用`shouldParkAfterFailedAcquire`方法，进行阻塞。

### `acquireQueued()`方法
```java
    /**
     * Acquires in exclusive uninterruptible mode for thread already in
     * queue. Used by condition wait methods as well as acquire.
     * 已经在CLH队列中，以独占模式获取锁
     * @param node the node
     * @param arg the acquire argument
     * @return {@code true} if interrupted while waiting
     */
    final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    // 将获得锁的节点设置成新的head节点
                    // 并将新的head节点的thread和prev属性设置成null
                    setHead(node);
                    // 旧head的next指针置空，帮助垃圾回收
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
                // 没有获取到锁，尝试进行Park
                // 只有shouldParkAfterFailedAcquire返回true，才会执行parkAndCheckInterrupt
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
```

### `shouldParkAfterFailedAcquire`方法
```java
    /**
     * Checks and updates status for a node that failed to acquire.
     * 获取锁失败后，检查更新node的状态
     * Returns true if thread should block. This is the main signal
     * 如果线程需要阻塞，返回true
     * control in all acquire loops.  Requires that pred == node.prev.
     * @param pred node's predecessor holding status
     * @param node the node
     * @return {@code true} if thread should block
     */
    private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus;
        if (ws == Node.SIGNAL)
            /*
             * This node has already set status asking a release
             * to signal it, so it can safely park.
             * 前驱节点已经设置了SIGNAL标识，可以安全的park了。
             */
            return true;
        if (ws > 0) {
            /*
             * Predecessor was cancelled. Skip over predecessors and
             * indicate retry.
             * 如果前驱节点是CANCELLED状态，则需要跳过这些CANCELLED节点，直到
             * <=0的节点，构建新的前驱指针指向
             */
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {
            /*
             * waitStatus must be 0 or PROPAGATE.  Indicate that we
             * waitStatus必须是0或者PROPAGATE。表示还没Park，需要将ws设置成SIGNAL
             * need a signal, but don't park yet.  Caller will need to
             * retry to make sure it cannot acquire before parking.
             */
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }
```
对应的，在 `release()`方法中，会有唤醒的操作
```java
/**
     * Releases in exclusive mode.  Implemented by unblocking one or
     * more threads if {@link #tryRelease} returns true.
     * This method can be used to implement method {@link Lock#unlock}.
     *
     * @param arg the release argument.  This value is conveyed to
     *        {@link #tryRelease} but is otherwise uninterpreted and
     *        can represent anything you like.
     * @return the value returned from {@link #tryRelease}
     */
    public final boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            // head的waitStatus不为0，开始唤醒后继节点线程
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);
            return true;
        }
        return false;
    }
```

```java
    /**
     * Wakes up node's successor, if one exists.
     *
     * @param node the node
     */
    private void unparkSuccessor(Node node) {
        /*
         * If status is negative (i.e., possibly needing signal) try
         * to clear in anticipation of signalling.  It is OK if this
         * fails or if status is changed by waiting thread.
         */
        int ws = node.waitStatus;
        if (ws < 0)
            // 将head的waitStatus设置成0【中间状态】
            // 如果head的后继唤醒后能获得锁，那么head的后继就会成为新的head，只要新head不为tail，那么新head的状态一般都为SIGNAL
            compareAndSetWaitStatus(node, ws, 0);

        /*
         * Thread to unpark is held in successor, which is normally
         * just the next node.  But if cancelled or apparently null,
         * traverse backwards from tail to find the actual
         * non-cancelled successor.
         * 如果head的后继节点是CANCELLED状态或者null，则从尾部向前遍历非CANCELLED状态的节点
         */
        Node s = node.next;
        if (s == null || s.waitStatus > 0) {
            s = null;
            for (Node t = tail; t != null && t != node; t = t.prev)
                if (t.waitStatus <= 0)
                    s = t;
        }
        if (s != null)
            LockSupport.unpark(s.thread);
    }
```