package com.allen.testForkJoinPool;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AppInfo {
    Integer replicas;
    String appName;
}
