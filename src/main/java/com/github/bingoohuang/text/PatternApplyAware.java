package com.github.bingoohuang.text;

/**
 * 正则匹配捕获子分组应用器。
 */
public interface PatternApplyAware {
    /**
     * 应用正则匹配捕获子分组内容。
     *
     * @param patternGroups 正则匹配捕获子分组
     */
    void apply(String[] patternGroups);

}
