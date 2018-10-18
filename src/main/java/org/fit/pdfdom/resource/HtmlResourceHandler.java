package org.fit.pdfdom.resource;

import java.io.IOException;

public interface HtmlResourceHandler {
    /**
     * Handle html resource.
     *
     * @param resource html resource
     * @return the URI to be used in generated HTML resource elements/
     * @throws IOException IOException
     */
    String handleResource(HtmlResource resource) throws IOException;
}

