package com.github.bingoohuang.pdf;

import lombok.Setter;
import lombok.val;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.fit.pdfdom.PDFDomTree;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

public class PdfDom extends PDFDomTree {
    @Setter private final PdfListener pdfListener;
    private boolean isRectangle = false;

    public PdfDom(PdfListener pdfListener) throws IOException, ParserConfigurationException {
        super();
        this.pdfListener = pdfListener;
    }

    @Override
    protected void processOperator(Operator operator, List<COSBase> arguments) throws IOException {
        super.processOperator(operator, arguments);

        isRectangle = operator.getName().equals("re") && !disableGraphics && arguments.size() == 4;
    }

    @Override
    protected Element createTextElement(String data, float width) {
        pdfListener.process(curstyle, data);
        return super.createTextElement(data, width);
    }

    @Override
    protected Element createRectangleElement(float x, float y, float width, float height, boolean stroke, boolean fill) {
        val el = super.createRectangleElement(x, y, width, height, stroke, fill);

        if (fill && isRectangle) {
            val style = el.getAttribute("style");
            val start = style.indexOf("background-color:") + "background-color:".length();
            val end = style.indexOf(";", start);
            val fcolor = style.substring(start, end);

            pdfListener.process(new PdfRect(x, y, width, height, fcolor));
        }

        isRectangle = false;

        return el;
    }
}
