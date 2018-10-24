package com.github.bingoohuang.pdf;

import com.google.common.io.Files;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class UtilTest {

    @Test @SneakyThrows @Ignore
    public void download() {
        val res = Util.download("http://eas.zhaopin.com/CompanyPlatform/RedirectToReport.ashx?eid=fc10fc13ffbc4168a2e60e0155018d1e&eaid=12286145&spid=3577&type=5");
        System.out.println(res);

        Files.write(res.getContent(), new File(res.getFileName()));
    }
}