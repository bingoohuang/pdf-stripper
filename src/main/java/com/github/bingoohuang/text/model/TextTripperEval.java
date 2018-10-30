package com.github.bingoohuang.text.model;

import lombok.Data;

@Data
public class TextTripperEval implements FiltersAware {
    private String condition;
    private String expr;
    private String name;

    private String valueFilters;
}
