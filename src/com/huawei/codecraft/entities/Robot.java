package com.huawei.codecraft.entities;


import com.huawei.codecraft.util.MessageCenter;

import java.util.ArrayDeque;


public class Robot {
    private int id;
    private int x, y, carrying;
    private int status;
    private ArrayDeque<Command> currentCommand = new ArrayDeque<>();

    public Robot() {}

    public Robot(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }
    public boolean containsCommand(){
        return !currentCommand.isEmpty();
    }

    public ArrayDeque<Command> currentCommand() {
        return currentCommand;
    }
    public void addCommand(Command command){
        currentCommand.add(command);
    }
    public Command popCommand(){
        return currentCommand.pop();
    }

    public Robot setCurrentCommand(Iterable<Command> currentCommand) {
        currentCommand.forEach(this::addCommand);
        return this;
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

    public int status() {
        return status;
    }

    public void executeAll(){
        while (containsCommand()){
            Command command = popCommand();
            if(!MessageCenter.send(command)){
                this.currentCommand.addFirst(command);
            }

        }
    }

    public Robot setStatus(int status) {
        this.status = status;
        return this;
    }
}
