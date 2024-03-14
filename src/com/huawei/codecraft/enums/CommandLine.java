package com.huawei.codecraft.enums;

public enum CommandLine {
    MOVE("move"),
    GET("get"),
    PULL("pull"),
    SHIP("ship"),
    GO("go"),
    ;
    private String command;

    CommandLine(String command) {
        this.command = command;
    }

    public String command() {
        return command;
    }

}
