package com.github.bingoohuang.pdf;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.output.NullWriter;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.fit.pdfdom.PDFDomTree;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Demo {
    @SneakyThrows
    public static void main(String[] args) {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

        @Cleanup val fis = new FileInputStream("原始报告（样本）/智联/职业行为风险测验（样本）.pdf");
//        @Cleanup val fis = new FileInputStream("原始报告（样本）/智联/情绪管理能力测验（样本）.pdf");
//        @Cleanup val fis = new FileInputStream("原始报告（样本）/智联/职业价值观测验（样本）.pdf");
        @Cleanup val doc = PDDocument.load(fis);
        String text = extractTextInLayout(doc);
        System.out.println(text);
    }

    @SneakyThrows
    public static void main1(String[] args) {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

        @Cleanup val is = Demo.class.getClassLoader().getResourceAsStream("hanergy.pdf");
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


//         val contentStream = new PDPageContentStream(doc, doc.getPage(1));

//        //Drawing a rectangle
//        contentStream.addRect(200, 650, 100, 100);
//        contentStream.setNonStrokingColor(Color.DARK_GRAY);
//        //Drawing a rectangle
//        contentStream.fill();
//        //Closing the ContentStream object
//        contentStream.close();
//        //Saving the document
//        File file1 = new File("colorbox.pdf");
//        doc.save(file1);

//        convertImage(doc.getPage(1));

//        extractTextInLayout(doc);

//        extractText(doc);
//        extractAllText(doc);

//        val catalog = doc.getDocumentCatalog();
//        val metadata = catalog.getMetadata();
//        val xmlInputStream = metadata.createInputStream();
//        Files.copy(xmlInputStream, Paths.get("hanery.xml"));
    }

    private static String extractTextInLayout(PDDocument doc) throws IOException {
        PDFTextStripper pdfTextStripper = new PDFLayoutTextStripper();
        return pdfTextStripper.getText(doc);
//        writeFile(string, "hanergy2.txt");
    }

    private static void extractAllText(PDDocument doc) throws IOException {
        val tStripper = new PDFTextStripper();
        String pdfFileInText = tStripper.getText(doc);

        String fileName = "hanergy.txt";
        writeFile(pdfFileInText, fileName);
        return;
    }

    private static void writeFile(String pdfFileInText, String fileName) throws IOException {
        File file = new File(fileName);
        CharSink sink = Files.asCharSink(file, Charsets.UTF_8);
        sink.write(pdfFileInText);
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

    public static void convertImage(PDPage pdPage) {
        PDResources pdResources = pdPage.getResources();
        Iterable<COSName> iterable = pdResources.getXObjectNames();
        AtomicInteger seq = new AtomicInteger(0);
        iterable.forEach(t -> {
            try {
                System.out.println(t.getName());
                if (pdResources.isImageXObject(t)) {
                    System.out.println("COSName " + t.getName() + " isImageXObject");
                    PDXObject pdXObject = pdResources.getXObject(t);
                    PDImageXObject pdImageXObject = (PDImageXObject) pdXObject;
                    String suffix = pdImageXObject.getSuffix();
                    System.out.println("Height:" + pdImageXObject.getHeight() + "Width:" + pdImageXObject.getWidth() + "Suffix:" + suffix);
                    BufferedImage image = pdImageXObject.getImage();
                    ImageIO.write(image, suffix, new File(seq.incrementAndGet() + "." + suffix));
                } else {
                    System.out.println("COSName " + t.getName() + " isOtherXObject");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        });
    }


}
