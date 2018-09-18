package org.fit.pdfdom.resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageResource extends HtmlResource {
    private final BufferedImage image;
    private double x = 0;
    private double y = 0;

    public ImageResource(String name, BufferedImage image) {
        super(name);

        this.image = image;
    }

    public byte[] getData() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", buffer);

        return buffer.toByteArray();
    }

    public String getFileEnding() {
        return "png";
    }

    public String getMimeType() {
        return "image/png";
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public float getHeight() {
        return image.getHeight();
    }

    public float getWidth() {
        return image.getWidth();
    }
}
