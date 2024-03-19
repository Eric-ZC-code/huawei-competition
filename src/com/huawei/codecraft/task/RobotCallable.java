package com.huawei.codecraft.task;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.enums.GoodStrategy;
import com.huawei.codecraft.wrapper.MapInfo;
import com.huawei.codecraft.util.MyLogger;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class RobotCallable implements Callable {
    private Integer frame;
    private final Robot robot;
    private final MapInfo mapInfo;
    private final GoodStrategy goodStrategy;
    public RobotCallable(Robot robot, MapInfo mapInfo,Integer frame,GoodStrategy goodStrategy) {
        this.robot = robot;
        this.mapInfo = mapInfo;
        this.frame = frame;
        this.goodStrategy= goodStrategy;
    }

    @Override
    public Object call() throws Exception {

        synchronized (robot){
//            long start = System.currentTimeMillis();
            if (!robot.containsCommand()) {
                // 目前机器人没有被分配任务
                // 则去搜索最近的货物，然后规划路径
                // 只有等待任务分配完成后才能开始执行。
                robot.clean();
                if(!setCmd(robot)){
                    return null;
                }
            }else if (robot.status()==0){
                //机器人发生碰撞
                robot.clean();
                if(!setCmd(robot)){
                    return null;
                }

            } else {
                if(robot.carrying()==0&&robot.shouldCarry()==true){
                    robot.clean();
                    robot.setShouldCarry(false);
                    if(!setCmd(robot)){
                        return null;
                    }
                }
                // 有任务则执行任务
//                if(robot.id()==0) System.err.printf("%dms\n",(System.currentTimeMillis()-start));
                robot.executeAll(mapInfo);
            }

        }
        return null;


    }
    public boolean setCmd(Robot robot) {
        if(robot.carrying()==1){

            Berth nearestBerth = mapInfo.findBestBerth(robot.x(), robot.y());
            if(nearestBerth==null){
                return false;
            }
            List<Command> path = mapInfo.getFullPath(robot,null ,nearestBerth);
            robot.fillCommand(path);
            return true;
        }
        else {
//            Good nearestGood = mapInfo.findBestGood(robot, goodStrategy);
//            if(nearestGood==null){
//                return false;
//            }
//
//            Berth nearestBerth = mapInfo.findBestBerth(nearestGood.x(), nearestGood.y());
////        Berth nearestBerth = mapInfo.berths()[robot.id()%mapInfo.berths().length];
//            if (nearestGood != null && nearestBerth != null) {
//                List<Command> path = mapInfo.getFullPath(robot, nearestGood, nearestBerth);
//                robot.fillCommand(path);
//                return true;
//            }
//            return false;

            List<Command> path = mapInfo.getFullPath(robot);
            robot.fillCommand(path);
            return true;

        }


    }
}
