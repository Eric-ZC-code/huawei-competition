package com.huawei.codecraft.enums;

import com.sun.xml.internal.ws.util.StringUtils;

public enum Command {
    MOVE("move"),
    MOVE_RIGHT("move", 0),
    MOVE_LEFT("move", 1),
    MOVE_UP("move", 2),
    MOVE_DOWN("move", 3),
    GET("get"),
    PULL("pull"),
    SHIP("ship"),
    GO("go"),
    ;
    private String command;
    private Integer actorId;
    private Integer para2;

    Command(String command, int actorId, int para2) {
        this.command = command;
        this.actorId = actorId;
        this.para2 = para2;
    }

    Command(String command, int para2) {
        this.command = command;
        this.para2 = para2;
    }

    Command(String command) {
        this.command = command;
    }

    public Command setCommand(String command) {
        this.command = command;
        return this;
    }

    public Command setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public Command setPara2(int para2) {
        this.para2 = para2;
        return this;
    }

    @Override
    public String toString() {
        String s = this.command + " ";
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
        System.out.println(Command.MOVE_LEFT);
        System.out.println(Command.MOVE_RIGHT.setActorId(2));
        System.out.println(Command.GET.setActorId(3));

    }

}
