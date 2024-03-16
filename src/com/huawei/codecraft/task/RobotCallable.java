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
    private Integer frame;
    private final Robot robot;
    private final MapInfo mapInfo;
    public RobotCallable(Robot robot, MapInfo mapInfo,Integer frame) {
        this.robot = robot;
        this.mapInfo = mapInfo;
        this.frame = frame;
    }

    @Override
    public Object call() throws Exception {

        synchronized (robot){
            if(robot.flags().get(frame)){
                // 该帧已经被处理过了
                logger.info("Processed, then skip this frame");
                return null;
            }
            else {
                robot.flags().put(frame,true);
            }

            if (!robot.containsCommand()||robot.status()==0) {
                // 目前机器人没有被分配任务或者发生碰撞
                // 则去搜索最近的货物，然后规划路径
                // 只有等待任务分配完成后才能开始执行。
                Good nearestGood = mapInfo.findBestGood(robot);
                if (nearestGood == null) {
                    return null;
                }
                Berth nearestBerth = mapInfo.findBestBerth(nearestGood);
                if (nearestGood != null && nearestBerth != null) {
                    List<Command> path = mapInfo.getFullPath(robot, nearestGood, nearestBerth);
                    robot.setCurrentCommand(path);
                    logger.info("Robot"+robot.id()+"task: "+path);
                }
            }else {
                // 有任务则执行任务
                logger.info( "Robot "+robot.id()+" executing tasks");
                robot.executeAll();
            }

        }
        return null;


    }
}
