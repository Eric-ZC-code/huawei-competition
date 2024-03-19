package com.huawei.codecraft.entities;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Berth {
    private int id;
    private int x;
    private int y;
    private int transportTime; // 运输时间 1-2000
    private int loadingSpeed; // 装卸货速度 1-5
    private int amount = 0; // 当前货物数量
    private boolean acquired = false;
    private Boat boat = null;
    private int priority; // 优先级 越大越优先
    private static final int MAX_TRANSPORT_TIME = 2000;
    private static final int MAX_LOADING_SPEED = 5;
    public Berth(){}
    public Berth(int id,int x, int y, int transportTime, int loadingSpeed) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.transportTime = transportTime;
        this.loadingSpeed = loadingSpeed;
    }

    public int priority() {
        return priority;
    }

    public Berth initPriority() {
        this.priority = calculatePriority(transportTime, loadingSpeed);
        return this;
    }

    public boolean acquired() {
        return acquired;
    }

    public Berth setAcquired(boolean acquired) {
        this.acquired = acquired;
        return this;
    }

    public Berth setId(int id) {
        this.id = id;
        return this;
    }
    public synchronized int amount() {
        return amount;
    }
    public synchronized void load(int amount) {
        this.amount += amount;
    }
    public synchronized void unload(int amount) {
        this.amount -= amount;
    }

    public int id() {
        return id;
    }

    public int x() {
        return x;
    }

    public Boat boat() {
        return boat;
    }

    public Berth setBoat(Boat boat) {
        this.boat = boat;
        return this;
    }

    public Berth setX(int x) {
        this.x = x;
        return this;
    }

    public int y() {
        return y;
    }

    public Berth setY(int y) {
        this.y = y;
        return this;
    }

    public int transportTime() {
        return transportTime;
    }

    public Berth setTransportTime(int transportTime) {
        this.transportTime = transportTime;
        return this;
    }

    public int loadingSpeed() {
        return loadingSpeed;
    }

    public Berth setLoadingSpeed(int loadingSpeed) {
        this.loadingSpeed = loadingSpeed;
        return this;
    }

    // 计算优先级
    private int calculatePriority(int transportTime, int loadingSpeed) {
        // 运输时间和装卸货速度的权重
        double transportWeight = 0.9;
        double loadingWeight = 0.1;

        // 运输时间和装卸货速度的归一化
        double normalizedTransportTime = 1.0 - ((double) transportTime / MAX_TRANSPORT_TIME);
        double normalizedLoadingSpeed = (double) loadingSpeed / MAX_LOADING_SPEED;

        // 计算加权平均
        double priority = transportWeight * normalizedTransportTime + loadingWeight * normalizedLoadingSpeed;

        // 返回优先级（取整）
        return (int) (priority * 100); // 乘以100以确保优先级在1到100之间
    }

    @Override
    public String toString() {
        return "Berth{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", transportTime=" + transportTime +
                ", loadingSpeed=" + loadingSpeed +
                ", amount=" + amount +
                ", acquired=" + acquired +
                ", boat=" + boat +
                ", priority=" + priority +
                '}';
    }

}
