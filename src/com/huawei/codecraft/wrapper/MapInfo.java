package com.huawei.codecraft.wrapper;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public abstract class MapInfo {
    protected PriorityQueue<Good> availableGoods = new PriorityQueue<>(Comparator.reverseOrder());
    protected PriorityQueue<Good> acquiredGoods = new PriorityQueue<>(10, Comparator.reverseOrder());
    protected char[][] map = new char[200][200];
    protected List<Berth> berths = new ArrayList<>();

    public PriorityQueue<Good> availableGoods() {
        return availableGoods;
    }

    public MapInfo setAvailableGoods(PriorityQueue<Good> availableGoods) {
        this.availableGoods = availableGoods;
        return this;
    }

    public PriorityQueue<Good> acquiredGoods() {
        return acquiredGoods;
    }

    public MapInfo setAcquiredGoods(PriorityQueue<Good> acquiredGoods) {
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

    public List<Berth> berths() {
        return berths;
    }

    public MapInfo setBerths(List<Berth> berths) {
        this.berths = berths;
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
    abstract public Good findBestGood(Robot robot);
    abstract public Berth findBestBerth(Good good);
    abstract public List<Command> getFullPath(Robot robot, Good good, Berth berth);
    abstract public List<Command> getRobotToGoodPath(Robot robot, Good good);
    abstract public List<Command> getGoodToBerthPath(Good good, Berth berth);
    abstract public Command getGood(Robot robot, Good good);
    abstract public void acquireGood(Robot robot, Good good);

}
