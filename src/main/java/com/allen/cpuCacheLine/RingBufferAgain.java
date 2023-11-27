package com.allen.cpuCacheLine;
import com.allen.models.NewPerson;
import com.allen.utils.UuidUtil;
import com.lmax.disruptor.util.Util;
import sun.misc.Unsafe;

import java.lang.reflect.InvocationTargetException;

public class RingBufferAgain {

    private static final int BUFFER_PAD;
    private static final long REF_ARRAY_BASE;
    private static final int REF_ELEMENT_SHIFT;
    private static final Unsafe UNSAFE = Util.getUnsafe();

    private final long indexMask;
    private final Object[] entries;
    protected final int bufferSize;

    RingBufferAgain(int bufferSize) {
        this.entries = new Object[bufferSize + 2 * BUFFER_PAD];
        this.bufferSize = bufferSize;
        this.indexMask = bufferSize - 1;
    }

    static
    {
        // 获取数组中一个元素的大小(get size of an element in the array)
        // 数组元素【对象引用】大小（指针压缩：4字节）
        final int scale = UNSAFE.arrayIndexScale(Object[].class);
        if (4 == scale)
        {
            REF_ELEMENT_SHIFT = 2;
        }
        else if (8 == scale)
        {
            REF_ELEMENT_SHIFT = 3;
        }
        else
        {
            throw new IllegalStateException("Unknown pointer size");
        }
        BUFFER_PAD = 128 / scale;
        System.out.println("BUFFER_PAD: " + BUFFER_PAD);
        // Including the buffer pad in the array base offset 包含了缓冲填充数据的偏移量
        // mark word标记字占用8字节，klass point类型指针占用4字节，数组对象特有的数组长度部分占用4字节，总共占用了16字节
        // 添加32个PAD填充，每个PAD 4字节，总偏移量为144
        // 获取数组中第一个元素的偏移量(get offset of a first element in the array)，这里值为16 + 128
        REF_ARRAY_BASE = UNSAFE.arrayBaseOffset(Object[].class) + (BUFFER_PAD << REF_ELEMENT_SHIFT);
        System.out.println("REF_ARRAY_BASE: " + REF_ARRAY_BASE);
    }

    private void fill(Class<?> clazz)
    {
        for (int i = 0; i < bufferSize; i++)
        {
            // BUFFER_PAD = 32
            try {
                System.out.println("数组下标：" + (BUFFER_PAD + i));
                entries[BUFFER_PAD + i] = clazz.getDeclaredConstructor(clazz.getDeclaredConstructors()[0].getParameterTypes()).newInstance(UuidUtil.getStrUuid(), "allen" + i,
                        "address", "tel", "sheng.chen@perfma.com", "job", "Perfma", "message", i);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * sequence & indexMask是环形队列的核心，在数组长度是2的幂次方的基础上，sequence & (size - 1) 相当于 % 取模运算
     * @param sequence
     * @return
     */
    protected final Object elementAt(long sequence)
    {
        System.out.println("REF_ELEMENT_SHIFT: " + REF_ELEMENT_SHIFT);
        System.out.println("sequence & indexMask: " + (sequence & indexMask));
        System.out.println("sequence & indexMask) << REF_ELEMENT_SHIFT： " + ((sequence & indexMask) << REF_ELEMENT_SHIFT));
        System.out.println("REF_ARRAY_BASE[数组开始的内存地址偏移量] + ((sequence & indexMask) << REF_ELEMENT_SHIFT) | " + (REF_ARRAY_BASE + ((sequence & indexMask) << REF_ELEMENT_SHIFT)));

        // 根据偏移量获取数组元素值，基础偏移量 144，加数组相对下标【比如现在32为第1个元素下表，就再加上1个引用大小的偏移4字节】
        // 参考Unsafe类使用：https://www.cnblogs.com/trunks2008/p/14720811.html
        return UNSAFE.getObject(entries, REF_ARRAY_BASE + ((sequence & indexMask) << REF_ELEMENT_SHIFT));
    }

    public static void main(String[] args) throws ClassNotFoundException {

        RingBufferAgain testObjectSize = new RingBufferAgain(128);
        testObjectSize.fill(NewPerson.class);
        System.out.println(testObjectSize.elementAt(6));
        System.out.println("===================================");
        System.out.println(testObjectSize.elementAt(129));

    }
}
