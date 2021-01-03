package com.linsen.jvm.method.handle;

import java.lang.invoke.MethodHandles;

/**
 * @author lane.lin
 * @Description TODO
 * @since 2020/11/16
 */
public class Foo {

    public static void bar(Object obj){
        new Exception().printStackTrace();
    }

    public static MethodHandles.Lookup lookup(){
        return MethodHandles.lookup();
    }
}
