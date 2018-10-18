package com.github.bingoohuang.pdf;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.output.NullWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.fit.pdfdom.PDFDomTree;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

public class PdfStripper {
    static {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
    }

    /**
     * 自定义提取。
     *
     * @param is          PDF输入流
     * @param pageSelect  指定页面
     * @param pdfListener 监听器
     */
    @SneakyThrows
    public static void stripCustom(InputStream is, PdfPageSelect pageSelect, PdfListener pdfListener) {
        @Cleanup val original = PDDocument.load(is);
        @Cleanup val doc = pageSelect.getPDDocument(original);

        val tree = new PDFDomTree();
        tree.setPdfListener(pdfListener);

        Writer output = pageSelect.isDebug()
                ? new PrintWriter("debug" + System.currentTimeMillis() + ".html", "utf-8")
                : new NullWriter();
        tree.writeText(doc, output);
        output.close();
    }

    /**
     * 从PDF中提取布局好的文本。
     *
     * @param is         PDF输入流
     * @param pageSelect 指定页面
     * @return 布局好的文本
     */
    @SneakyThrows
    public static String stripText(InputStream is, PdfPageSelect pageSelect) {
        @Cleanup val original = PDDocument.load(is);
        @Cleanup val doc = pageSelect.getPDDocument(original);

        return new PDFLayoutTextStripper().getText(doc);
    }


    /**
     * 提取图片。
     *
     * @param is           PDF输入流
     * @param pageIndex    指定页面索引
     * @param imageIndices 图片序号
     * @return 图片列表
     */
    @SneakyThrows
    public static List<PdfImage> stripImages(InputStream is, int pageIndex, int... imageIndices) {
        @Cleanup val doc = PDDocument.load(is);
        val page = doc.getPage(pageIndex);

        val images = Lists.<PdfImage>newArrayList();

        val resources = page.getResources();
        int imageIndex = 0;
        for (val xobj : resources.getXObjectNames()) {
            if (resources.isImageXObject(xobj)) {
                val img = (PDImageXObject) resources.getXObject(xobj);
                if (imageIndices.length > 0 && !Ints.contains(imageIndices, imageIndex++)) continue;

                images.add(new PdfImage(img.getImage(), xobj.getName(), img.getSuffix(), img.getHeight(), img.getWidth()));
            }
        }

        return images;
    }
}
