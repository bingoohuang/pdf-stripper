package com.github.bingoohuang.pdf.model;

import lombok.Data;

@Data
public class PatternText {
    private String pattern;
    private int index;
    private String startAnchor;
    private String endAnchor;
    private String name;
    private boolean temp;
}
