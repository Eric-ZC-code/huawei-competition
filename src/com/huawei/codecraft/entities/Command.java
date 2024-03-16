package com.huawei.codecraft.entities;

public class Command {
    private String cmd;
    private Integer actorId;
    private Integer para2;

    public Command(String cmd, Integer actorId, Integer para2) {
        this.cmd = cmd;
        this.actorId = actorId;
        this.para2 = para2;
    }

    public static Command move(Integer actorId, Integer dir){
        return new Command("move",actorId,dir);
    }
    public static Command get(Integer actorId){
        return new Command("get",actorId,null);
    }
    public static Command pull(Integer actorId){
        return new Command("pull",actorId,null);
    }
    public static Command ship(Integer actorId, Integer para2 ){
        return new Command("ship",actorId,para2);
    }
    public static Command go(Integer actorId){
        return new Command("go",actorId,null);
    }
    public static Command ignore(){
        return new Command("ignore",null,null);
    }
    public boolean isBoatCmd(){
        return this.cmd.equals("ship")|| this.cmd.equals("go");
    }

    public String cmd() {
        return cmd;
    }

    public Command setCmd(String cmd) {
        this.cmd = cmd;
        return this;
    }

    public Integer actorId() {
        return actorId;
    }

    public Command setActorId(Integer actorId) {
        this.actorId = actorId;
        return this;
    }

    public Integer para2() {
        return para2;
    }

    public Command setPara2(Integer para2) {
        this.para2 = para2;
        return this;
    }

    @Override
    public String toString() {
        String s = this.cmd + " ";
        if (this.actorId== null) {
            return "ignore";
        }
        s += (this.actorId+" ");
        if (this.para2 != null) {
            s += this.para2;
        }
        return s;
    }

    public static void main(String[] args) {
        System.out.println(Command.move(1, 2));
        System.out.println(Command.get(1));
        System.out.println(Command.pull(1));
        System.out.println(Command.ship(1, 2));
        System.out.println(Command.go(1));
    }
}
