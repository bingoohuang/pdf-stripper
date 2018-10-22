package com.github.bingoohuang.pdf;

import com.google.common.primitives.Ints;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.val;
import org.apache.pdfbox.pdmodel.PDDocument;

@AllArgsConstructor @Builder
public class PdfPagesSelect {
    private final int[] pageIndices;
    private final boolean excluded;

    public boolean included(int pageIndex) {
        return pageIndices.length == 0 || excluded != Ints.contains(pageIndices, pageIndex);
    }

    public static PdfPagesSelect allPages() {
        return new PdfPagesSelect(new int[0], false);
    }

    public static PdfPagesSelect onPages(int... pageIndices) {
        return new PdfPagesSelect(pageIndices, false);
    }

    public static PdfPagesSelect offPages(int... pageIndices) {
        return new PdfPagesSelect(pageIndices, true);
    }

    public PDDocument getPDDocument(PDDocument doc) {
        if (pageIndices.length == 0) return doc;

        val pageDoc = new PDDocument();
        for (int i = 0, ii = doc.getNumberOfPages(); i < ii; ++i) {
            if (included(i)) pageDoc.addPage(doc.getPage(i));
        }

        return pageDoc;
    }

}
