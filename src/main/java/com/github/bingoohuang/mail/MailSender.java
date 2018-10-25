package com.github.bingoohuang.mail;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Properties;

import static javax.mail.Message.RecipientType.*;
import static javax.mail.Part.ATTACHMENT;
import static javax.mail.internet.InternetAddress.parse;

/**
 * SMTP邮件发送器。
 */
@RequiredArgsConstructor
public class MailSender {
    private final String host;
    private final String port;
    private final String fromEmail;
    private final String username;
    private final String password;

    public MailSender() {
        this.host = MailConfig.get("smtp.host");
        this.port = StringUtils.defaultString(MailConfig.get("smtp.port"), "25");
        this.fromEmail = MailConfig.get("fromEmail");
        this.username = MailConfig.get("username");
        this.password = MailConfig.get("password");
    }

    @SneakyThrows
    public void send(MailMessage mailMessage) {
        val properties = new Properties() {{
            put("mail.smtp.host", host);
            put("mail.smtp.port", port);
            put("mail.smtp.connectiontimeout", "10000");
            put("mail.smtp.timeout", "10000");
            put("mail.smtp.auth", "true");
            put("mail.smtp.starttls.enable", "true");
        }};

        val session = Session.getInstance(properties, new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        val msg = new MimeMessage(session);
        msg.setHeader("Content-Type", "text/plain; charset=UTF-8");
        msg.setFrom(new InternetAddress(StringUtils.defaultString(fromEmail, username)));

        msg.setRecipients(TO, parse(String.join(",", mailMessage.getTos())));
        if (!mailMessage.getCcs().isEmpty()) {
            msg.setRecipients(CC, parse(String.join(",", mailMessage.getCcs())));
        }
        if (!mailMessage.getBccs().isEmpty()) {
            msg.setRecipients(BCC, parse(String.join(",", mailMessage.getBccs())));
        }

        msg.setSubject(mailMessage.getSubject(), "UTF-8");
        msg.setSentDate(new Date());

        val bodyPart = new MimeBodyPart();
        val isHtml = mailMessage.getContent().contains("<html>");
        bodyPart.setContent(mailMessage.getContent(),
                isHtml ? "text/html; charset=UTF-8" : "text/plain; charset=UTF-8");

        val multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);

        for (val x : mailMessage.getAttachments()) {
            val attachPart = new MimeBodyPart();

            attachPart.setContent(x.getBytes(), x.getContentType());
            attachPart.setFileName(x.getOriginalFilename());
            attachPart.setDisposition(ATTACHMENT);

            multipart.addBodyPart(attachPart);
        }

        msg.setContent(multipart);

        Transport.send(msg);
    }
}
