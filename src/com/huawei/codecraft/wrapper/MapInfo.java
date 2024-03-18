package com.huawei.codecraft.wrapper;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.enums.GoodStrategy;
import com.huawei.codecraft.util.Pair;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class MapInfo {
    protected final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    protected LinkedHashMap<Pair,Good> availableGoodsMap = new LinkedHashMap<>(100);
    protected LinkedList<Good> acquiredGoodsMap = new LinkedList<>();
    protected char[][] map = new char[200][200];
    protected Berth[] berths = new Berth[5];
    protected Robot[] robots = null;

    public LinkedHashMap<Pair, Good> availableGoods() {
        return availableGoodsMap;
    }

    public Robot[] robots() {
        return robots;
    }

    public MapInfo setRobots(Robot[] robots) {
        this.robots = robots;
        return this;
    }

    public LinkedList<Good> acquiredGoods() {
        return acquiredGoodsMap;
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
            availableGoodsMap.put(good.pair(),good);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    public void addGood(int x, int y, int value) {
        rwLock.writeLock().lock();
        try {
            availableGoodsMap.put(new Pair(x,y),new Good(x, y, value));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    abstract public Good findBestGood(Robot robot, GoodStrategy strategy);
    abstract public Berth findBestBerth(int x, int y);
    abstract public List<Command> getFullPath(Robot robot, Good good, Berth berth);
    abstract public List<Command> getRobotToGoodPath(Robot robot, Good good);
    abstract public List<Command> getGoodToBerthPath(Good good, Berth berth, Robot robot);
    abstract public List<Command> getRobotToBerthPath(Robot robot, Berth berth);
    abstract public Command getGood(Robot robot, Good good);
    abstract public Command pullGood(Robot robot, Good good, Berth berth);
    abstract public Integer getAvailableBerth();
    abstract public void addItem(int x, int y, char c);
    abstract public Berth currentBerth(int x, int y);
}
