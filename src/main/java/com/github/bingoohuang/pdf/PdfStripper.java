package com.github.bingoohuang.pdf;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import lombok.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PdfStripper {
    static {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
    }

    @Value @RequiredArgsConstructor
    public static class PdfPageSelect {
        private final Set<Integer> pageIndices;
        private final boolean included;

        public boolean included(int pageIndex) {
            return pageIndices.isEmpty() || included == pageIndices.contains(pageIndex);
        }

        public static PdfPageSelect allPages() {
            return new PdfPageSelect(Sets.newHashSet(), true);
        }

        public static PdfPageSelect includePages(int... pageIndices) {
            return new PdfPageSelect(IntStream.of(pageIndices).boxed().collect(Collectors.toSet()), true);
        }

        public static PdfPageSelect excludePages(int... pageIndices) {
            return new PdfPageSelect(IntStream.of(pageIndices).boxed().collect(Collectors.toSet()), false);
        }
    }

    /**
     * 从PDF中提取布局好的文本。
     *
     * @param is          PDF输入流
     * @param pageIndices 指定页面，不指定时全部页
     * @return 布局好的文本
     */
    @SneakyThrows
    public static String stripText(InputStream is, PdfPageSelect pageIndices) {
        @Cleanup val doc = PDDocument.load(is);
        if (pageIndices.getPageIndices().isEmpty())
            return new PDFLayoutTextStripper().getText(doc);

        @Cleanup val pageDoc = new PDDocument();
        for (int pageIndex = 0, pageCount = doc.getNumberOfPages(); pageIndex < pageCount; ++pageIndex) {
            if (pageIndices.included(pageIndex)) {
                pageDoc.addPage(doc.getPage(pageIndex));
            }
        }
        return new PDFLayoutTextStripper().getText(pageDoc);
    }


    @Value @AllArgsConstructor
    public static class PdfImage {
        private final BufferedImage image;
        private final String name;
        private final String suffix;
        private final int height;
        private final int width;
    }

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
