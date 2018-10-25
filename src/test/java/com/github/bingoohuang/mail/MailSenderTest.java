package com.github.bingoohuang.mail;

import com.github.bingoohuang.pdf.Util;
import org.junit.Ignore;
import org.junit.Test;

public class MailSenderTest {
    @Test @Ignore
    public void sendText() {
        new MailSender().send(MailMessage.builder()
                .to("bingoohuang@dingtalk.com")
                .subject("黄进兵测试")
                .content("你好，黄进兵。我就是小测试一下")
                .build());
    }

    @Test @Ignore
    public void sendHtml() {
        new MailSender().send(MailMessage.builder()
                .to("huangjb@raiyee.com")
                .cc("bingoohuang@dingtalk.com")
                .subject("黄进兵测试HTML")
                .content("<!doctype html>" +
                        "<html>" +
                        "<head>" +
                        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                        "</head>" +
                        "<body><h3>你好，黄进兵。我哈哈哈哈</h3></body>" +
                        "</html>")
                .build());
    }

    @Test @Ignore
    public void sendAttachments() {
        new MailSender().send(MailMessage.builder()
                .to("huangjb@raiyee.com")
                .cc("bingoohuang@dingtalk.com")
                .subject("黄进兵测试附件")
                .content("<!doctype html>" +
                        "<html>" +
                        "<head>" +
                        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                        "</head>" +
                        "<body><h3>你好，详情见附件</h3></body>" +
                        "</html>")
                .attachment(MailAttachment.of(Util.loadClasspathFile("原始报告（样本）/Hogan/Flash_SimpChinese.pdf")))
                .attachment(MailAttachment.of(Util.loadClasspathFile("原始报告（样本）/北森/管理技能测评报告（样本）.pdf")))
                .build());
    }
}

