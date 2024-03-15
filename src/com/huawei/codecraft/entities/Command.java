package com.huawei.codecraft.entities;

import com.huawei.codecraft.enums.CommandLine;

public class Command {
    private CommandLine commandLine;
    private int para1;
    private int para2;

    public CommandLine commandLine() {
        return commandLine;
    }

    public Command setCommandLine(CommandLine commandLine) {
        this.commandLine = commandLine;
        return this;
    }

    public int para1() {
        return para1;
    }

    public Command setPara1(int para1) {
        this.para1 = para1;
        return this;
    }

    public int para2() {
        return para2;
    }

    public Command setPara2(int para2) {
        this.para2 = para2;
        return this;
    }

    @Override
    public String toString() {
        return commandLine.command() + " " + para1 + " " + para2;
    }
}
