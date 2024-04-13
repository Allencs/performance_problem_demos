package com.allen.resourceMonitor;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.util.Util;

/**
 * @Author: allen
 * @Date: 2024/4/13 22:06
 * @Description:
 **/
public class SystemStatsOshi {

    private static final SystemInfo SI = new SystemInfo();
    private static final HardwareAbstractionLayer HAL = SI.getHardware();
    private static final CentralProcessor PROCESSOR = HAL.getProcessor();
    private static final GlobalMemory MEMORY = HAL.getMemory();
    public static void getCpuAndMemStats() {
        while (true) {
            // 获取CPU使用率
            long[] prevTicks = PROCESSOR.getSystemCpuLoadTicks();
            // 等待一秒钟以获取更准确的读数
            Util.sleep(1000);
            double cpuLoad = PROCESSOR.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
            System.out.println("CPU使用率: " + String.format("%.2f", cpuLoad) + "%");

            // 获取内存使用率
            long totalMemory = MEMORY.getTotal();
            long availableMemory = MEMORY.getAvailable();
            long usedMemory = totalMemory - availableMemory;
            double memoryUsage = (double) usedMemory / totalMemory * 100;
            System.out.println("总内存: " + totalMemory / (1024 * 1024) + " MB");
            System.out.println("已用内存: " + usedMemory / (1024 * 1024) + " MB");
            System.out.println("内存使用率: " + String.format("%.2f", memoryUsage) + "%");
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
