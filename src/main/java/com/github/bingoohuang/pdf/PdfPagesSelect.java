package com.github.bingoohuang.pdf;

import com.google.common.primitives.Ints;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.val;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * 页面筛选器。通过本筛选器，包含指定界面，或者排除指定页面，加速PDF提取过程。
 */
@AllArgsConstructor @Builder
public class PdfPagesSelect {
    private final int[] pageIndices;
    private final boolean excluded;

    public boolean included(int pageIndex) {
        return pageIndices.length == 0 || excluded != Ints.contains(pageIndices, pageIndex);
    }

    /**
     * 包含所有页面。
     *
     * @return PdfPagesSelect对象。
     */
    public static PdfPagesSelect allPages() {
        return new PdfPagesSelect(new int[0], false);
    }

    /**
     * 指定页面索引。
     *
     * @param includedPageIndices 页面索引（0-based)
     * @return PdfPagesSelect
     */
    public static PdfPagesSelect onPages(int... includedPageIndices) {
        return new PdfPagesSelect(includedPageIndices, false);
    }

    /**
     * 排除指定页面。
     *
     * @param excludedPageIndcies 页面索引（0-based)
     * @return PdfPagesSelect
     */
    public static PdfPagesSelect offPages(int... excludedPageIndcies) {
        return new PdfPagesSelect(excludedPageIndcies, true);
    }

    /**
     * 获取指定页面的PDDocument对象。
     *
     * @param doc
     * @return
     */
    public PDDocument getPDDocument(PDDocument doc) {
        if (pageIndices.length == 0) return doc;

        val pageDoc = new PDDocument();
        for (int i = 0, ii = doc.getNumberOfPages(); i < ii; ++i) {
            if (included(i)) pageDoc.addPage(doc.getPage(i));
        }

        return pageDoc;
    }

}
