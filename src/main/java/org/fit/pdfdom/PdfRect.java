package org.fit.pdfdom;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data @RequiredArgsConstructor
public class PdfRect {
    private final float x, y, width, height;
    private String fcolor;
}
