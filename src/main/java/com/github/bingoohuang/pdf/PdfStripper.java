package com.github.bingoohuang.pdf;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.InputStream;

public class PdfStripper {
    static {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
    }

    @SneakyThrows
    public static String stripText(InputStream is) {
        @Cleanup val doc = PDDocument.load(is);
        return new PDFLayoutTextStripper().getText(doc);
    }
}
