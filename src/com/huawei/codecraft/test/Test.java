package com.huawei.codecraft.test;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.wrapper.GoodsInfo;
import com.huawei.codecraft.wrapper.impl.GoodsInfoimpl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Test {
    public static void main(String[] args) throws IOException {
        BufferedReader reader;
        String total = "";
        try {
            reader = new BufferedReader(new FileReader("src/com/huawei/codecraft/test/map1.txt"));
            String line = reader.readLine();
            while (line != null) {
                total+= line + "\n";
                // 读取下一行
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] s = total.split("\n");
        char[][] map = new char[s.length][s[0].length()];
        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < s[i].length(); j++) {
                map[i][j] = s[i].charAt(j);
            }
        }
        for (char[] chars : map) {
            System.out.println(Arrays.toString(chars));
        }
        System.out.println(map.length);
        System.out.println(map[0].length);
//        GoodsInfo GoodsInfoimpl = new GoodsInfoimpl();
//        GoodsInfoimpl.setMap(map);
//        int startX = 1, startY = 1;
//        int endX = 5, endY = 5;
//        Robot robot = new Robot(startX, startY);
//        Good good = new Good(endX, endY, 10);
//        Berth berth = new Berth(6, 1, 1, 1);
//        List<Command> path = GoodsInfoimpl.getFullPath(robot, good, berth);
//        System.out.println("Path: " + path);
    }


}
