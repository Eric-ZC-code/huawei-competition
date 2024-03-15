/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.codecraft;


import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Boat;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.task.RobotCallable;
import com.huawei.codecraft.util.MessageCenter;
import com.huawei.codecraft.util.MyLogger;
import com.huawei.codecraft.wrapper.MapInfo;
import com.huawei.codecraft.wrapper.impl.MapInfoimpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

    private HashMap<Integer,Integer> robotFrameRec = new HashMap<>();
    private int money, boatCapacity, id;
    private char[][] ch = new char[n][n];
    private MapInfo mapInfo = new MapInfoimpl();
    private Robot[] robot = new Robot[robotNum];
    private Berth[] berth = new Berth[berthNum];
    private Boat[] boat = new Boat[5];

    private ExecutorService robotExecutor = Executors.newFixedThreadPool(10);

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
                     .setLoadingSpeed(scanf.nextInt());
        }
        this.boatCapacity = scanf.nextInt();
        for (int i = 0; i < 5; i++) {
            boat[i] = new Boat(boatCapacity);
        }
        // init robots
        for (int i = 0; i < robotNum; i++) {
            robot[i] = new Robot();
            robotFrameRec.put(i,0);
        }
        // init mapInfo
        mapInfo.setMap(ch);
        mapInfo.setBerths(berth);
        String okk = scanf.nextLine();
        System.out.println("OK");
        System.out.flush();
    }
    private int input() {
        Scanner scanf = new Scanner(System.in);
        this.id = scanf.nextInt();
        this.money = scanf.nextInt();
        int num = scanf.nextInt();
        for (int i = 1; i <= num; i++) {
            int x = scanf.nextInt();
            int y = scanf.nextInt();
            int val = scanf.nextInt();
            try {
                mapInfo.addGood(x, y, val);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
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
                    .setPos(scanf.nextInt());
        }
        String okk = scanf.nextLine();
        return id;
    }

    public static void main(String[] args) {

        Main mainInstance = new Main();

        mainInstance.init();


        for (int frame = 1; frame <= 15000; frame++) {
            int id = mainInstance.input();
            if (frame == 1) {
                for (Robot robot : mainInstance.robot) {

                    System.err.println(robot);
                }
                mainInstance.mapInfo.availableGoods().forEach(System.err::println);
                for (Berth berth : mainInstance.berth) {
                    System.err.println(berth);
                }
                System.err.flush();
            }

            for (Robot robot : mainInstance.robot) {
                HashMap<Integer, Integer> record = mainInstance.robotFrameRec;
                if (record.get(robot.id())<frame){

                    Future future = mainInstance.robotExecutor.submit(new RobotCallable(robot, mainInstance.mapInfo, frame));
                    final int passFrame = frame;
                    record.put(robot.id(), passFrame);
//                    CompletableFuture.supplyAsync(()-> {
//                        try {
//                            return new RobotCallable(robot, mainInstance.mapInfo, passFrame).call();
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }).thenAccept((result)->{
//
//                        synchronized (record) { // 确保线程安全
//                            record.put(robot.id(), passFrame);
//                        }
//                    });

                }

            }
            System.out.println("OK");
            MessageCenter.reset();
            System.out.flush();
            System.err.flush();
        }
        mainInstance.robotExecutor.shutdown();

    }


}
