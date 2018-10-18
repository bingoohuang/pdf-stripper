package com.github.bingoohuang.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.IOException;


public class Demo {
    private static void extractText(PDDocument doc) throws IOException {
        for (PDPage page : doc.getPages()) {
            PDRectangle pdr = page.getCropBox();
            Rectangle rec = new Rectangle();
            rec.setBounds(
                    Math.round(pdr.getLowerLeftX())
                    , Math.round(pdr.getLowerLeftY())
                    , Math.round(pdr.getWidth())
                    , Math.round(pdr.getHeight()));
            System.out.println("Crobox: " + rec);
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.addRegion("cropbox", rec);
            stripper.setSortByPosition(true);
            stripper.extractRegions(page);
            java.util.List<String> regions = stripper.getRegions();
            for (String region : regions) {
                String text = stripper.getTextForRegion(region);
                System.out.println(text);
            }
        }
    }


}
