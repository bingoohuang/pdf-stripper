package com.github.bingoohuang.pdf;

import com.github.bingoohuang.utils.lang.Str;
import com.google.common.collect.Lists;
import lombok.val;
import org.fit.pdfdom.BoxStyle;

import java.util.List;
import java.util.stream.Collectors;

public class HogonPdfListener implements PdfListener {
    private String lastText = null;
    private String preText = null;
    private List<HogonItem> itemScores = Lists.newArrayList();
    private List<PdfRect> pdfRects = Lists.newArrayList();
    private boolean complete = false;

    @Override public void process(PdfRect rect) {
        if (!complete) pdfRects.add(rect);
    }

    @Override public void process(BoxStyle curstyle, String text) {
        if (!complete)
        if (pdfRects.size() == 4) {
            val scope = lastText == null ? "" : lastText;
            val item = (preText == null ? "" : preText) + text;
            val scores = pdfRects.stream().filter(x -> !Str.anyOf(x.getFcolor(), "#e6e7e8", "#e5e6e7")).count();
            itemScores.add(new HogonItem(scope, item, (int) scores));
            preText = null;
            if ("顺从依赖".equals(item)) {
                complete = true;
            }
        } else {
            val fontSize = curstyle.getFontSize();
            if (fontSize >= 9.0f && fontSize <= 10.0f) {
                lastText = text;
            } else if (Math.abs(fontSize - 8) < 0.1) {
                preText = text;
            }
        }

        pdfRects.clear();
    }

    public String itemScores() {
        return itemScores.stream().map(HogonItem::toString).collect(Collectors.joining("\n"));
    }


}
