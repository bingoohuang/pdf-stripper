package org.fit.pdfdom;

import org.fit.pdfdom.resource.EmbedAsBase64Handler;
import org.fit.pdfdom.resource.HtmlResourceHandler;
import org.fit.pdfdom.resource.IgnoreResourceHandler;
import org.fit.pdfdom.resource.SaveResourceToDirHandler;

import java.io.File;

public class PDFDomTreeConfig {
    private HtmlResourceHandler imageHandler;
    private HtmlResourceHandler fontHandler;

    public static PDFDomTreeConfig createDefaultConfig() {
        PDFDomTreeConfig config = new PDFDomTreeConfig();
        config.setFontHandler(embedAsBase64());
        config.setImageHandler(embedAsBase64());

        return config;
    }

    public static HtmlResourceHandler embedAsBase64() {
        return new EmbedAsBase64Handler();
    }

    public static HtmlResourceHandler saveToDirectory(File directory) {
        return new SaveResourceToDirHandler(directory);
    }

    public static HtmlResourceHandler ignoreResource() {
        return new IgnoreResourceHandler();
    }

    private PDFDomTreeConfig() {
    }

    public HtmlResourceHandler getImageHandler() {
        return imageHandler;
    }

    public void setImageHandler(HtmlResourceHandler imageHandler) {
        this.imageHandler = imageHandler;
    }

    public HtmlResourceHandler getFontHandler() {
        return fontHandler;
    }

    public void setFontHandler(HtmlResourceHandler fontHandler) {
        this.fontHandler = fontHandler;
    }
}
