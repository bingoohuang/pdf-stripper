package com.github.bingoohuang.mail;

import com.google.common.collect.Lists;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import java.util.List;
import java.util.Properties;

@RequiredArgsConstructor
public class Pop3MailFetcher {
    private final String host;
    private final String port;
    private final String userName;
    private final String password;
    private final MailMatcher matcher;

    @SneakyThrows
    public List<Pop3MailMessage> fetchMails() {
        val properties = new Properties();
        properties.put("mail.pop3.host", host);
        properties.put("mail.pop3.port", port);
        val session = Session.getDefaultInstance(properties);

        @Cleanup val store = session.getStore("pop3");
        store.connect(userName, password);
        @Cleanup val inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        return fetchInboxMessages(inbox);
    }

    @SneakyThrows
    private List<Pop3MailMessage> fetchInboxMessages(Folder inbox) {
        int end = matcher.messageEnd();
        if (end < 0) end = inbox.getMessageCount();

        val inboxMessages = inbox.getMessages(matcher.messageStart(), end);

        List<Pop3MailMessage> messages = Lists.newArrayList();
        for (int i = 0; i < inboxMessages.length; i++) {
            val message = inboxMessages[i];

            val subject = message.getSubject();
            if (!matcher.matchSubject(subject)) continue;

            val from = ((InternetAddress) message.getFrom()[0]).getAddress();
            if (!matcher.matchFrom(from)) continue;

            val sendDateTime = new DateTime(message.getSentDate());
            if (!matcher.matchSentDateTime(sendDateTime)) continue;

            val attachments = parseAttachments(message);

            messages.add(Pop3MailMessage.builder()
                    .messageNumber(message.getMessageNumber())
                    .subject(subject)
                    .from(from)
                    .sendDateTime(sendDateTime)
                    .content(getText(message))
                    .attachments(attachments)
                    .build());
        }

        return messages;
    }

    @SneakyThrows
    private List<Pop3MailMessageAttachment> parseAttachments(Message message) {
        List<Pop3MailMessageAttachment> attachments = Lists.newArrayList();

        if (message.getContent() instanceof Multipart) {
            val multi = (Multipart) message.getContent();
            for (int j = 0; j < multi.getCount(); j++) {
                val bodyPart = multi.getBodyPart(j);
                val partFileName = bodyPart.getFileName();
                if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) || StringUtils.isBlank(partFileName)) {
                    continue; // dealing with attachments only
                }

                val fileName = MimeUtility.decodeText(partFileName);
                if (!matcher.matchAttachmentFileName(fileName)) continue;

                attachments.add(Pop3MailMessageAttachment.builder()
                        .fileName(fileName)
                        .inputStream(bodyPart.getInputStream())
                        .build());
            }
        }

        return attachments;
    }


    /**
     * Return the primary text content of the message.
     */
    @SneakyThrows
    private static String getText(Part p) {
        if (p.isMimeType("text/html")) {
            return Jsoup.parse((String) p.getContent()).text();
        }

        if (p.isMimeType("text/*")) {
            return (String) p.getContent();
        }

        if (p.isMimeType("multipart/*")) {
            // prefer html text over plain text
            Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null) text = getText(bp);
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null) return Jsoup.parse(s).text();
                } else {
                    return getText(bp);
                }
            }
            return text;
        }

        return null;
    }
}
