package com.allen.gcanalyzer;

import com.microsoft.gctoolkit.GCToolKit;
import com.microsoft.gctoolkit.io.SingleGCLogFile;
import com.microsoft.gctoolkit.jvm.JavaVirtualMachine;
import lombok.var;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author: allen
 * @Date: 2025/1/7 17:18
 * @Description:
 **/
public class Main {

    public static void main(String[] args) throws Exception {
        Path path = Paths.get(System.getProperty("gclog"));
        SingleGCLogFile logFile = new SingleGCLogFile(path);
        GCToolKit gcToolKit = new GCToolKit();
        JavaVirtualMachine jvm = gcToolKit.analyze(logFile);
        var results = jvm.getAggregation(HeapOccupancyAfterCollectionSummary.class);
        System.out.println(results.toString());
    }
}
