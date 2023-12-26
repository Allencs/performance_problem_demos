package com.allen.testReference;

import com.allen.models.User;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * @Author: allen
 * @Date: 2023/12/26 22:11
 * @Description:
 **/
public class ReferenceQueueExample {
    public static void main(String[] args) throws Exception{
        User strongReference = new User("allen", "18");
        ReferenceQueue<User> referenceQueue = new ReferenceQueue<>();
        WeakReference<User> weakReference = new WeakReference<>(strongReference, referenceQueue);

        System.out.println("Now to call gc...");
        System.gc();
        Thread.sleep(1000);
        System.out.println("Any weak references in Queue? " + (referenceQueue.poll() != null));

        strongReference = null;

        System.out.println("strongReference is null, Now to call gc...");

//        weakReference.clear(); // 将referent置为null，weakReference对象不会进队列

        System.gc();
        Thread.sleep(1000);

        System.out.println("Is the weak reference added to the ReferenceQ  ? " + (weakReference.isEnqueued()));
        System.out.println("Does the weak reference still hold the heap object ? " + (weakReference.get() != null));

//        weakReference.clear();
//        System.gc();
//        Thread.sleep(1000);
//        System.out.println("Is the weak reference added to the ReferenceQ  ? " + (weakReference.isEnqueued()));
//        System.out.println("Does the weak reference still hold the heap object ? " + (weakReference.get() != null));

        Reference<? extends User> weakRefInQueue = referenceQueue.remove();
        System.out.println("队列中weakReference的referent：" + weakRefInQueue.get());
        System.out.println("Is this same as original weak reference ? " + (weakRefInQueue == weakReference));
        System.out.println("Is the weak reference added to the ReferenceQ  ? " + (weakReference.isEnqueued()));
    }
}
