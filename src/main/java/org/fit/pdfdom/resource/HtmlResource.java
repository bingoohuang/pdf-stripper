package org.fit.pdfdom.resource;

import java.io.IOException;

public abstract class HtmlResource {
    protected String name;

    public HtmlResource(String name) {
        this.name = name;
    }

    public abstract byte[] getData() throws IOException;

    public abstract String getFileEnding();

    public abstract String getMimeType();

    public String getName() {
        return name;
    }
}
