package com.github.bingoohuang.pdf;

import com.google.common.collect.Lists;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.objenesis.ObjenesisStd;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextMatcher {
    private final String text;

    public TextMatcher(String text) {
        this.text = text;
    }

    public String findLineLabelText(String label) {
        return findLineLabelText(label, null);
    }
    
    public String findLineLabelText(String label, String stripChars) {
        int labelPos = text.indexOf(label);
        if (labelPos < 0) return "";

        int fromIndex = labelPos + label.length();
        int endPos = text.indexOf("\n", fromIndex);

        val labelText = endPos < 0 ? text.substring(fromIndex) : text.substring(fromIndex, endPos);

        return StringUtils.trim(StringUtils.strip(StringUtils.trim(labelText), stripChars));
    }

    public <T> List<T> searchPattern(String pattern, Class<T> itemClass) {
        if (!PatternApplyAware.class.isAssignableFrom(itemClass)) {
            throw new RuntimeException(itemClass + " required to implement PatternApplyAware");
        }

        val instantiator = new ObjenesisStd().getInstantiatorOf(itemClass);
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        List<T> items = Lists.newArrayList();
        while (m.find()) {
            int groupCount = m.groupCount();
            String[] groups = new String[groupCount];
            for (int i = 0; i < groupCount; ++i) {
                groups[i] = m.group(i + 1);
            }

            T item = instantiator.newInstance();
            ((PatternApplyAware) item).apply(groups);

            items.add(item);
        }

        return items;
    }

    private static Pattern BlankPattern = Pattern.compile("\\s*(\\S+)");

    public String findLabelText(String label, String rangeOpenKey, String rangeCloseKey) {
        val range = locateRange(rangeOpenKey, rangeCloseKey);

        int pos = text.indexOf(label, range.getLeft());
        if (pos < 0) return "";
        if (pos >= range.getRight()) return "";

        String sub = text.substring(pos + label.length(), range.getRight());
        Matcher matcher = BlankPattern.matcher(sub);
        return matcher.find() ? matcher.group(1) : "";
    }

    public String findPatternText(String pattern, String rangeOpenKey, String rangeCloseKey) {
        val range = locateRange(rangeOpenKey, rangeCloseKey);

        String sub = text.substring(range.getLeft(), range.getRight());
        Matcher matcher = Pattern.compile(pattern).matcher(sub);
        return matcher.find() ? matcher.group() : "";
    }

    private Pair<Integer, Integer> locateRange(String rangeOpenKey, String rangeCloseKey) {
        int start = StringUtils.isNotEmpty(rangeOpenKey) ? text.indexOf(rangeOpenKey) : 0;
        if (start < 0) start = 0;

        int end = text.length();
        if (StringUtils.isNoneEmpty(rangeCloseKey)) {
            int close = text.indexOf(rangeCloseKey, start);
            if (close >= 0) end = close;
        }

        return Pair.of(start, end);
    }
}
