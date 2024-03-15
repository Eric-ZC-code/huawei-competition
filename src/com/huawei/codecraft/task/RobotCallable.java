package com.huawei.codecraft.task;

import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.wrapper.GoodsInfo;

import java.util.List;
import java.util.concurrent.Callable;

public class RobotCallable implements Callable {
    private Robot robot;
    private GoodsInfo goodsInfo;

    public RobotCallable(Robot robot, GoodsInfo goodsInfo) {
        this.robot = robot;
        this.goodsInfo = goodsInfo;
    }

    @Override
    public Object call() throws Exception {

        synchronized (robot){
//            System.err.println("Robot "+robot.id()+" is running");
//            System.err.println("Robot "+robot.id()+" contains command: "+robot.containsCommand());
            if (!robot.containsCommand()) {
                // 目前机器人没有被分配任务,并且任务缓存中没有堆积的任务
                // 则去搜索最近的货物，然后规划路径
                // 只有等待任务分配完成后才能开始执行。
                Good nearestGood = goodsInfo.findNearestGood(robot);
//                System.err.println("Robot "+robot.id()+" generating tasks");
                if (nearestGood != null) {
                    List<Command> path = goodsInfo.getPath(robot, nearestGood);
                    robot.setCurrentCommand(path);
                }
            }else {
                // 有任务则执行任务
//                System.err.println("Robot "+robot.id()+" executing tasks");
                robot.executeAll();
            }

            return null;

        }


    }
}
