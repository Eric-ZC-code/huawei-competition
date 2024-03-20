package com.huawei.codecraft.entities;


import com.huawei.codecraft.util.MessageCenter;
import com.huawei.codecraft.util.MyLogger;
import com.huawei.codecraft.util.Position;
import com.huawei.codecraft.wrapper.MapInfo;

import java.util.*;


public class Robot {
    private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final int yieldDistance = 4;
    public static final Random rand = new Random();
    private int id;
    private int x, y, carrying;
    private int status;
    private boolean shouldCarry = false;
    private Integer priority; // 优先级 0-9 0最高 9最低
    private ArrayDeque<Command> currentCommand = new ArrayDeque<>();

    public Robot() {
    }

    public Robot(int startX, int startY,Integer priority) {
        this.x = startX;
        this.y = startY;
        this.priority = priority;
    }
    public Integer priority() {
        return priority;
    }

    public Robot setPriority(Integer priority) {
        this.priority = priority;
        return this;
    }
    public boolean containsCommand(){
        return !currentCommand.isEmpty();
    }

    public ArrayDeque<Command> currentCommand() {
        return currentCommand;
    }
    public void fillCommand(List<Command> commands){
        commands.forEach(this.currentCommand::add);
    }
    public Command popCommand(){
        return currentCommand.pop();
    }

    public int x() {
        return x;
    }
    public Position position(){
        return Position.of(x,y);
    }

    public Robot setX(int x) {
        this.x = x;
        return this;
    }

    public int y() {
        return y;
    }

    public Robot setY(int y) {
        this.y = y;
        return this;
    }

    public int id() {
        return id;
    }

    public Robot setId(int id) {
        this.id = id;
        return this;
    }

    public int carrying() {
        return carrying;
    }

    public Robot setCarrying(int carrying) {
        this.carrying = carrying;
        return this;
    }

    public boolean shouldCarry() {
        return shouldCarry;
    }

    public Robot setShouldCarry(boolean shouldCarry) {
        this.shouldCarry = shouldCarry;
        return this;
    }

    public int status() {
        return status;
    }

    public void executeAll(MapInfo map){
        boolean moved = false;
        Robot[] robots = map.robots();
        Random rand = new Random();
        Robot nearby = havingRobotNearby(robots);
        Berth[] berths = map.berths();
        if(nearby!=null){
            //普通的基于priority 避让没有 random效果好
//            int i = rand.nextInt(10);
//            if(i%2==0){
//                logger.info("Robot" + id + " has robot nearby, skip this command by random");
//                return;
////            }
//            clean();
//            return;
        }
        while (containsCommand()){
            Command command = popCommand();
            if (command.cmd().equals("move")) {
                if (moved) {
                    this.currentCommand.addFirst(command);
                    break;

                }else {
                    // 机器人允许move
                    Position position = command.targetPosition(Position.of(x, y));
                    if(!map.acquirePoint(position,this)){
                        //没拿到当前点意味着如果继续走就会撞就yield
                        //这种避让方式没法避让双向的撞击
                        //todo 判断周围信息做出更完善的决策

                        Robot conflictRobot = map.getPositionInfo(position);
                        if(conflictRobot==null){
                            //todo 为什么会运行这段
                            //
                            System.err.println("inconsistent state");
                            System.err.flush();
                            this.currentCommand.addFirst(Command.yield());
                            break;
                        }
                        clean();
                        if(conflictRobot.x()!=x){
                            // x 轴 冲突
                            if(conflictRobot.y()>y){
                                //冲突机器人在右边
                                //则往左避让
                                if(map.isObstacle(x,y-1)){

                                    break;
                                }
                                command = Command.move(id,1);
                                this.currentCommand.addFirst(Command.move(id,0));

                            }
                            else{
                                //冲突机器人在左边或者正上往右避让
                                //往右避让
                                if(map.isObstacle(x,y+1)){

                                    break;
                                }
                                command = Command.move(id,0);
                                this.currentCommand.addFirst(Command.move(id,1));
                            }
                        } else if (conflictRobot.y()!=y) {
                            // y轴冲突
                            if(conflictRobot.x()<x){
                                //冲突机器人在上方
                                //往下避让
                                if(map.isObstacle(x+1,y)){
                                    break;
                                }
                                command = Command.move(id,3);
                                this.currentCommand.addFirst(Command.move(id,2));
                            }
                            else {
                                // 冲突机器人在下方
                                // 往上避让
                                if (map.isObstacle(x - 1, y)) {
                                    break;
                                }
                                command = Command.move(id,2);
                                this.currentCommand.addFirst(Command.move(id,3));
                            }
                        }
//                        this.currentCommand.addFirst(command);
//                        break;
                    }
                }
                moved = true;
            }

            if(!MessageCenter.send(command)){
                this.currentCommand.addFirst(command);
                break;
            }else {
                if(command.cmd().equals("get")){
                    shouldCarry = true;
                } else if (command.cmd().equals("pull")) {
                    try {
                        shouldCarry = false;
                        Berth berth = map.findBestBerth(this.x, this.y);
                        if(berth!=null){
                            berth.load(1);
                        }
                    } catch (Exception e) {
                        System.err.println("Robot error: "+e);
                        e.printStackTrace();
                    }

                } else if (command.cmd().equals("move")) {
                    map.map()[x][y] = '.';
                    map.removePoint(this.position());
                }

            }
        }
    }

    public Robot setStatus(int status) {
        this.status = status;
        return this;
    }
    public void clean(){
        this.currentCommand = new ArrayDeque<>();
    }
    private Robot havingRobotNearby(Robot [] robots){

        for (Robot robot : robots) {
            if(robot.id()==this.id){
                continue;
            }
            int dx = Math.abs(robot.x() - x);
            int dy = Math.abs(robot.y() - y);
            if(dx+dy<yieldDistance){
                return robot;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return "Robot{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

}
