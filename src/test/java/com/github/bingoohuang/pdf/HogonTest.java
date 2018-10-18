package com.github.bingoohuang.pdf;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class HogonTest {
    @Test @SneakyThrows
    public void scores() {
        @Cleanup val is = Util.loadClassPathRes("原始报告（样本）/Hogan/Flash_SimpChinese.pdf");
        val text = PdfStripper.stripText(is, PdfStripper.PdfPageSelect.includePages(0));
        val textMatcher = new TextMatcher(text);

        List<ValuesItem> items = textMatcher.searchPattern("(\\S+)\\s+(\\d+)", ValuesItem.class,
                TextMatcherOption.builder().rangeOpen("简报").rangeClose("©").build());
        assertThat(items.toString()).isEqualTo("[" +
                "ValuesItem(name=调适, score=98), " +
                "ValuesItem(name=抱负, score=73), " +
                "ValuesItem(name=社交, score=74), " +
                "ValuesItem(name=人际敏感度, score=69), " +
                "ValuesItem(name=审慎, score=72), " +
                "ValuesItem(name=好奇, score=90), " +
                "ValuesItem(name=学习方式, score=73), " +
                "ValuesItem(name=激动, score=99), " +
                "ValuesItem(name=多疑, score=99), " +
                "ValuesItem(name=谨慎, score=86), " +
                "ValuesItem(name=内敛, score=93), " +
                "ValuesItem(name=消极, score=97), " +
                "ValuesItem(name=自大, score=43), " +
                "ValuesItem(name=狡猾, score=49), " +
                "ValuesItem(name=戏剧化, score=34), " +
                "ValuesItem(name=幻想, score=96), " +
                "ValuesItem(name=苛求, score=38), " +
                "ValuesItem(name=恭顺, score=5), " +
                "ValuesItem(name=认可, score=22), " +
                "ValuesItem(name=权力, score=86), " +
                "ValuesItem(name=享乐, score=98), " +
                "ValuesItem(name=利他, score=96), " +
                "ValuesItem(name=归属, score=94), " +
                "ValuesItem(name=传统, score=36), " +
                "ValuesItem(name=保障, score=40), " +
                "ValuesItem(name=商业, score=79), " +
                "ValuesItem(name=美感, score=38), " +
                "ValuesItem(name=科学, score=86)]");
    }
}
