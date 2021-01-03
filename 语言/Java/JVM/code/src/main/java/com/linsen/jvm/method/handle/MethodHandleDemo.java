package com.linsen.jvm.method.handle;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

/**
 * @author lane.lin
 * @Description TODO
 * @since 2020/11/16
 */
public class MethodHandleDemo {

    public static void main(String[] args) throws Exception, Throwable {
        // methodHandles和反射对比，methodHandles权限校验在创建lookup的地方
        MethodHandles.Lookup l = Foo.lookup();

        Method m = Foo.class.getDeclaredMethod("bar", Object.class);
        MethodHandle mh0 = l.unreflect(m);

        //创建methodType 返回值，入参
        MethodType t = MethodType.methodType(void.class, Object.class);
        //找方法
        MethodHandle mh1 = l.findStatic(Foo.class, "bar", t);
        mh1.invokeWithArguments("Hello,World");
    }
}
