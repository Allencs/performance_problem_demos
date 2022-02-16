package com.allen.perfTuning;

import org.openjdk.jmh.annotations.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @Author: allen
 * @Date: 2022/2/16 5:45 下午
 * @Description:
 */

@State(Scope.Benchmark)
public class TestListAndHashSet {

    public static final HashSet hashSet = new HashSet();
    public static final ArrayList arrayList = new ArrayList();

    @Setup
    public void init() {

    }
}
