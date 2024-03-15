package com.huawei.codecraft.wrapper;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public abstract class GoodsInfo {
    protected PriorityQueue<Good> availableGoods = new PriorityQueue<>(Comparator.reverseOrder());
    protected PriorityQueue<Good> acquiredGoods = new PriorityQueue<>(10, Comparator.reverseOrder());
    protected char[][] map = new char[200][200];

    public GoodsInfo setMap(char[][] map) {
        this.map = map;
        return this;
    }

    public Good getMostValuableGood() {
        return availableGoods.poll();
    }
    public void addGood(Good good) {
        availableGoods.add(good);
    }
    public void addGood(int x, int y, int value) {
        availableGoods.add(new Good(x, y, value));
    }
    abstract public Good findNearestGood(Robot robot);
    abstract public List<Command> getFullPath(Robot robot, Good good, Berth berth);
    abstract public void acquireGood(Robot robot, Good good);
    abstract public void getRobotToGoodPath(Robot robot, Berth berth);
    abstract public void getRobotToBerthPath(Robot robot, Good good);
    abstract public void getGood(Robot robot, Good good);
}
