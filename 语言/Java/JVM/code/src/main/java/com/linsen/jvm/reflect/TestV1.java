package com.linsen.jvm.reflect;

import java.lang.reflect.Method;

/**
 * @author lane.lin
 * @Description TODO
 * @since 2020/11/12
 */
public class TestV1 {

    public static void target(int i) {
        new Exception("#" + i).printStackTrace();
    }

    public static void main(String[] args) throws Exception {
        Class klass = Class.forName("com.linsen.jvm.reflect.TestV1");
        Method method = klass.getMethod("target", int.class);
        method.invoke(null, 0);
    }
}
