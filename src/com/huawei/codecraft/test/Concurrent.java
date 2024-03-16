package com.huawei.codecraft.test;

import java.util.ArrayList;

public class Concurrent {
    public static void main(String[] args) throws InterruptedException {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    list.add(1);
//                    System.out.println("add");
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        Thread t2 = new Thread(()->{
            final  int size =list.size();
            for (int i = 0; i < size; i++) {
                System.out.println(i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
