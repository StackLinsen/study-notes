package com.linsen.jvm.exception;

/**
 * @author lane.lin
 * @Description TODO
 * @since 2020/11/12
 */
public class AutoCloseableDemo implements AutoCloseable{

    private final String name;
    public AutoCloseableDemo(String name) {
        this.name = name; }

    @Override
    public void close() {
        throw new RuntimeException(name);
    }

    public static void main(String[] args) {
        try(AutoCloseableDemo demo1 = new AutoCloseableDemo("demo1");
            AutoCloseableDemo demo2 = new AutoCloseableDemo("demo2");
            AutoCloseableDemo demo3 = new AutoCloseableDemo("demo3");
            ){
            throw new RuntimeException("Initial");
        }
    }
}
