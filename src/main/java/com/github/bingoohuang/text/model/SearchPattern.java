package com.github.bingoohuang.text.model;

import com.github.bingoohuang.text.AnchorAware;
import lombok.Data;

@Data
public class SearchPattern implements AnchorAware, FiltersAware {
    private String pattern;
    private String startAnchor;
    private String endAnchor;
    private int nameIndex;
    private int descIndex;
    private int valueIndex;
    private boolean temp;

    private String nameFilters;
    private String valueFilters;

    private String nameMatchers;
}