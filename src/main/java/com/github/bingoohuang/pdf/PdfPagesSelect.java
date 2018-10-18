package com.github.bingoohuang.pdf;

import com.google.common.primitives.Ints;
import lombok.Builder;
import lombok.Value;
import lombok.val;
import org.apache.pdfbox.pdmodel.PDDocument;

@Value @Builder
public class PdfPagesSelect {
    private final int[] pageIndices;
    private final boolean excluded;

    public boolean included(int pageIndex) {
        return pageIndices.length == 0 || excluded != Ints.contains(pageIndices, pageIndex);
    }

    public static PdfPagesSelect allPages() {
        return new PdfPagesSelect(new int[0], false);
    }

    public static PdfPagesSelect includePages(int... pageIndices) {
        return new PdfPagesSelect(pageIndices, false);
    }

    public static PdfPagesSelect excludePages(int... pageIndices) {
        return new PdfPagesSelect(pageIndices, true);
    }

    public PDDocument getPDDocument(PDDocument doc) {
        if (pageIndices.length == 0) return doc;

        val pageDoc = new PDDocument();
        for (int pageIndex = 0, pageCount = doc.getNumberOfPages(); pageIndex < pageCount; ++pageIndex) {
            if (included(pageIndex)) {
                pageDoc.addPage(doc.getPage(pageIndex));
            }
        }

        return pageDoc;
    }

}
