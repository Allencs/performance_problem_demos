package com.allen.testForkJoinPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class MyTask extends RecursiveTask<AppInfo[]> {

    private final AppInfo[] source;

    public MyTask(AppInfo[] source) {
        this.source = source;
    }

    @Override
    protected AppInfo[] compute() {
        int sourceLen = source.length;
        // 如果条件成立，说明任务中要进行排序的集合还不够小
        if (sourceLen > 2) {
            int midIndex = sourceLen / 2;
            // 拆分成两个子任务
            MyTask task1 = new MyTask(Arrays.copyOf(source, midIndex));
            task1.fork();
            MyTask task2 = new MyTask(Arrays.copyOfRange(source, midIndex, sourceLen));
            task2.fork();
            // 将两个有序的数组，合并成一个有序的数组
            AppInfo[] result1 = task1.join();
            AppInfo[] result2 = task2.join();
            return joinArrays(result1, result2);
        }
        // 否则说明集合中只有一个或者两个元素，可以进行这两个元素的比较排序了
        else {
            // 如果条件成立，说明数组中只有一个元素，或者是数组中的元素都已经排列好位置了
            if (sourceLen == 1
                    || source[0].getReplicas() <= source[1].getReplicas()) {
                return source;
            } else {
                AppInfo[] targetp = new AppInfo[sourceLen];
                targetp[0] = source[1];
                targetp[1] = source[0];
                return targetp;
            }
        }
    }

    /**
     * 这个方法用于合并两个有序集合
     *
     * @param array1
     * @param array2
     */
    private static AppInfo[] joinArrays(AppInfo[] array1, AppInfo[] array2) {
        AppInfo[] destAppInfoArray = new AppInfo[array1.length + array2.length];
        int array1Len = array1.length;
        int array2Len = array2.length;
        int destLen = destAppInfoArray.length;

        // 只需要以新的集合destInts的长度为标准，遍历一次即可
        for (int index = 0, array1Index = 0, array2Index = 0; index < destLen; index++) {
            AppInfo value1 = array1Index >= array1Len ? array1[array1Len - 1] : array1[array1Index];
            AppInfo value2 = array2Index >= array2Len ? array2[array2Len - 1] : array2[array2Index];
//            AppInfo value1 = array1[array1Index];
//            AppInfo value2 = array2[array2Index];
            // 如果条件成立，说明应该取数组array1中的值
            if (value1.getReplicas() < value2.getReplicas()) {
                array1Index++;
                destAppInfoArray[index] = value1;
            }
            // 否则取数组array2中的值
            else {
                array2Index++;
                destAppInfoArray[index] = value2;
            }
        }

        return destAppInfoArray;
    }

    public static void main(String[] args) {
        AppInfo appInfo1 = AppInfo.builder().appName("Test-1").replicas(2).build();
        AppInfo appInfo2 = AppInfo.builder().appName("Test-2").replicas(5).build();
        AppInfo appInfo3 = AppInfo.builder().appName("Test-3").replicas(3).build();
        AppInfo appInfo4 = AppInfo.builder().appName("Test-4").replicas(20).build();
        AppInfo[] appInfos = new AppInfo[]{appInfo1, appInfo2, appInfo3, appInfo4};
        List<AppInfo> appInfoList = new ArrayList<>(Arrays.asList(appInfos));
//        AppInfo app = appInfoList.parallelStream().max(new Comparator<AppInfo>() {
//            @Override
//            public int compare(AppInfo o1, AppInfo o2) {
//                return o1.getReplicas().compareTo(o2.getReplicas());
//            }
//        }).get();
//        System.out.println(app);
        List<Integer> replicas = appInfoList.parallelStream().map(AppInfo::getReplicas).collect(Collectors.toList());
        System.out.println(replicas);
    }
}
