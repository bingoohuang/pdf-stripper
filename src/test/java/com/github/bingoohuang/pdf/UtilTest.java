package com.github.bingoohuang.pdf;

import com.google.common.io.Files;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static com.google.common.truth.Truth.assertThat;

public class UtilTest {
    @Test @SneakyThrows @Ignore
    public void download() {
        val res = Util.download("http://eas.zhaopin.com/CompanyPlatform/RedirectToReport.ashx?eid=fc10fc13ffbc4168a2e60e0155018d1e&eaid=12286145&spid=3577&type=5");
        System.out.println(res);

        Files.asByteSink(new File(res.getFileName())).write(res.getContent());
    }

    @Test
    public void roundHalfUp() {
        assertThat(Util.roundHalfUp("1", 1)).isEqualTo("1.0");
        assertThat(Util.roundHalfUp("1.11", 1)).isEqualTo("1.1");
        assertThat(Util.roundHalfUp("1.15", 1)).isEqualTo("1.2");
    }

    @Test
    public void regularDateTime() {
        val fmt = "yyyy-MM-dd HH:mm:ss.SSS";
        assertThat(Util.regular("2018-10-23").toString(fmt)).isEqualTo("2018-10-23 00:00:00.000");
        assertThat(Util.regular("2018.10.23").toString(fmt)).isEqualTo("2018-10-23 00:00:00.000");
        assertThat(Util.regular("10-23").toString(fmt)).isEqualTo("1970-10-23 00:00:00.000");
        assertThat(Util.regular("2018-10-23  21:47").toString(fmt)).isEqualTo("2018-10-23 21:47:00.000");
        assertThat(Util.regular("2018.10.23 21:47:01").toString(fmt)).isEqualTo("2018-10-23 21:47:01.000");
        assertThat(Util.regular("21:47").toString(fmt)).isEqualTo("1970-01-01 21:47:00.000");
        assertThat(Util.regular("21:47:40").toString(fmt)).isEqualTo("1970-01-01 21:47:40.000");
        assertThat(Util.regular("2018-10-23 1:47").toString(fmt)).isEqualTo("2018-10-23 01:47:00.000");
        assertThat(Util.regular("2018年9月23日21:47").toString(fmt)).isEqualTo("2018-09-23 21:47:00.000");
        assertThat(Util.regular("18年9月23日21:47").toString(fmt)).isEqualTo("2018-09-23 21:47:00.000");
        assertThat(Util.regular("2018/10/23 21:48:15").toString(fmt)).isEqualTo("2018-10-23 21:48:15.000");
        assertThat(Util.regular("2018-10-23T21:48:15.235").toString(fmt)).isEqualTo("2018-10-23 21:48:15.235");
    }
}