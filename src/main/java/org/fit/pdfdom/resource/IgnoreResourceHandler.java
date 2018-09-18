package org.fit.pdfdom.resource;

public class IgnoreResourceHandler implements HtmlResourceHandler {
    public String handleResource(HtmlResource resource) {
        return "";
    }
}
