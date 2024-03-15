package com.huawei.codecraft.entities;

public class Boat {
    private int num;
    private int pos;
    private int status;
    private int capacity;

    public Boat(int capacity) {
        this.capacity = capacity;
    }

    public int capacity() {
        return capacity;
    }

    public Boat setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public int num() {
        return num;
    }

    public int pos() {
        return pos;
    }

    public Boat setPos(int pos) {
        this.pos = pos;
        return this;
    }

    public int status() {
        return status;
    }

    public Boat setStatus(int status) {
        this.status = status;
        return this;
    }

    public Boat setNum(int num) {
        this.num = num;
        return this;
    }
}
