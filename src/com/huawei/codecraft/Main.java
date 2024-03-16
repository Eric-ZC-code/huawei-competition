/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.codecraft;


import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Boat;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.task.BoatCallable;
import com.huawei.codecraft.task.RobotCallable;
import com.huawei.codecraft.util.MessageCenter;
import com.huawei.codecraft.util.MyLogger;
import com.huawei.codecraft.wrapper.MapInfo;
import com.huawei.codecraft.wrapper.impl.MapInfoimpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Main
 *
 * @since 2024-02-05
 */
public class Main {
    private static final MyLogger logger = MyLogger.getLogger(Main.class.getName());
    private static final int n = 200;
    private static final int robotNum = 10;
    private static final int berthNum = 10;
    private static final int processTime = 10;
    private HashMap<Integer, Integer> robotFrameRec = new HashMap<>();
    private int money, boatCapacity, id;
    private char[][] ch = new char[n][n];
    private MapInfo mapInfo = new MapInfoimpl();
    private Robot[] robot = new Robot[robotNum];
    private Berth[] berth = new Berth[berthNum];
    private Boat[] boat = new Boat[5];
    private Future<Robot>[] robotFuture = new Future[robotNum];
    private Future<Boat>[] boatFuture = new Future[5];
    private ExecutorService robotExecutor = Executors.newFixedThreadPool(10);
    private ExecutorService boatExecutor = Executors.newFixedThreadPool(5);

    private void init() {
        logger.info("init");

        Scanner scanf = new Scanner(System.in);
        for (int i = 0; i < n; i++) {
            String line = scanf.nextLine();
            line = line.replace('A', '.');
            ch[i] = line.toCharArray();
        }
        for (int i = 0; i < berthNum; i++) {
            int id = scanf.nextInt();
            berth[id] = new Berth();
            berth[id].setX(scanf.nextInt())
                     .setY(scanf.nextInt())
                     .setTransportTime(scanf.nextInt())
                     .setLoadingSpeed(scanf.nextInt())
                     .setId(id);

        }
        this.boatCapacity = scanf.nextInt();
        for (int i = 0; i < 5; i++) {
            boat[i] = new Boat(boatCapacity);
        }
        // init robots
        for (int i = 0; i < robotNum; i++) {
            robot[i] = new Robot();
            robotFrameRec.put(i, 0);
        }
        // init mapInfo
        mapInfo.setMap(ch);
        mapInfo.setBerths(berth);
        String okk = scanf.nextLine();
        System.out.println("OK");
        System.out.flush();
    }

    private int input() {
//        MessageCenter.open();
        Scanner scanf = new Scanner(System.in);
        this.id = scanf.nextInt();
        this.money = scanf.nextInt();
        int num = scanf.nextInt();
        for (int i = 1; i <= num; i++) {
            int x = scanf.nextInt();
            int y = scanf.nextInt();
            int val = scanf.nextInt();
            mapInfo.addGood(x, y, val);
        }
        for (int i = 0; i < robotNum; i++) {
            // {r1@0x123,r2@0x234...}
            robot[i].setCarrying(scanf.nextInt())
                    .setX(scanf.nextInt())
                    .setY(scanf.nextInt())
                    .setStatus(scanf.nextInt())
                    .setId(i);
        }
        for (int i = 0; i < 5; i++) {
            boat[i].setStatus(scanf.nextInt())
                   .setPos(scanf.nextInt())
                   .setId(i);
        }
        String okk = scanf.nextLine();
        return id;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        Main mainInstance = new Main();
        try {


            mainInstance.init();

            long l1 = System.currentTimeMillis();
            int skip = 2;
            for (int frame = 1; frame <= 15000; frame++) {

                int id = mainInstance.input();
                System.err.println("frame: "+id);
                // 一个简易的计时器保证输出中心在processtime之前关闭输出，保证不丢失输出
    //            if(frame==50){
    //                System.err.println("time: "+(System.currentTimeMillis()-l1));
    //            }
//                CompletableFuture.supplyAsync(()->{
//                    long l= System.currentTimeMillis() ;
//                    try {
//
//                        Thread.sleep(processTime);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    return l ;
//                }).whenComplete((result,e)->{
//                    logger.info("weak up");
//                    if(e!=null){
//                        e.printStackTrace();
//                    }
//    //                long end = System.currentTimeMillis();
//    //                System.err.printf("%dms\n",(end-result));
//                    logger.info("close message center");
//                    MessageCenter.close();
//                });




                if (frame == 1) {
    //                for (Robot robot : mainInstance.robot) {
    //
    //                    System.err.println(robot);
    //                }
    //                mainInstance.mapInfo.availableGoods().forEach(System.err::println);
    //                for (Berth berth : mainInstance.berth) {
    //                    System.err.println(berth);
    //                }
//                    for (Boat boat : mainInstance.boat) {
//                        System.err.println(boat);
//                    }
//                    System.err.flush();
                }
                if ((frame-1)%500==0){
                    logger.info("try to ship");
                    for (int i = 0; i < mainInstance.boat.length; i++) {

                        Future submit = mainInstance.boatExecutor.submit(new BoatCallable(mainInstance.boat[i], mainInstance.mapInfo, frame));
                        mainInstance.boatFuture[i]=submit;
                    }
                }
                for (int i = 0; i < mainInstance.robot.length; i++) {
                    Robot robot = mainInstance.robot[i];
                    if(robot.id()==0||robot.id()==9){
                        Future<Robot> submit = mainInstance.robotExecutor.submit(new RobotCallable(mainInstance.robot[i], mainInstance.mapInfo, frame));
                        mainInstance.robotFuture[i]=submit;
                    }
                }
                for (int i = 0; i < mainInstance.robot.length; i++) {
                    if (mainInstance.robotFuture[i]==null) {
                        continue;
                    }
                    mainInstance.robotFuture[i].get();
                    logger.info("[frame:"+ id+" ]Robot "+i+ "completed");
                }
                for (int i = 0; i < mainInstance.boat.length; i++) {
                    mainInstance.boatFuture[i].get();
                    logger.info("[frame:"+ id+" ]Boat "+i+ "completed");
                }
                System.out.println("OK");
                MessageCenter.reset();
                System.out.flush();
                System.err.flush();

            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mainInstance.robotExecutor.shutdown();
            mainInstance.boatExecutor.shutdown();

            System.exit(0);
        }


    }


}
