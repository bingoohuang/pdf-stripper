package com.github.bingoohuang.pdf;

import lombok.Cleanup;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class HanergyZhilianTest {
    @Test @SneakyThrows
    public void 情绪管理能力测验() {
        @Cleanup val is = Util.loadClassPathRes("原始报告（样本）/智联/情绪管理能力测验（样本）.pdf");
        val text = PdfStripper.stripText(is);
        System.out.println(text);
        val textMatcher = new TextMatcher(text);
        String tester = textMatcher.findLineLabelText("Respondent", "：");
        assertThat(tester).isEqualTo("张晓平");
        assertThat(textMatcher.findLineLabelText("Date：")).isEqualTo("2016-05-25");

        val t1 = textMatcher.findLabelText("很高", "情绪管理能力", "总体而言，" + tester);
        assertThat(t1).isEqualTo("5.5");

        val t2 = textMatcher.findLineLabelText("作答时间：");
        assertThat(t2).isEqualTo("7分钟22秒");

        val t3 = textMatcher.findLineLabelText("正常与否：", "参考时间", "选项分布");
        assertThat(t3).isEqualTo("正常");

    }

    @Test @SneakyThrows
    public void 职业行为风险测验() {
        @Cleanup val is = Util.loadClassPathRes("原始报告（样本）/智联/职业行为风险测验（样本）.pdf");
        val text = PdfStripper.stripText(is);
        val textMatcher = new TextMatcher(text);

        assertThat(textMatcher.findLineLabelText("测试者：")).isEqualTo("张晓平");
        assertThat(textMatcher.findLineLabelText("测试日期：")).isEqualTo("2016-06-12");

        List<Score> scores = textMatcher.searchPattern("(\\S+)：([高中低])\\s*(-?\\d+)", Score.class);
        assertThat(scores.toString()).isEqualTo("[" +
                "HanergyZhilianTest.Score(name=焦虑不安, level=低, score=0), " +
                "HanergyZhilianTest.Score(name=抑郁消沉, level=低, score=0), " +
                "HanergyZhilianTest.Score(name=偏执多疑, level=低, score=0), " +
                "HanergyZhilianTest.Score(name=冷漠孤僻, level=低, score=-14), " +
                "HanergyZhilianTest.Score(name=特立独行, level=低, score=0), " +
                "HanergyZhilianTest.Score(name=冲动暴躁, level=低, score=0), " +
                "HanergyZhilianTest.Score(name=喜怒无常, level=低, score=0), " +
                "HanergyZhilianTest.Score(name=社交回避, level=低, score=0), " +
                "HanergyZhilianTest.Score(name=僵化固执, level=低, score=0), " +
                "HanergyZhilianTest.Score(name=依赖顺从, level=低, score=-12), " +
                "HanergyZhilianTest.Score(name=夸张做作, level=低, score=-12), " +
                "HanergyZhilianTest.Score(name=狂妄自恋, level=低, score=0), " +
                "HanergyZhilianTest.Score(name=压力耐受, level=低, score=54), " +
                "HanergyZhilianTest.Score(name=积极乐观, level=低, score=58), " +
                "HanergyZhilianTest.Score(name=合理自信, level=中, score=68), " +
                "HanergyZhilianTest.Score(name=坚韧不拔, level=低, score=42)]");

        val t1 = textMatcher.findLabelText("正常与否：", "作答时间", "称许性");
        assertThat(t1).isEqualTo("正常");

        val t2 = textMatcher.findLabelText("共计", "作答时间", "称许性");
        assertThat(t2).isEqualTo("16分钟");
        val t3 = textMatcher.findPatternText("\\d+秒", "作答时间", "称许性");
        assertThat(t3).isEqualTo("45秒");

        val t4 = textMatcher.findLabelText("正常与否：", "称许性", "选项分布");
        assertThat(t4).isEqualTo("正常");

        val t5 = textMatcher.findLabelText("正常与否：", "选项分布", "作答完整性");
        assertThat(t5).isEqualTo("正常");

        val t6 = textMatcher.findLabelText("完成率：", "作答题数", "%");
        assertThat(t6).isEqualTo("100");

    }

    @Data
    public static class Score implements PatternApplyAware {
        private String name;
        private String level; // 高中低
        private int score;

        @Override public void apply(String[] groups) {
            this.name = groups[0];
            this.level = groups[1];
            this.score = Integer.parseInt(groups[2]);
        }
    }
}
