package com.huawei.codecraft.wrapper.impl;

import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.enums.Command;
import com.huawei.codecraft.wrapper.GoodsInfo;

import java.util.List;

public class GoodsInfoimpl extends GoodsInfo {
    @Override
    public Good findNearestGood(Robot robot) {
        return null;
    }

    @Override
    public List<Command> getPath(Robot robot, Good good) {
        return null;
    }


    /**
     * acquire good synchronously
     * @param robot
     * @param good
     */
    @Override
    public synchronized void acquireGood(Robot robot, Good good) {

    }
}
