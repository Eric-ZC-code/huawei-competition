package com.huawei.codecraft.util;

import com.huawei.codecraft.entities.Command;

public class MessageCenter {
    public static MyLogger logger = MyLogger.getLogger("MessageCenter");
    public static int sentMsg=0;
    private static final int maxMsg = 1;

    public static synchronized void add(){
        sentMsg++;
    }
    public static synchronized void reset(){
        sentMsg=0;
    }
    public static boolean send(Command cmd){
        if(sentMsg>=maxMsg){
            //并不能保证不超过maxMsg，但是因为限制为8kb，所以只需要保证发送的命令在maxMsg左右就行
            //故没有做多线程同步
            return false;
        }
        add();
        System.out.println(cmd);
        logger.info(cmd.toString());
        return true;

    }
}
