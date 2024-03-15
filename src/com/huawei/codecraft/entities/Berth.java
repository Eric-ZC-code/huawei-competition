package com.huawei.codecraft.entities;

public class Berth {
    private int x;
    private int y;
    private int transportTime;
    private int loadingSpeed;
    public Berth(){}
    public Berth(int x, int y, int transportTime, int loadingSpeed) {
        this.x = x;
        this.y = y;
        this.transportTime = transportTime;
        this.loadingSpeed = loadingSpeed;
    }

    public int x() {
        return x;
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

    @Override
    public String toString() {
        return "Berth{" +
                "x=" + x +
                ", y=" + y +
                ", transportTime=" + transportTime +
                ", loadingSpeed=" + loadingSpeed +
                '}';
    }
}
