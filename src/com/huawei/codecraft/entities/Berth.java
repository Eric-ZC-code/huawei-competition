package com.huawei.codecraft.entities;

public class Berth {
    private int x;
    private int y;
    private int transport_time;
    private int loading_speed;
    public Berth(){}
    public Berth(int x, int y, int transport_time, int loading_speed) {
        this.x = x;
        this.y = y;
        this.transport_time = transport_time;
        this.loading_speed = loading_speed;
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

    public int transport_time() {
        return transport_time;
    }

    public Berth setTransport_time(int transport_time) {
        this.transport_time = transport_time;
        return this;
    }

    public int loading_speed() {
        return loading_speed;
    }

    public Berth setLoading_speed(int loading_speed) {
        this.loading_speed = loading_speed;
        return this;
    }
}
