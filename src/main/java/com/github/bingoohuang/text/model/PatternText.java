package com.github.bingoohuang.text.model;

import com.github.bingoohuang.text.AnchorAware;
import lombok.Data;

@Data
public class PatternText implements AnchorAware, FiltersAware {
    private String pattern;
    private int index;
    private String startAnchor;
    private String endAnchor;
    private String name;
    private boolean temp;
    private int valueIndex;

    private String nameFilters;
    private String valueFilters;
}
