package com.github.bingoohuang.pdf;

import lombok.Value;
import org.apache.commons.lang3.StringUtils;

@Value
public class HogonItem {
    private final String scope;
    private final String item;
    private final int score;

    @Override public String toString() {
        return appendIfNotEmpty(scope, ".") + item + ":" + score;
    }

    public static String appendIfNotEmpty(String s, String append) {
        return StringUtils.isEmpty(s) ? "" : (s + append);
    }
}
