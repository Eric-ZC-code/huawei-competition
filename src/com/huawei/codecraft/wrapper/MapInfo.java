package com.huawei.codecraft.wrapper;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.enums.GoodStrategy;
import com.huawei.codecraft.util.Pair;
import sun.awt.image.ImageWatched;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class MapInfo {
    protected final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    protected HashMap<Pair,Good> availableGoods = new LinkedHashMap<>();
    protected HashMap<Pair,Good> acquiredGoods = new LinkedHashMap<>();
    protected char[][] map = new char[200][200];
    protected Berth[] berths = new Berth[5];
    protected Robot[] robots = null;

    public HashMap<Pair, Good> availableGoods() {
        return availableGoods;
    }

    public MapInfo setAvailableGoods(HashMap<Pair, Good> availableGoods) {
        this.availableGoods = availableGoods;
        return this;
    }

    public HashMap<Pair, Good> acquiredGoods() {
        return acquiredGoods;
    }

    public MapInfo setAcquiredGoods(HashMap<Pair, Good> acquiredGoods) {
        this.acquiredGoods = acquiredGoods;
        return this;
    }

    public Robot[] robots() {
        return robots;
    }

    public MapInfo setRobots(Robot[] robots) {
        this.robots = robots;
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
//        PriorityQueue<Berth> queue = new PriorityQueue<>(new Comparator<Berth>() {
//            @Override
//            public int compare(Berth b1, Berth b2) {
//                if (b1.loadingSpeed() < b2.loadingSpeed()) {
//                    return -1;
//                } else if (b1.loadingSpeed() > b2.loadingSpeed()) {
//                    return 1;
//                } else {
//                    return 0;
//                }
//            }
//        });
        for (int i = 0; i < berths.length; i++) {
            if (i%2 == 0){
                this.berths[i/2] = berths[i];
            }
        }
        return this;
    }

    public void addGood(Good good) {
        rwLock.writeLock().lock();
        try {
            availableGoods.put(new Pair(good.x(), good.y()), good);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void addGood(int x, int y, int value) {
        rwLock.writeLock().lock();
        try {
            Good newGood = new Good(x, y, value);
            availableGoods.put(new Pair(x, y), newGood);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    abstract public Good findBestGood(Robot robot, GoodStrategy strategy);
    abstract public Berth findBestBerth(int x, int y);
    abstract public List<Command> getFullPath(Robot robot, Good good, Berth berth);
    abstract public List<Command> getFullPath(Robot robot);
    abstract public List<Command> getRobotToGoodPath(Robot robot, Good good);
    abstract public HashMap<Good, List<Command>> getGoodAndPath(Robot robot);
    abstract public List<Command> getGoodToBerthPath(Good good, Berth berth, Robot robot);
    abstract public List<Command> getRobotToBerthPath(Robot robot, Berth berth);
    abstract public Command getGood(Robot robot, Good good);
    abstract public Command pullGood(Robot robot, Good good, Berth berth);
    abstract public Integer getAvailableBerth();
    abstract public void addItem(int x, int y, char c);
    abstract public Berth currentBerth(int x, int y);
}
