package com.github.bingoohuang.pdf;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.InputStream;

public class Util {
    public static InputStream loadClassPathRes(String classpath) {
        return Util.class.getClassLoader().getResourceAsStream(classpath);
    }

    @SneakyThrows
    public static void saveImage(PdfStripper.PdfImage pdfImage) {
        ImageIO.write(pdfImage.getImage(), pdfImage.getSuffix(), new File(pdfImage.getName() + "." + pdfImage.getSuffix()));
    }

    @SneakyThrows
    private static void writeFile(String text, String fileName) {
        File file = new File(fileName);
        CharSink sink = Files.asCharSink(file, Charsets.UTF_8);
        sink.write(text);
    }
}
