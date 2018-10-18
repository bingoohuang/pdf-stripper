package com.github.bingoohuang.pdf;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.output.NullWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.fit.pdfdom.PDFDomTree;
import org.junit.Ignore;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Demo {
    @Test @Ignore
    @SneakyThrows
    public void test() {
        @Cleanup val is = Util.loadClassPathRes("hanergy.pdf");
        @Cleanup val doc = PDDocument.load(is);

        Pattern itemScorePattern = Pattern.compile("\\b([^\\s\\d]+)[\\s\\\r\\n]+(\\d?\\d)\\b");
        val pdfTextStripper = new PDFLayoutTextStripper() {
            @Override public void processPage(PDPage page) throws IOException {
                if (getCurrentPageNo() == 1) {
                    super.processPage(page);
                }
            }
        };
        val string = pdfTextStripper.getText(doc);
        Matcher matcher = itemScorePattern.matcher(string);
        StringBuilder scores = new StringBuilder();
        while (matcher.find()) {
            scores.append(matcher.group(1) + ":" + matcher.group(2) + "\n");
        }
        System.out.println(scores);

        // 输出第2页
        PDFDomTree parser = new PDFDomTree();
//        Writer output = new PrintWriter("hanergy.html", "utf-8");
        Writer output = new NullWriter();
        parser.writeText(doc, output);
        output.close();

        String itemScores = parser.getItemScores();
        System.out.println(itemScores);
    }


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
