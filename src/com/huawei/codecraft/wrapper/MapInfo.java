package com.huawei.codecraft.wrapper;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public abstract class MapInfo {
    protected List<Good> availableGoods = new ArrayList<>();
    protected List<Good> acquiredGoods = new ArrayList<>(10);
    protected char[][] map = new char[200][200];
    protected Berth[] berths = new Berth[10];

    public List<Good> availableGoods() {
        return availableGoods;
    }


    public List<Good> acquiredGoods() {
        return acquiredGoods;
    }

    public MapInfo setAcquiredGoods(List<Good> acquiredGoods) {
        this.acquiredGoods = acquiredGoods;
        return this;
    }

    public char[][] map() {
        return map;
    }

    public MapInfo setMap(char[][] map) {
        this.map = map;
        return this;
    }

    public Berth[] berths() {
        return berths;
    }

    public MapInfo setBerths(Berth[] berths) {
        this.berths = berths;
        return this;
    }

    public void addGood(Good good) {
        availableGoods.add(good);
    }
    public void addGood(int x, int y, int value) {
        availableGoods.add(new Good(x, y, value));
    }
    abstract public Good findBestGood(Robot robot);
    abstract public Berth findBestBerth(Good good);
    abstract public List<Command> getFullPath(Robot robot, Good good, Berth berth);
    abstract public List<Command> getRobotToGoodPath(Robot robot, Good good);
    abstract public List<Command> getGoodToBerthPath(Good good, Berth berth, Robot robot);
    abstract public Command getGood(Robot robot, Good good);
    abstract public void acquireGood(Robot robot, Good good);
    abstract public Command pullGood(Robot robot, Good good, Berth berth);
    abstract public Integer getAvailableBerth();

}
