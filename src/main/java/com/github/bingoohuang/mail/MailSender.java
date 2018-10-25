package com.github.bingoohuang.mail;

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
public class MailSender {
    private final String fromEmail;
    private final String username;
    private final String password;
    private final Properties properties = new Properties();

    public MailSender() {
        val env = MailConfig.getEnv();
        env.stringPropertyNames().forEach(k -> {
            if (k.startsWith("mail.smtp.")) properties.put(k, env.getProperty(k));
        });

        if (!properties.containsKey("mail.smtp.port"))
            properties.put("mail.smtp.port", "25");

        username = MailConfig.get("username");
        password = MailConfig.get("password");
        fromEmail = env.getProperty("fromEmail", username);
    }

    @SneakyThrows
    public void send(MailMessage mailMessage) {
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
