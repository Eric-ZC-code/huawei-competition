package com.huawei.codecraft.entities;

public class Robot {
    private int x, y, carrying;
    private int status;

    public Robot() {}

    public Robot(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public int x() {
        return x;
    }

    public Robot setX(int x) {
        this.x = x;
        return this;
    }

    public int y() {
        return y;
    }

    public Robot setY(int y) {
        this.y = y;
        return this;
    }

    public int carrying() {
        return carrying;
    }

    public Robot setCarrying(int carrying) {
        this.carrying = carrying;
        return this;
    }

    public int status() {
        return status;
    }

    public Robot setStatus(int status) {
        this.status = status;
        return this;
    }

}
