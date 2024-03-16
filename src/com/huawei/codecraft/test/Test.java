package com.huawei.codecraft.test;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.wrapper.MapInfo;
import com.huawei.codecraft.wrapper.impl.MapInfoimpl;

import java.io.*;
import java.util.Arrays;
import java.util.List;


public class Test {
    public static void main(String[] args) throws IOException {
        BufferedReader reader;
        String total = "";
        try {
            reader = new BufferedReader(new FileReader("src/com/huawei/codecraft/test/map1.txt"));
            String line = reader.readLine();
            while (line != null) {
                total += line + "\n";
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

        MapInfo MapInfoimpl = new MapInfoimpl();
        MapInfoimpl.setMap(map);
        int startX = 36, startY = 173;
        int endX = 44, endY = 194;
        Robot robot = new Robot(startX, startY,10);
        Good good = new Good(endX, endY, 10);
        Berth berth = new Berth(1,2, 187, 1, 1);
        List<Command> path = MapInfoimpl.getFullPath(robot, good, berth);
        System.out.println("Path: " + path);
    }


}
