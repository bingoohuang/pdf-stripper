package com.github.bingoohuang.pdf;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.awt.image.BufferedImage;

@Value @AllArgsConstructor
public class PdfImage {
    private final BufferedImage image;
    private final String name;
    private final String suffix;
    private final int height;
    private final int width;
}
