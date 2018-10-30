package com.github.bingoohuang.text;

import com.github.bingoohuang.pdf.Util;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

@Slf4j
public class Filter {
    private static Map<String, BiFunction<String, List<String>, String>> predefinedFilters = Maps.newHashMap();
    private static Pattern TRAILING_ZERO_PATTERN = Pattern.compile("(\\.\\d*?)0+\\b");

    static {
        // 压缩字符串中的多个空格为一个。
        predefinedFilters.put("compactBlanks", (s, args) -> s.replaceAll("\\s\\s+", " "));

        // 去除数字类型小数后面多余的0，例如：4.7000->4.7, 4.00->4
        predefinedFilters.put("trimTrailingZeroes", (s, args) -> {
            val m = TRAILING_ZERO_PATTERN.matcher(s);
            return m.find() ? m.replaceAll(m.group(1).equals(".") ? "" : m.group(1)) : s;
        });

        // 归整化日期时间，例如：2018-10-24T11:21:11.683-> 2018-10-24 11:21:11
        predefinedFilters.put("normalizeDateTime", (s, args) ->
                s.replace('T', ' ')
                        .replaceAll("\\.\\d{3}", ""));

        // 去除指定前缀
        predefinedFilters.put("unPrefix", (s, args) -> {
            String s2 = s;
            for (val arg : args) {
                if (StringUtils.startsWith(s2, arg)) {
                    s2 = s2.substring(arg.length());
                }
            }

            return s2;
        });

        // 去除指定后缀
        predefinedFilters.put("unPostfix", (s, args) -> {
            String s2 = s;
            for (val arg : args) {
                if (StringUtils.endsWith(s2, arg)) {
                    s2 = s2.substring(0, s2.length() - arg.length());
                }
            }

            return s2;
        });

        // 映射处理
        predefinedFilters.put("map", (s, args) ->
                args.size() == 2 && StringUtils.equals(s, args.get(0)) ? args.get(1) : s);

        // 规整处理
        predefinedFilters.put("roundup", (s, args) ->
                args.size() == 1 ? Util.roundHalfUp(s, Integer.parseInt(args.get(0))) : s);
    }


    public static String filter(String s, String filersOptions) {
        if (StringUtils.isEmpty(filersOptions)) return s;

        String filtered = s;
        val specs = SpecParser.parseSpecs(filersOptions);

        for (val spec : specs) {
            val func = predefinedFilters.get(spec.getName());
            if (func != null) {
                filtered = func.apply(filtered, spec.getParams());
            } else {
                log.warn("Unknown filter name @{}", spec.getName());
            }
        }

        return filtered;
    }

}
