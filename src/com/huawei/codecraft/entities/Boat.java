package com.huawei.codecraft.entities;

import com.huawei.codecraft.util.MessageCenter;
import com.huawei.codecraft.util.MyLogger;

import java.util.HashMap;
import java.util.Map;

public class Boat {
    private int num;
    private int pos;
    private int status;
    private int capacity;
    private int id;
    private int goodsNum = 0;
    private static MyLogger logger = MyLogger.getLogger("boat");
    public Boat(int capacity) {
        this.capacity = capacity;
    }

    public int id() {
        return id;
    }

    public Boat setId(int id) {
        this.id = id;
        return this;
    }

    public Boat(int pos, int capacity) {
        this.pos = pos;
        this.capacity = capacity;
    }
    public int capacity() {
        return capacity;
    }

    public void load(int number){
        this.goodsNum+=number;
    }

    public int goodsNum() {
        return goodsNum;
    }
    public boolean isFull(){
        return goodsNum>=capacity;
    }
    public void reset(){
        goodsNum = 0;
    }

    public Boat setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public int num() {
        return num;
    }

    public int pos() {
        return pos;
    }

    public Boat setPos(int pos) {
        this.pos = pos;
        return this;
    }

    public int status() {
        return status;
    }

    public Boat setStatus(int status) {
        this.status = status;
        return this;
    }

    public Boat setNum(int num) {
        this.num = num;
        return this;
    }
    public boolean ship(Integer berthId){
        if(MessageCenter.send(Command.ship(this.id,berthId))){
            this.pos = berthId;
            logger.info("Boat "+this.id+" ship msg sent");
            return true;
        }
        logger.info("Boat"+this.id+" ship msg not sent");
        return false;
    }

    @Override
    public String toString() {
        return "Boat{" +
                "num=" + num +
                ", pos=" + pos +
                ", status=" + status +
                ", capacity=" + capacity +
                '}';
    }

    public boolean go() {

        if(MessageCenter.send(Command.go(this.id))){
            this.pos=-1;

            reset();
            logger.info("Boat "+this.id+" go msg sent");
            return true;
        }
        logger.info("Boat "+this.id+" go msg not sent");
        return false;
    }
}
