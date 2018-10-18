package org.fit.pdfdom;

import com.github.bingoohuang.pdf.PdfRect;
import lombok.Data;


@Data
public class PathSegment {
    private float x1, y1, x2, y2;
    private PdfRect pdfRect;

    public PathSegment(float x1, float y1, float x2, float y2, PdfRect pdfRect) {
        this(x1, y1, x2, y2);
        this.pdfRect = pdfRect;
    }

    public PathSegment(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
}
