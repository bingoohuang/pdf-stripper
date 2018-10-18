package com.github.bingoohuang.pdf;

import com.google.common.collect.Sets;
import lombok.Builder;
import lombok.Value;
import lombok.val;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Value @Builder
public class PdfPageSelect {
    private final Set<Integer> pageIndices;
    private final boolean included;
    private final boolean debug;

    public boolean included(int pageIndex) {
        return pageIndices.isEmpty() || included == pageIndices.contains(pageIndex);
    }

    public static PdfPageSelect allPages() {
        return new PdfPageSelect(Sets.newHashSet(), true, false);
    }

    public static PdfPageSelect includePages(int... pageIndices) {
        return new PdfPageSelect(IntStream.of(pageIndices).boxed().collect(Collectors.toSet()), true, false);
    }

    public static PdfPageSelect excludePages(int... pageIndices) {
        return new PdfPageSelect(IntStream.of(pageIndices).boxed().collect(Collectors.toSet()), false, false);
    }

    public PDDocument getPDDocument(PDDocument doc) {
        if (pageIndices.isEmpty()) return doc;

        val pageDoc = new PDDocument();
        for (int pageIndex = 0, pageCount = doc.getNumberOfPages(); pageIndex < pageCount; ++pageIndex) {
            if (included(pageIndex)) {
                pageDoc.addPage(doc.getPage(pageIndex));
            }
        }

        return pageDoc;
    }

}
