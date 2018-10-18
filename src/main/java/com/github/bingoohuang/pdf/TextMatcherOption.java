package com.github.bingoohuang.pdf;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

@Value @Builder
public class TextMatcherOption {
    private final String stripChars;
    private final String rangeOpen;
    private final String rangeClose;

    public TextMatcherOption(String stripChars) {
        this(stripChars, null, null);
    }

    public TextMatcherOption(String rangeOpen, String rangeClose) {
        this(null, rangeOpen, rangeClose);
    }

    public TextMatcherOption(String stripChars, String rangeOpen, String rangeClose) {
        this.stripChars = stripChars;
        this.rangeOpen = rangeOpen;
        this.rangeClose = rangeClose;
    }

    public Pair<Integer, Integer> locateRange(String text) {
        int start = StringUtils.isNotEmpty(rangeOpen) ? text.indexOf(rangeOpen) : 0;
        start = start <= 0 ? 0 : start + rangeOpen.length();

        int end = text.length();
        if (StringUtils.isNotEmpty(rangeClose)) {
            int close = text.indexOf(rangeClose, start);
            if (close >= 0) end = close;
        }

        return Pair.of(start, end);
    }

    public String strip(String text) {
        return StringUtils.trim(StringUtils.strip(StringUtils.trim(text), stripChars));
    }
}
