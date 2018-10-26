package com.github.bingoohuang.pdf.model;

import lombok.Data;

@Data
public class LabelText {
    private String label;
    private String name;
    private String startAnchor;
    private String endAnchor;
    private boolean temp;
    private String options;
}
