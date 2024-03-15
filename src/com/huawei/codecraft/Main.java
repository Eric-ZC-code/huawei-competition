/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.codecraft;


import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Boat;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.wrapper.GoodsInfo;
import com.huawei.codecraft.wrapper.impl.GoodsInfoimpl;

import java.util.*;

/**
 * Main
 *
 * @since 2024-02-05
 */
public class Main {
    private static final int n = 200;
    private static final int robot_num = 10;
    private static final int berth_num = 10;
    private static final int N = 210;

    private int money, boat_capacity, id;
    private String[] ch = new String[n];
    private GoodsInfo goodsInfo= new GoodsInfoimpl();
    private Robot[] robot = new Robot[robot_num + 10];
    private Berth[] berth = new Berth[berth_num + 10];
    private Boat[] boat = new Boat[10];

    private void init() {
        Scanner scanf = new Scanner(System.in);
        for (int i = 0; i < n; i++) {
            ch[i] = scanf.nextLine();
        }
        for (int i = 0; i < berth_num; i++) {
            int id = scanf.nextInt();
            berth[id] = new Berth();
            berth[id].setX(scanf.nextInt())
                     .setY(scanf.nextInt())
                     .setTransport_time(scanf.nextInt())
                     .setLoading_speed(scanf.nextInt());
        }
        this.boat_capacity = scanf.nextInt();
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
                goodsInfo.addGood(x, y, val);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
//            goodsInfo.addGood(x, y, val);
        }
        for (int i = 0; i < robot_num; i++) {
            robot[i] = new Robot();
            robot[i].setCarrying(scanf.nextInt())
                    .setX(scanf.nextInt())
                    .setY(scanf.nextInt())
                    .setStatus(scanf.nextInt());
        }
        for (int i = 0; i < 5; i++) {
            boat[i] = new Boat();
            boat[i].setStatus(scanf.nextInt())
                    .setPos(scanf.nextInt());
        }
        String okk = scanf.nextLine();
        return id;
    }

    public static void main(String[] args) {

        Main mainInstance = new Main();

        mainInstance.init();


        for (int zhen = 1; zhen <= 15000; zhen++) {
            int id = mainInstance.input();
            if (zhen == 1) {

                for (String ele : mainInstance.ch) {
                    System.err.println(ele);

                }

            }
            Random rand = new Random();
            for (int i = 0; i < robot_num; i++)
                System.out.printf("move %d %d" + System.lineSeparator(), i, rand.nextInt(4) % 4);
            System.out.println("OK");
            System.out.flush();
            System.err.flush();
        }


    }


}
