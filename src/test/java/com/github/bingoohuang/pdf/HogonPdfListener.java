package com.github.bingoohuang.pdf;

import com.github.bingoohuang.utils.lang.Str;
import com.google.common.collect.Lists;
import org.fit.pdfdom.BoxStyle;

import java.util.List;

public class HogonPdfListener implements PdfListener {
    private String lastText = null;
    private String preText = null;
    private StringBuilder itemScores = new StringBuilder();
    private List<PdfRect> pdfRects = Lists.newArrayList();

    @Override public void process(PdfRect rect) {
        pdfRects.add(rect);
    }

    @Override public void process(BoxStyle curstyle, String text) {
        if (pdfRects.size() == 4) {
            long scores = pdfRects.stream().filter(x -> !Str.anyOf(x.getFcolor(), "#e6e7e8", "#e5e6e7")).count();
            itemScores.append((lastText == null ? "" : (lastText + ".")) + (preText == null ? "" : preText) + text + ":" + scores);
            itemScores.append("\n");
            pdfRects.clear();
            preText = null;
        } else {
            if (curstyle.getFontSize() >= 9.0f && curstyle.getFontSize() <= 10.0f) {
                lastText = text;
            } else if (Math.abs(curstyle.getFontSize() - 8) < 0.1) {
                preText = text;
            }
        }
    }

    public String itemScores() {
        return itemScores.toString();
    }
}
