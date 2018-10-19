package com.github.bingoohuang.pdf;

import lombok.Value;

@Value
public class PdfRect {
    private final float x, y, width, height;
    private final String fcolor;
}
