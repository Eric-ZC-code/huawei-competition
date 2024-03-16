package com.huawei.codecraft.util;

import com.huawei.codecraft.entities.Command;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MessageCenter {
    public static MyLogger logger = MyLogger.getLogger("MessageCenter");
    public static ReadWriteLock rwLock = new ReentrantReadWriteLock();
    public static int sentMsg=0;
    private static final int maxMsg = 1000;

    private static boolean closed = false;

    public static synchronized void add(){
        sentMsg++;
    }
    public static synchronized void reset(){
        sentMsg=0;
    }
    public static boolean send(Command cmd){
        rwLock.readLock().lock();

        try {
            if(closed){
                logger.info("refused to send because it is closed");
                return false;
            }
            if(!cmd.isBoatCmd()&&sentMsg>=maxMsg){
                //并不能保证不超过maxMsg，但是因为限制为8kb，所以只需要保证发送的命令在maxMsg左右就行
                //故没有做多线程同步
                logger.info("refused to send because it is greater than maxMsg:"+maxMsg);
                return false;
            }
            add();
            System.out.println(cmd);
            System.out.flush();
            logger.info(cmd.toString());
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.readLock().unlock();
        }

    }
    public static void close(){
        rwLock.writeLock().lock();
        try {
            closed = true;
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    public static void open(){
        rwLock.writeLock().lock();
        try {
            closed = false;
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}
