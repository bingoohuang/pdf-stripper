package com.github.bingoohuang.mail;

import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;
import lombok.val;

import javax.activation.FileDataSource;
import java.io.File;

public interface MailAttachment {
    byte[] getBytes();

    String getContentType();

    String getOriginalFilename();

    static MailAttachment of(File file) {
        val fds = new FileDataSource(file);
        return new MailAttachment() {
            @SneakyThrows
            @Override public byte[] getBytes() {
                return ByteStreams.toByteArray(fds.getInputStream());
            }

            @Override public String getContentType() {
                return fds.getContentType();
            }

            @Override public String getOriginalFilename() {
                return file.getName();
            }
        };
    }
}
