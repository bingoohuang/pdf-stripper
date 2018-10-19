package com.github.bingoohuang.pdf;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.output.NullWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.io.InputStream;
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
     * @param is          PDF输入流
     * @param pageSelect  指定页面
     * @param pdfListener 监听器
     */
    @SneakyThrows
    public static void stripCustom(InputStream is, PdfPagesSelect pageSelect, PdfListener pdfListener) {
        @Cleanup val original = PDDocument.load(is);
        stripCustom(original, pageSelect, pdfListener);
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
     * @param is         PDF输入流
     * @param pageSelect 指定页面
     * @return 布局好的文本
     */
    @SneakyThrows
    public static String stripText(InputStream is, PdfPagesSelect pageSelect) {
        @Cleanup val original = PDDocument.load(is);
        return stripText(original, pageSelect);
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
     * @param is           PDF输入流
     * @param pageIndex    指定页面索引
     * @param imageIndices 图片序号
     * @return 图片列表
     */
    @SneakyThrows
    public static List<PdfImage> stripImages(InputStream is, int pageIndex, int... imageIndices) {
        @Cleanup val doc = PDDocument.load(is);
        return stripImages(doc, pageIndex, imageIndices);
    }

    /**
     * 提取图片。
     *
     * @param pdDoc        PDF文档对象
     * @param pageIndex    指定页面索引
     * @param imageIndices 图片序号
     * @return 图片列表
     */
    public static List<PdfImage> stripImages(PDDocument pdDoc, int pageIndex, int[] imageIndices) throws IOException {
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

    /**
     * 从输入流装载PDF文档对象。
     *
     * @param inputStream 输入流
     * @return PDF文档对象
     */
    @SneakyThrows
    public static PDDocument loadPdDocument(InputStream inputStream) {
        return PDDocument.load(inputStream);
    }
}
