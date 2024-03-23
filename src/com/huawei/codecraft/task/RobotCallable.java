package com.huawei.codecraft.task;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.enums.BerthStrategy;
import com.huawei.codecraft.enums.GoodStrategy;
import com.huawei.codecraft.wrapper.MapInfo;

import java.util.List;
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
        if (robot.whiteListedSetUp() == false){
            // 初始化白名单
            robot.initWhiteList(mapInfo, 8);
            robot.setWhiteListedSetUp(true);
            return null;
        }

//        if (robot.id() == 5 || robot.id() == 0) {
//            System.err.println("Robot id：" + robot.id() + ": " + robot.berthWhiteList());
//        }

        if(robot.searching()){
            return null;
        }
        boolean b = robot.robotLock().tryLock();
        if(!b){
            return null;
        }
        try {

            // 白名单为空，说明没有货物了
            if(robot.berthWhiteList().isEmpty()){
                return null;
            }
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
                    // 货物消失没有拿到或者其他 inconsistent的状态
                    robot.clean();
                    robot.setShouldCarry(false);
                    if(!setCmd(robot)){
                        return null;
                    }
                }
                // 有任务则执行任务
                robot.executeAll(mapInfo);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            robot.robotLock().unlock();
        }
        return null;


    }
    public boolean setCmd(Robot robot) {
        robot.setSearching(true);
        try {
            if(robot.carrying()==1){

                // 机器人已经携带货物

                Berth nearestBerth = mapInfo.findBestBerth(robot.x(), robot.y(),
                                                           robot.berthWhiteList(), BerthStrategy.MANHANTTAN);
                if(nearestBerth==null){
                    return false;
                }
                // 如果目的地是白名单中的泊位，则规划路径
                if(!robot.berthWhiteList().contains(nearestBerth)||nearestBerth==null){
                    return false;
                }
                List<Command> path = mapInfo.getFullPath(robot,null ,nearestBerth);
                robot.fillCommand(path);
                return true;
            }
            else {
//                Good nearestGood = mapInfo.findBestGood(robot, goodStrategy);
//                if(nearestGood==null){
//                    return false;
//                }
//
//                Berth nearestBerth = mapInfo.findBestBerth(nearestGood.x(), nearestGood.y(),
//                                                           robot.berthBlackList(), BerthStrategy.MANHANTTAN);
//    //        Berth nearestBerth = mapInfo.berths()[robot.id()%mapInfo.berths().length];
//                if (nearestGood != null && nearestBerth != null) {
//                    List<Command> path = mapInfo.getFullPath(robot, nearestGood, nearestBerth);
//                    robot.fillCommand(path);
//                    return true;
//                }
//                return false;

                List<Command> path = mapInfo.getFullPath(robot);
                if (path == null || path.size() == 0){
                    return false;
                }
                robot.fillCommand(path);
                return true;

            }
        } finally {
            robot.setSearching(false);
        }


    }
}
