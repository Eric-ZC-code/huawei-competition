package com.huawei.codecraft.enums;

public enum GoodStrategy {
    MANHATTAN("manhattan"),
    VALUE("value"),
    RATIO("ratio"),


    ;
    private String strategy;

    GoodStrategy(String strategy) {
        this.strategy = strategy;
    }
}
