package com.huawei.codecraft.test;

import java.util.Arrays;

public class Test2 {
    public static void main(String[] args) {
        char [ ] c = new char[10];
        System.out.println(Arrays.toString(c));
        change(c);
        System.out.println(Arrays.toString(c));
    }
    public static void change(char[] c) {
        c[0] = 'A';
    }
}
