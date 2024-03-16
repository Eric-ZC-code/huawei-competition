package com.huawei.codecraft.test;

public class SynchronizeTest {
    class Task {
        public synchronized void doSomething() throws InterruptedException {
            System.out.println("doSomething");
            Thread.sleep(10000);
        }
        public synchronized void doSomethingElse() {
            System.out.println("doSomethingElse");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Task task = new SynchronizeTest().new Task();
        Thread t1 = new Thread(() -> {



            try {
                task.doSomething();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t1.start();
        Thread t2 = new Thread(() -> {
            task.doSomethingElse();
        });
        t2.start();
        t1.join();
        t2.join();
    }
}
