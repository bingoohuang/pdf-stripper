package com.github.bingoohuang.pdf;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.io.File;

@Slf4j
public class Util {

    /**
     * 保存PdfImage图片对象为文件。
     *
     * @param pdfImage PdfImage图片对象
     */
    @SneakyThrows
    public static void saveImage(PdfImage pdfImage) {
        ImageIO.write(pdfImage.getImage(), pdfImage.getSuffix(),
                new File(pdfImage.getName() + "." + pdfImage.getSuffix()));
    }


}
