package org.fit.pdfdom.resource;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

public class EmbedAsBase64Handler implements HtmlResourceHandler {
    public String handleResource(HtmlResource resource) throws IOException {
        String base64Data = "";
        byte[] data = resource.getData();
        if (data != null)
            base64Data = DatatypeConverter.printBase64Binary(data);

        return String.format("data:%s;base64,%s", resource.getMimeType(), base64Data);
    }
}
