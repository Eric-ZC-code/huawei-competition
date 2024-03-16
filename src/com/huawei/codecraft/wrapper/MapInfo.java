package com.huawei.codecraft.wrapper;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class MapInfo {
    protected final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    protected List<Good> availableGoods = new ArrayList<>();
    protected List<Good> acquiredGoods = new ArrayList<>(10);
    protected char[][] map = new char[200][200];
    protected Berth[] berths = new Berth[5];
    protected Robot[] robots = null;

    public List<Good> availableGoods() {
        return availableGoods;
    }

    public Robot[] robots() {
        return robots;
    }

    public MapInfo setRobots(Robot[] robots) {
        this.robots = robots;
        return this;
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
            availableGoods.add(good);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    public void addGood(int x, int y, int value) {
        rwLock.writeLock().lock();
        try {
            availableGoods.add(new Good(x, y, value));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    abstract public Good findBestGood(Robot robot);
    abstract public Berth findBestBerth(Good good);
    abstract public List<Command> getFullPath(Robot robot, Good good, Berth berth);
    abstract public List<Command> getRobotToGoodPath(Robot robot, Good good);
    abstract public List<Command> getGoodToBerthPath(Good good, Berth berth, Robot robot);
    abstract public Command getGood(Robot robot, Good good);
    abstract public Command pullGood(Robot robot, Good good, Berth berth);
    abstract public Integer getAvailableBerth();
    abstract public Integer getMatchedBerth(Integer berthId);
    abstract public void addItem(int x, int y, char c);
}
