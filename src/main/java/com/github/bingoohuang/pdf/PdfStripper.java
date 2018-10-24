package com.github.bingoohuang.pdf;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.output.NullWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

public class PdfStripper {
    static {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
        System.setProperty("org.apache.pdfbox.rendering.UsePureJavaCMYKConversion", "true");
    }

    /**
     * 自定义提取。
     *
     * @param pdDoc       PDF文档对象
     * @param pageSelect  指定页面
     * @param pdfListener 监听器
     */
    @SneakyThrows
    public static void stripCustom(PDDocument pdDoc, PdfPagesSelect pageSelect, PdfListener pdfListener) {
        @Cleanup val doc = pageSelect.getPDDocument(pdDoc);

        val tree = new PdfDom(pdfListener);

        Writer output = pdfListener.createHtml()
                ? new PrintWriter("debug" + System.currentTimeMillis() + ".html", "utf-8")
                : new NullWriter();
        tree.writeText(doc, output);
        output.close();
    }

    /**
     * 从PDF中提取布局好的文本。
     *
     * @param pdDoc      PDF文档对象
     * @param pageSelect 指定页面
     * @return 布局好的文本
     */
    @SneakyThrows
    public static String stripText(PDDocument pdDoc, PdfPagesSelect pageSelect) {
        @Cleanup val doc = pageSelect.getPDDocument(pdDoc);

        return new PDFLayoutTextStripper().getText(doc);
    }


    /**
     * 提取图片。
     *
     * @param pdDoc        PDF文档对象
     * @param pageIndex    指定页面索引
     * @param imageIndices 图片序号
     * @return 图片列表
     */
    @SneakyThrows
    public static List<PdfImage> stripImages(PDDocument pdDoc, int pageIndex, int... imageIndices) {
        val page = pdDoc.getPage(pageIndex);
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
