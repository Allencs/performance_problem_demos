
ClassLoader:                                                                              
+-sun.misc.Launcher$AppClassLoader@18b4aac2                                               
  +-sun.misc.Launcher$ExtClassLoader@21cafffc                                             

Location:                                                                                 
/Users/chensheng/IdeaProjects/Perf-Tuning/performance_problem_demos/target/classes/       

       /*
        * Decompiled with CFR.
        */
       package com.allen.testReflect;
       
       import java.lang.reflect.InvocationTargetException;
       import java.lang.reflect.Method;
       
       public class TestRef {
           public static void main(String[] args) {
               try {
/*10*/             Class<?> clazz = Class.forName("com.allen.entities.CommonTestEntity");
/*11*/             Object refTest = clazz.newInstance();
/*12*/             Method method = clazz.getMethod("defaultMethod", new Class[0]);
/*13*/             clazz.getMethods();
/*14*/             Method method1 = clazz.getDeclaredMethod("defaultMethod", new Class[0]);
/*15*/             method.invoke(refTest, new Object[0]);
/*16*/             for (int i = 0; i < 100000; ++i) {
/*17*/                 Thread.sleep(1000L);
/*18*/                 method.invoke(refTest, new Object[0]);
                   }
               }
               catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InterruptedException | NoSuchMethodException | InvocationTargetException e) {
/*21*/             e.printStackTrace();
               }
           }
       }

Affect(row-cnt:1) cost in 541 ms.
