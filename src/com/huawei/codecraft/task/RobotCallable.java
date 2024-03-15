package com.huawei.codecraft.task;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.wrapper.MapInfo;
import com.huawei.codecraft.util.MyLogger;

import java.util.List;
import java.util.concurrent.Callable;

public class RobotCallable implements Callable {
    private static final MyLogger logger= MyLogger.getLogger("RobotCallable");
    private final Robot robot;
    private final MapInfo mapInfo;

    public RobotCallable(Robot robot, MapInfo mapInfo) {
        this.robot = robot;
        this.mapInfo = mapInfo;
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
                Good nearestGood = mapInfo.findBestGood(robot);
                Berth nearestBerth = mapInfo.findBestBerth(nearestGood);
                if (nearestGood != null && nearestBerth != null) {
                    List<Command> path = mapInfo.getFullPath(robot, nearestGood, nearestBerth);
                    robot.setCurrentCommand(path);
                }
            }else {
                // 有任务则执行任务
//                System.err.println("Robot "+robot.id()+" executing tasks");
                logger.info( "Robot "+robot.id()+" executing tasks");
                robot.executeAll();
            }

            return null;

        }

    }
}
