package com.huawei.codecraft.util;

import java.util.Objects;

public class Position {
    private int x, y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }
    public static Position of(int x, int y){
        return new Position(x,y);
    }

    public Position setX(int x) {
        this.x = x;
        return this;
    }

    public int y() {
        return y;
    }

    public Position setY(int y) {
        this.y = y;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
