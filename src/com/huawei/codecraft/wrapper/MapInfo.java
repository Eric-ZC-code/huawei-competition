package com.huawei.codecraft.wrapper;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.enums.BerthStrategy;
import com.huawei.codecraft.enums.GoodStrategy;
import com.huawei.codecraft.util.Pair;
import com.huawei.codecraft.util.Position;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class MapInfo {
    protected final ReadWriteLock goodRWLock = new ReentrantReadWriteLock();
    protected final ReadWriteLock berthRWLock = new ReentrantReadWriteLock();
    protected LinkedHashMap<Position,Good> availableGoodsMap = new LinkedHashMap<>(100);
    protected char[][] map = new char[200][200];
    protected Berth[] berths = new Berth[10];
    protected Robot[] robots = null;
    protected HashMap<Position,Robot> goingPoint = new HashMap<>(20);

    public LinkedHashMap<Position, Good> availableGoods() {
        return availableGoodsMap;
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

        for (int i = 0; i < berths.length; i++) {
            this.berths[i] = berths[i];
        }
        return this;
    }

    public void addGood(Good good) {
        goodRWLock.writeLock().lock();

        try {
            availableGoodsMap.put(good.pair(),good);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            goodRWLock.writeLock().unlock();
        }
    }

    public void addGood(int x, int y, int value) {
        goodRWLock.writeLock().lock();
        try {
            availableGoodsMap.put(new Position(x,y),new Good(x, y, value));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            goodRWLock.writeLock().unlock();
        }
    }
    abstract public Good findBestGood(Robot robot, GoodStrategy strategy);
    abstract public Berth findBestBerth(int x, int y, Set<Berth> blackList, BerthStrategy strategy);
    abstract public List<Command> getFullPath(Robot robot, Good good, Berth berth);
    abstract public List<Command> getFullPath(Robot robot);
    abstract public List<Command> getRobotToGoodPath(Robot robot, Good good);
    abstract public Pair<Good, List<Command>> getGoodAndPath(Robot robot);
    abstract public List<Command> getGoodToBerthPath(Good good, Berth berth, Robot robot);
    abstract public List<Command> getRobotToBerthPath(Robot robot, Berth berth);
    abstract public Command getGood(Robot robot, Good good);
    abstract public Command pullGood(Robot robot, Good good, Berth berth);
    abstract public Integer getAvailableBerth();
    abstract public void setBerthFree(int id);
    abstract public void addItem(int x, int y, char c);
    abstract public Berth currentBerth(int x, int y);
    abstract public boolean acquirePoint(Position pos, Robot robot);
    abstract public Robot getPositionInfo(Position pos);
    abstract public boolean pointIsAvailable(Position position);
    abstract public boolean removePoint(Position position);
    abstract public boolean cleanPoints();
    abstract public List<Command> circumventionCommand(Position curPos);
    abstract public boolean isObstacle(int x, int y);
}
