package com.huawei.codecraft.util;

import com.huawei.codecraft.wrapper.impl.MapInfoimpl;

import java.util.Objects;

public class Pair {
    private int x, y;

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public Pair setX(int x) {
        this.x = x;
        return this;
    }

    public int y() {
        return y;
    }

    public Pair setY(int y) {
        this.y = y;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pair pair = (Pair) obj;
        return x == pair.x && y == pair.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
