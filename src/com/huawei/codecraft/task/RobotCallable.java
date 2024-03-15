package com.huawei.codecraft.task;

import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.util.MyLogger;
import com.huawei.codecraft.wrapper.GoodsInfo;

import java.util.List;
import java.util.concurrent.Callable;

public class RobotCallable implements Callable {
    private static final MyLogger logger= MyLogger.getLogger("RobotCallable");
    private Integer frame;
    private final Robot robot;
    private GoodsInfo goodsInfo;
    public RobotCallable(Robot robot, GoodsInfo goodsInfo,Integer frame) {
        this.robot = robot;
        this.goodsInfo = goodsInfo;
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

            if (!robot.containsCommand()) {
                // 目前机器人没有被分配任务,并且任务缓存中没有堆积的任务
                // 则去搜索最近的货物，然后规划路径
                // 只有等待任务分配完成后才能开始执行。
                Good nearestGood = goodsInfo.findNearestGood(robot);

                if (nearestGood != null) {
                    List<Command> path = goodsInfo.getFullPath(robot, nearestGood, null);

                    logger.info("Robot "+robot.id()+" generating tasks: "+path);
                    robot.setCurrentCommand(path);
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
