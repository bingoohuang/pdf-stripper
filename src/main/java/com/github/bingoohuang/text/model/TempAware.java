package com.github.bingoohuang.text.model;

public interface TempAware {
    default String getTemp() {
        return "";
    }
}
