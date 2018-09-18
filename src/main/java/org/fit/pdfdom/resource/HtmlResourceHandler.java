package org.fit.pdfdom.resource;

import java.io.IOException;

public interface HtmlResourceHandler {
    /**
     * @return the URI to be used in generated HTML resource elements/
     */
    String handleResource(HtmlResource resource) throws IOException;
}

