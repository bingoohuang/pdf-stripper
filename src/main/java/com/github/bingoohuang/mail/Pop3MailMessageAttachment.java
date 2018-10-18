package com.github.bingoohuang.mail;

import lombok.Builder;
import lombok.Value;

import java.io.InputStream;

@Value @Builder
public class Pop3MailMessageAttachment {
    private final String fileName;
    private final InputStream inputStream;
}
