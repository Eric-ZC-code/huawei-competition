package com.huawei.codecraft.entities;


import com.huawei.codecraft.util.MessageCenter;
import com.huawei.codecraft.util.MyLogger;
import com.huawei.codecraft.wrapper.MapInfo;

import java.util.*;


public class Robot {
    private static final MyLogger logger = MyLogger.getLogger("Robot");
    private final int yieldDistance = 5;
    private int id;
    private int x, y, carrying;
    private int status;
    private boolean shouldCarry = false;

    private Integer priority; // 优先级 0-9 0最高 9最低
    private final Map<Integer,Boolean> flags = new HashMap<>(); //判断这一帧是否做过事情了，一帧只做一件事
    private ArrayDeque<Command> currentCommand = new ArrayDeque<>();

    public Robot() {
        init();
    }

    public Robot(int startX, int startY,Integer priority) {
        this.x = startX;
        this.y = startY;
        this.priority = priority;
        init();
        
    }

    public Integer priority() {
        return priority;
    }

    public Robot setPriority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public  Map<Integer, Boolean> flags() {
        return flags;
    }

    public void init(){
        for (int i = 1; i <= 15000; i++) {
            flags.put(i,false);
        }
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
        if(nearby!=null){

            int i = rand.nextInt(10);
            if(i%2==0){
                logger.info("Robot" + id + " has robot nearby, skip this command by random");
                return;
            }
        }
        while (containsCommand()){
            Command command = popCommand();
            if (command.cmd().equals("move")) {
                if (moved) {
                    logger.info("Robot" + id + " moved, skip this command");
                    this.currentCommand.addFirst(command);
                    break;

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
                    shouldCarry = false;

                } else if (command.cmd().equals("move")) {
                    map.map()[x][y] = '.';
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
