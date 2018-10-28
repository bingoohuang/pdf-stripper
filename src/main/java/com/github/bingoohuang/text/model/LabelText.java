package com.github.bingoohuang.text.model;

import com.github.bingoohuang.text.AnchorAware;
import lombok.Data;

@Data
public class LabelText implements AnchorAware, FiltersAware {
    private String label;
    private String name;
    private String startAnchor;
    private String endAnchor;
    private boolean temp;
    private String valueFilters;
}
