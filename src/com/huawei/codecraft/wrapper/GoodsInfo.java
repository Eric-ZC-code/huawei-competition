package com.huawei.codecraft.wrapper;

import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.enums.Command;

import java.util.List;
import java.util.PriorityQueue;

public abstract class GoodsInfo {
    private PriorityQueue<Good> availableGoods = new PriorityQueue<>();
    private PriorityQueue<Good> acquiredGoods = new PriorityQueue<>(10);

    public Good getMostValuableGood() {
        return availableGoods.poll();
    }
    public void addGood(Good good) {
        availableGoods.add(good);
    }
    public void addGood(int x, int y, int value) {
        availableGoods.add(new Good(x, y, value));
    }
    abstract public Good findNearestGood(Robot robot);
    abstract public List<Command> getPath(Robot robot, Good good);
    abstract public void acquireGood(Robot robot, Good good);
}
