package com.huawei.codecraft.test;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.wrapper.GoodsInfo;
import com.huawei.codecraft.wrapper.impl.GoodsInfoimpl;

import java.util.List;
import java.util.Scanner;


public class Test {
    public static void main(String[] args) {
        char[][] map = {
                {'#', '#', '#', '#', '#', '#', '#'},
                {'#', '.', '#', '.', '.', '.', '#'},
                {'#', '.', '#', '.', '#', '.', '#'},
                {'#', '.', '.', '.', '#', '.', '#'},
                {'#', '#', '#', '.', '#', '.', '#'},
                {'#', '.', '.', '.', '.', '.', '#'},
                {'*', 'B', '*', '*', '*', '*', '*'}
        };
        GoodsInfo GoodsInfoimpl = new GoodsInfoimpl();
        GoodsInfoimpl.setMap(map);
        int startX = 1, startY = 1;
        int endX = 5, endY = 5;
        Robot robot = new Robot(startX, startY);
        Good good = new Good(endX, endY, 10);
        Berth berth = new Berth(6, 1, 1, 1);
        List<Command> path = GoodsInfoimpl.getFullPath(robot, good, berth);
        System.out.println("Path: " + path);
    }


}
