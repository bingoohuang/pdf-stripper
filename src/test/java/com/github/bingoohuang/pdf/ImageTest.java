package com.github.bingoohuang.pdf;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class ImageTest {
    @Test @SneakyThrows
    public void test() {
        @Cleanup val is = Util.loadClassPathRes("原始报告（样本）/智联/情绪管理能力测验（样本）.pdf");
        List<PdfImage> pdfImages = PdfStripper.stripImages(is, 3, 3);
        assertThat(pdfImages).hasSize(1);
        val pdfImage = pdfImages.get(0);
        assertThat(pdfImage.getName()).isEqualTo("Im37");
        assertThat(pdfImage.getSuffix()).isEqualTo("jpg");
        assertThat(pdfImage.getImage()).isNotNull();
        assertThat(pdfImage.getHeight()).isGreaterThan(0);
        assertThat(pdfImage.getWidth()).isGreaterThan(0);

//        Util.saveImage(pdfImages.get(0));
    }
}
