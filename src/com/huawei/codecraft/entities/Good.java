package com.huawei.codecraft.entities;


import java.util.Comparator;

public class Good implements Comparator,Comparable{
    private int x;
    private int y;
    private int value;
    private boolean acquired = false;

    public boolean acquired() {
        return acquired;
    }

    public Good setAcquired(boolean acquired) {
        this.acquired = acquired;
        return this;
    }

    public int x() {
        return x;
    }

    public Good setX(int x) {
        this.x = x;
        return this;
    }

    public int y() {
        return y;
    }

    public Good setY(int y) {
        this.y = y;
        return this;
    }

    public int value() {
        return value;
    }

    public Good setValue(int value) {
        this.value = value;
        return this;
    }

    public Good(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    @Override
    public int compare(Object o1, Object o2) {
        Good g1 = (Good) o1;
        Good g2 = (Good) o2;
        if (g1.value > g2.value) {
            return 1;
        } else if (g1.value < g2.value) {
            return -1;
        }
        return 0;
    }

    @Override
    public int compareTo(Object o) {
        Good g = (Good) o;
        if (this.value > g.value) {
            return 1;
        } else if (this.value < g.value) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Good{" +
                "x=" + x +
                ", y=" + y +
                ", value=" + value +
                '}';
    }
}
