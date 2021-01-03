package com.linsen.jvm.reflect;

import java.lang.reflect.Method;

/**
 * @author lane.lin
 * @Description TODO
 * @since 2020/11/12
 */
public class TestV2 {

    public static void target(int i) {
        new Exception("#" + i).printStackTrace();
    }

    public static void main(String[] args) throws Exception {
        Class klass = Class.forName("com.linsen.jvm.reflect.TestV2");
        Method method = klass.getMethod("target", int.class);
        for(int i = 0; i < 20; i++){
            method.invoke(null, i);
        }
    }
}
