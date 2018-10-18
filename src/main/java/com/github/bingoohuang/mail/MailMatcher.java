package com.github.bingoohuang.mail;

import org.joda.time.DateTime;

public interface MailMatcher {
    default boolean matchSubject(String subject) {
        return true;
    }

    default boolean matchFrom(String from) {
        return true;
    }

    default boolean matchSentDateTime(DateTime sentDateTime) {
        return true;
    }

    default boolean matchAttachmentFileName(String fileName) {
        return true;
    }

    default int messageStart() {
        return 1;
    }

    default int messageEnd() {
        return -1;
    }
}
