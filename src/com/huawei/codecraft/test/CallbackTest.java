package com.huawei.codecraft.test;


import com.sun.org.glassfish.external.amx.MBeanListener;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class CallbackTest {
    public static void main(String[] args) throws InterruptedException {
        final long mills = 1000l;
        CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("start sleep");
                Thread.sleep(mills);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }).whenComplete((result,e)->{

            if(e!=null){
                e.printStackTrace();
            }
            System.out.println("done2");
            System.out.flush();
            System.exit(0);
        });
        Thread.sleep(1500);


    }
}
