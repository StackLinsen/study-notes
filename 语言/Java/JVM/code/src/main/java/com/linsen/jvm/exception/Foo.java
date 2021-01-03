package com.linsen.jvm.exception;

public class Foo {
    private static int tryBlock;
    private static int catchBlock;
    private static int finallyBlock;
    private static int methodExit;

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            try {
                tryBlock = 0;
                if (i < 50) {
                    continue;
                } else if (i < 80) {
                    break;
                } else {
                    return;
                }
            } catch (Exception e) {
                catchBlock = 1;
            } finally {
                finallyBlock = 2;
            }
        }
        methodExit = 3;

        System.out.println("tryBlock = " + tryBlock);
        System.out.println("catchBlock = " + catchBlock);
        System.out.println("finallyBlock = " + finallyBlock);
    }

}