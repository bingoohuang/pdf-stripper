package com.github.bingoohuang.pdf;

import com.github.bingoohuang.text.ConfiguredStripper;
import com.github.bingoohuang.text.model.TextItem;
import com.github.bingoohuang.text.model.TextTripperConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
import java.util.Map;
import java.util.Set;

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


    /**
     * 根据文本提取配置，提取指定页码(1-based)的文本。
     *
     * @param pdDoc  PDF文档对象
     * @param config 文本提取配置
     * @return map of 页面->文本
     */
    private static Map<Integer, String> stripPagedText(PDDocument pdDoc, TextTripperConfig config) {
        Map<Integer, String> pagedText = Maps.newHashMap();
        if (config.getRules() == null) return pagedText;

        Set<Integer> pages = Sets.newHashSet();
        config.getRules().forEach(x -> pages.addAll(x.getPages()));

        pages.forEach(x -> pagedText.put(x, stripText(pdDoc, PdfPagesSelect.onPages(x - 1))));

        return pagedText;
    }

    /**
     * 根据文本提取配置，从文本中获取指定属性。
     *
     * @param pdDoc  PDF文档对象
     * @param config 文本提取配置
     * @return 文本对象
     */
    public static List<TextItem> strip(PDDocument pdDoc, TextTripperConfig config) {
        val pagedText = stripPagedText(pdDoc, config);
        return new ConfiguredStripper(pagedText, config).strip();
    }
}
