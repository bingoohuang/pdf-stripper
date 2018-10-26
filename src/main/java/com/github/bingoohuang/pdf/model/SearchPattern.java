package com.github.bingoohuang.pdf.model;

import lombok.Data;

@Data
public class SearchPattern {
    private String pattern;
    private String startAnchor;
    private String endAnchor;
    private int nameIndex;
    private int descIndex;
    private int valueIndex;
    private boolean temp;
}
