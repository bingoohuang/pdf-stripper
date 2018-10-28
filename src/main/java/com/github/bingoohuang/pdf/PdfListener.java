package com.github.bingoohuang.pdf;

import org.fit.pdfdom.BoxStyle;

public interface PdfListener {
    default void process(PdfRect rect) {

    }

    default void process(BoxStyle curstyle, String text) {

    }

    default boolean createHtml() {
        return false;
    }
}
