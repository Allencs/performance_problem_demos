package com.allen.resourceMonitor;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * @Author: allen
 * @Date: 2024/4/13 21:43
 * @Description:
 **/
public class SystemStatsOsBean {

    private static com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean)
            ManagementFactory.getOperatingSystemMXBean();

    public static void getCpuAndMemStats() {
        System.out.println("CPU核数：" + osBean.getAvailableProcessors());
        for (;;) {
            // 获取系统的CPU负载
            double systemCpuLoad = osBean.getSystemCpuLoad() * 100;
            System.out.println("系统CPU负载: " + String.format("%.2f", systemCpuLoad) + "%");

            // 获取进程的CPU负载
            double processCpuLoad = osBean.getProcessCpuLoad() * 100;
            System.out.println("进程CPU负载: " + String.format("%.2f", processCpuLoad) + "%");

            // 获取系统的物理内存
            long totalPhysicalMemorySize = osBean.getTotalPhysicalMemorySize();
            long freePhysicalMemorySize = osBean.getFreePhysicalMemorySize();
            long usedPhysicalMemorySize = totalPhysicalMemorySize - freePhysicalMemorySize;

            System.out.println("总物理内存: " + totalPhysicalMemorySize / (1024 * 1024) + " MB");
            System.out.println("已用物理内存: " + usedPhysicalMemorySize / (1024 * 1024) + " MB");
            System.out.println("剩余物理内存: " + freePhysicalMemorySize / (1024 * 1024) + " MB");
            System.out.println("------------------------");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        getCpuAndMemStats();
    }
}
