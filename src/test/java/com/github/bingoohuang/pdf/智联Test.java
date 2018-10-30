package com.github.bingoohuang.pdf;

import com.alibaba.fastjson.JSON;
import com.github.bingoohuang.text.TextMatcher;
import com.github.bingoohuang.text.TextMatcherOption;
import com.github.bingoohuang.text.model.TextItem;
import com.github.bingoohuang.text.model.TextTripperConfig;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.hjson.JsonValue;
import org.junit.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class 智联Test {
    @Test @SneakyThrows
    public void 职业价值观测验hjson() {
        val hjson = Util.loadClasspathResAsString("智联.2998.职业价值观测验.hjson");
        val json = JsonValue.readHjson(hjson).toString();
        val config = JSON.parseObject(json, TextTripperConfig.class);

        @Cleanup val is = Util.loadClassPathRes("原始报告（样本）/智联/职业价值观测验（样本）.pdf");
        @Cleanup val pdDoc = Util.loadPdf(is);
        List<TextItem> items = PdfStripper.strip(pdDoc, config);
        assertThat(items.toString()).isEqualTo("[" +
                "TextItem(name=有效性, value=比较可信, desc=null), " +
                "TextItem(name=测试开始时间, value=2016-06-12 16:39:06, desc=null), " +
                "TextItem(name=测试完成时间, value=2016-06-12 16:56:33, desc=null), " +
                "TextItem(name=薪酬福利, value=8.3, desc=null), " +
                "TextItem(name=工作稳定, value=6.1, desc=null), " +
                "TextItem(name=公平公正, value=3.5, desc=null), " +
                "TextItem(name=工作强度, value=3.3, desc=null), " +
                "TextItem(name=同事关系, value=5.7, desc=null), " +
                "TextItem(name=服务他人, value=4.8, desc=null), " +
                "TextItem(name=上级支持, value=4.7, desc=null), " +
                "TextItem(name=他人认可, value=4.9, desc=null), " +
                "TextItem(name=管理他人, value=4.8, desc=null), " +
                "TextItem(name=独立自主, value=4.4, desc=null), " +
                "TextItem(name=获得成就, value=8.6, desc=null), " +
                "TextItem(name=晋升机会, value=6.5, desc=null), " +
                "TextItem(name=培训机会, value=3.9, desc=null), " +
                "TextItem(name=施展才华, value=3.2, desc=null)]");
    }

    @Test @SneakyThrows
    public void 职业价值观测验() {
        @Cleanup val is = Util.loadClassPathRes("原始报告（样本）/智联/职业价值观测验（样本）.pdf");
        @Cleanup val pdDoc = Util.loadPdf(is);
        val text = PdfStripper.stripText(pdDoc, PdfPagesSelect.offPages(1, 2, 3));
        val textMatcher = new TextMatcher(text);
        assertThat(textMatcher.findLineLabelText("测试者：")).isEqualTo("张晓平");
        assertThat(textMatcher.findLineLabelText("测试日期：")).isEqualTo("2016-06-12");

        val sb = new StringBuilder();
        textMatcher.searchPattern("(\\S+)[　\\s]+(\\d+(?>\\.\\d+)?)",
                groups -> sb.append(groups[0]).append(":").append(groups[1]).append("\n"),
                new TextMatcherOption("3  详细结果", "©智联测评版权所有"));

        assertThat(sb.toString()).isEqualTo(
                "薪酬福利:8.3\n" +
                        "工作稳定:6.1\n" +
                        "公平公正:3.5\n" +
                        "工作强度:3.3\n" +
                        "同事关系:5.7\n" +
                        "服务他人:4.8\n" +
                        "上级支持:4.7\n" +
                        "他人认可:4.9\n" +
                        "管理他人:4.8\n" +
                        "独立自主:4.4\n" +
                        "获得成就:8.6\n" +
                        "晋升机会:6.5\n" +
                        "培训机会:3.9\n" +
                        "施展才华:3.2\n");

        List<ValuesItem> items = textMatcher.searchPattern("(\\S+)[　\\s]+(\\d+(?>\\.\\d+)?)", ValuesItem.class,
                new TextMatcherOption("3  详细结果", "©智联测评版权所有"));
        assertThat(items.toString()).isEqualTo("[" +
                "ValuesItem(name=薪酬福利, score=8.3), " +
                "ValuesItem(name=工作稳定, score=6.1), " +
                "ValuesItem(name=公平公正, score=3.5), " +
                "ValuesItem(name=工作强度, score=3.3), " +

                "ValuesItem(name=同事关系, score=5.7), " +
                "ValuesItem(name=服务他人, score=4.8), " +
                "ValuesItem(name=上级支持, score=4.7), " +

                "ValuesItem(name=他人认可, score=4.9), " +
                "ValuesItem(name=管理他人, score=4.8), " +
                "ValuesItem(name=独立自主, score=4.4), " +

                "ValuesItem(name=获得成就, score=8.6), " +
                "ValuesItem(name=晋升机会, score=6.5), " +
                "ValuesItem(name=培训机会, score=3.9), " +
                "ValuesItem(name=施展才华, score=3.2)]");

        val t2 = textMatcher.findLineLabelText("作答时间：");
        assertThat(t2).isEqualTo("8分钟35秒");
        val t3 = textMatcher.findLineLabelText("正常与否", new TextMatcherOption(":：", "参考时间", "选项分布"));
        assertThat(t3).isEqualTo("正常");
        val t4 = textMatcher.findLineLabelText("正常与否", new TextMatcherOption(":：", "选项分布", "作答完整性"));
        assertThat(t4).isEqualTo("正常");
        val t5 = textMatcher.findLineLabelText("完成率", new TextMatcherOption(":：", "全部题数", "%"));
        assertThat(t5).isEqualTo("100.00");
    }


    @Test @SneakyThrows
    public void 情绪管理能力测验hjson() {
        val hjson = Util.loadClasspathResAsString("智联.238.情绪管理能力测验.hjson");
        val json = JsonValue.readHjson(hjson).toString();
        val config = JSON.parseObject(json, TextTripperConfig.class);

        @Cleanup val is = Util.loadClassPathRes("原始报告（样本）/智联/情绪管理能力测验（样本）.pdf");
        @Cleanup val pdDoc = Util.loadPdf(is);
        List<TextItem> items = PdfStripper.strip(pdDoc, config);
        assertThat(items.toString()).isEqualTo("[TextItem(name=有效性, value=比较可信, desc=null), " +
                "TextItem(name=测试开始时间, value=2016-05-25 14:54:20, desc=null), " +
                "TextItem(name=测试完成时间, value=2016-05-25 15:01:45, desc=null)]");
    }

    @Test @SneakyThrows
    public void 情绪管理能力测验() {
        @Cleanup val is = Util.loadClassPathRes("原始报告（样本）/智联/情绪管理能力测验（样本）.pdf");
        @Cleanup val pdDoc = Util.loadPdf(is);
        val text = PdfStripper.stripText(pdDoc, PdfPagesSelect.allPages());

        val textMatcher = new TextMatcher(text);
        assertThat(textMatcher.findLineLabelText("Respondent", new TextMatcherOption("："))).isEqualTo("张晓平");
        assertThat(textMatcher.findLineLabelText("Date：")).isEqualTo("2016-05-25");

        val t1 = textMatcher.findLabelText("很高", new TextMatcherOption("2.1  情绪管理能力", "的情绪管理能力在人群中"));
        assertThat(t1).isEqualTo("5.5");
        val s1 = textMatcher.findLabelText("很高", new TextMatcherOption("2.2  积极情绪指数", "的积极情绪指数"));
        assertThat(s1).isEqualTo("4.8");

        val s2 = textMatcher.findLabelText("很高", new TextMatcherOption("3.1  自我情绪认知", "的自我情绪认知"));
        assertThat(s2).isEqualTo("6.7");
        val s3 = textMatcher.findLabelText("很高", new TextMatcherOption("3.2  自我情绪激励", "的自我情绪激励"));
        assertThat(s3).isEqualTo("5.9");
        val s4 = textMatcher.findLabelText("很高", new TextMatcherOption("3.3  自我情绪调控", "的自我情绪调控"));
        assertThat(s4).isEqualTo("4.7");

        val b0 = textMatcher.findLabelText("很高", new TextMatcherOption("4.1 他人情绪感知", "的他人情绪感知"));
        assertThat(b0).isEqualTo("5");
        val b1 = textMatcher.findLabelText("很高", new TextMatcherOption("4.2 他人情绪激励", "的他人情绪激励"));
        assertThat(b1).isEqualTo("5.8");
        val b2 = textMatcher.findLabelText("很高", new TextMatcherOption("4.3 人际关系处理", "的人际关系处理"));
        assertThat(b2).isEqualTo("5.5");

        val t2 = textMatcher.findLineLabelText("作答时间：");
        assertThat(t2).isEqualTo("7分钟22秒");
        val t3 = textMatcher.findLineLabelText("正常与否", new TextMatcherOption(":：", "参考时间", "选项分布"));
        assertThat(t3).isEqualTo("正常");
        val t4 = textMatcher.findLineLabelText("正常与否", new TextMatcherOption(":：", "选项分布", "作答题数"));
        assertThat(t4).isEqualTo("正常");
        val t5 = textMatcher.findLineLabelText("完成率", new TextMatcherOption(":：", "全部题数", "%"));
        assertThat(t5).isEqualTo("100.00");
    }

    @Test @SneakyThrows
    public void 职业行为风险测验兵进黄() {
        @Cleanup val is = Util.loadClassPathRes("原始报告（样本）/智联/兵进黄_职业行为风险测验.pdf");
        @Cleanup val pdDoc = Util.loadPdf(is);
        val text = PdfStripper.stripText(pdDoc, PdfPagesSelect.allPages());
        val textMatcher = new TextMatcher(text);

        assertThat(textMatcher.findLineLabelText("测试者：")).isEqualTo("兵进黄");
        assertThat(textMatcher.findLineLabelText("测试日期：")).isEqualTo("2018-10-23");

        List<RiskItem> riskItems = textMatcher.searchPattern("(\\S+)：([高中低])\\s*(-?\\d+)", RiskItem.class);
        assertThat(riskItems.toString()).isEqualTo("[" +
                "RiskItem(name=焦虑不安, level=中, score=-58), " +
                "RiskItem(name=抑郁消沉, level=高, score=-58), " +
                "RiskItem(name=偏执多疑, level=高, score=-71), " +
                "RiskItem(name=冷漠孤僻, level=中, score=-43), " +
                "RiskItem(name=特立独行, level=高, score=-100), " +
                "RiskItem(name=冲动暴躁, level=高, score=-86), " +
                "RiskItem(name=喜怒无常, level=中, score=-44), " +
                "RiskItem(name=社交回避, level=高, score=-71), " +
                "RiskItem(name=僵化固执, level=高, score=-50), " +
                "RiskItem(name=依赖顺从, level=低, score=-38), " +
                "RiskItem(name=夸张做作, level=高, score=-100), " +
                "RiskItem(name=狂妄自恋, level=低, score=-33), " +
                "RiskItem(name=压力耐受, level=低, score=34), " +
                "RiskItem(name=积极乐观, level=高, score=72), " +
                "RiskItem(name=合理自信, level=低, score=48), " +
                "RiskItem(name=坚韧不拔, level=中, score=44)]");

        val t1 = textMatcher.findLabelText("正常与否：", new TextMatcherOption("作答时间", "称许性"));
        assertThat(t1).isEqualTo("正常");

        val t2 = textMatcher.findLabelText("共计", new TextMatcherOption("作答时间", "称许性"));
        assertThat(t2).isEqualTo("4分钟");
        val t3 = textMatcher.findPatternText("\\d+秒", new TextMatcherOption("作答时间", "称许性"), 0);
        assertThat(t3).isEqualTo("45秒");

        val t4 = textMatcher.findLabelText("正常与否", new TextMatcherOption(":：", "称许性", "选项分布"));
        assertThat(t4).isEqualTo("正常");

        val t5 = textMatcher.findLabelText("正常与否", new TextMatcherOption(":：", "选项分布", "作答完整性"));
        assertThat(t5).isEqualTo("正常");

        val t6 = textMatcher.findLabelText("完成率", new TextMatcherOption(":：", "作答题数", "%"));
        assertThat(t6).isEqualTo("100.00");
    }

    @Test @SneakyThrows
    public void 职业行为风险测验hjson() {
        val hjson = Util.loadClasspathResAsString("智联.3577.职业行为风险测验.hjson");
        val json = JsonValue.readHjson(hjson).toString();
        val config = JSON.parseObject(json, TextTripperConfig.class);

        @Cleanup val is = Util.loadClassPathRes("原始报告（样本）/智联/职业行为风险测验（样本）.pdf");
        @Cleanup val pdDoc = Util.loadPdf(is);
        List<TextItem> items = PdfStripper.strip(pdDoc, config);
        assertThat(items.toString()).isEqualTo("[" +
                "TextItem(name=结果可参考性, value=高, desc=null), " +
                "TextItem(name=职业行为风险等级, value=低风险低防御, desc=null), " +
                "TextItem(name=焦虑不安, value=低, desc=0), " +
                "TextItem(name=抑郁消沉, value=低, desc=0), " +
                "TextItem(name=偏执多疑, value=低, desc=0), " +
                "TextItem(name=冷漠孤僻, value=低, desc=-14), " +
                "TextItem(name=特立独行, value=低, desc=0), " +
                "TextItem(name=冲动暴躁, value=低, desc=0), " +
                "TextItem(name=喜怒无常, value=低, desc=0), " +
                "TextItem(name=社交回避, value=低, desc=0), " +
                "TextItem(name=僵化固执, value=低, desc=0), " +
                "TextItem(name=依赖顺从, value=低, desc=-12), " +
                "TextItem(name=夸张做作, value=低, desc=-12), " +
                "TextItem(name=狂妄自恋, value=低, desc=0), " +
                "TextItem(name=压力耐受, value=低, desc=54), " +
                "TextItem(name=积极乐观, value=低, desc=58), " +
                "TextItem(name=合理自信, value=中, desc=68), " +
                "TextItem(name=坚韧不拔, value=低, desc=42), " +
                "TextItem(name=称许性, value=正常, desc=null), " +
                "TextItem(name=选项分布, value=正常, desc=null), " +
                "TextItem(name=完成率, value=100%, desc=null), " +
                "TextItem(name=测试开始时间, value=2016-06-12 17:04:00, desc=null), " +
                "TextItem(name=测试完成时间, value=2016-06-13 09:39:00, desc=null), " +
                "TextItem(name=作答时间, value=16分钟45秒, desc=null)]");
    }

    @Test @SneakyThrows
    public void 职业行为风险测验() {
        @Cleanup val is = Util.loadClassPathRes("原始报告（样本）/智联/职业行为风险测验（样本）.pdf");
        @Cleanup val pdDoc = Util.loadPdf(is);
        val text = PdfStripper.stripText(pdDoc, PdfPagesSelect.allPages());
        val textMatcher = new TextMatcher(text);

        assertThat(textMatcher.findLineLabelText("测试者：")).isEqualTo("张晓平");
        assertThat(textMatcher.findLineLabelText("测试日期：")).isEqualTo("2016-06-12");

        List<RiskItem> riskItems = textMatcher.searchPattern("(\\S+)：([高中低])\\s*(-?\\d+)", RiskItem.class);
        assertThat(riskItems.toString()).isEqualTo("[" +
                "RiskItem(name=焦虑不安, level=低, score=0), " +
                "RiskItem(name=抑郁消沉, level=低, score=0), " +
                "RiskItem(name=偏执多疑, level=低, score=0), " +
                "RiskItem(name=冷漠孤僻, level=低, score=-14), " +
                "RiskItem(name=特立独行, level=低, score=0), " +
                "RiskItem(name=冲动暴躁, level=低, score=0), " +
                "RiskItem(name=喜怒无常, level=低, score=0), " +
                "RiskItem(name=社交回避, level=低, score=0), " +
                "RiskItem(name=僵化固执, level=低, score=0), " +
                "RiskItem(name=依赖顺从, level=低, score=-12), " +
                "RiskItem(name=夸张做作, level=低, score=-12), " +
                "RiskItem(name=狂妄自恋, level=低, score=0), " +
                "RiskItem(name=压力耐受, level=低, score=54), " +
                "RiskItem(name=积极乐观, level=低, score=58), " +
                "RiskItem(name=合理自信, level=中, score=68), " +
                "RiskItem(name=坚韧不拔, level=低, score=42)]");

        val t1 = textMatcher.findLabelText("正常与否：", new TextMatcherOption("作答时间", "称许性"));
        assertThat(t1).isEqualTo("正常");

        val t2 = textMatcher.findLabelText("共计", new TextMatcherOption("作答时间", "称许性"));
        assertThat(t2).isEqualTo("16分钟");
        val t3 = textMatcher.findPatternText("\\d+秒", new TextMatcherOption("作答时间", "称许性"), 0);
        assertThat(t3).isEqualTo("45秒");

        val a2 = textMatcher.findPatternText("\\d\\d\\d\\d-\\d\\d-\\d\\d", new TextMatcherOption("作答时间", "称许性"), 0);
        assertThat(a2).isEqualTo("2016-06-12");
        val a3 = textMatcher.findPatternText("\\d\\d\\d\\d-\\d\\d-\\d\\d", new TextMatcherOption("作答时间", "称许性"), 1);
        assertThat(a3).isEqualTo("2016-06-13");

        val b2 = textMatcher.findPatternText("\\d?\\d:\\d\\d", new TextMatcherOption("作答时间", "称许性"), 0);
        assertThat(b2).isEqualTo("17:04");
        val b3 = textMatcher.findPatternText("\\d?\\d:\\d\\d", new TextMatcherOption("作答时间", "称许性"), 1);
        assertThat(b3).isEqualTo("9:39");

        val t4 = textMatcher.findLabelText("正常与否", new TextMatcherOption(":：", "称许性", "选项分布"));
        assertThat(t4).isEqualTo("正常");

        val t5 = textMatcher.findLabelText("正常与否", new TextMatcherOption(":：", "选项分布", "作答完整性"));
        assertThat(t5).isEqualTo("正常");

        val t6 = textMatcher.findLabelText("完成率", new TextMatcherOption(":：", "作答题数", "%"));
        assertThat(t6).isEqualTo("100");
    }
}
