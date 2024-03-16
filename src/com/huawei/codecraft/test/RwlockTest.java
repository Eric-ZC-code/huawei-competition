package com.huawei.codecraft.test;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RwlockTest {
    public static void main(String[] args) throws InterruptedException {
        ReadWriteLock rwlock = new ReentrantReadWriteLock();

        Thread t1 = new Thread(() -> {
            rwlock.readLock().lock();
            try {
                System.out.println("t1 read lock");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                rwlock.readLock().unlock();
            }
        });

        Thread t3 = new Thread(() -> {
            rwlock.readLock().lock();
            try {
                System.out.println("t3 read lock");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                rwlock.readLock().unlock();
            }
        });

        Thread t2 = new Thread(() -> {
            rwlock.writeLock().lock();
            try {
                System.out.println("t2 write lock");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                rwlock.writeLock().unlock();
            }
        });

        t1.start();
        t2.start();


        t1.join();
        t2.join();
        t3.join();
    }
}
