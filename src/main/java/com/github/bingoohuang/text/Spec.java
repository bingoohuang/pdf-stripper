package com.github.bingoohuang.text;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Spec {
    @Getter @Setter private String name;
    @Getter private List<String> params = new ArrayList<String>();

    public void addParam(String param) {
        params.add(param);
    }
}
