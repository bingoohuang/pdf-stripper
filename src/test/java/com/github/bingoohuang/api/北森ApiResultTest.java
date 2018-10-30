package com.github.bingoohuang.api;

import com.alibaba.fastjson.JSON;
import com.github.bingoohuang.pdf.Util;
import com.github.bingoohuang.text.TextMatcher;
import com.github.bingoohuang.text.model.TextTripperConfig;
import lombok.val;
import org.hjson.JsonValue;
import org.junit.Test;

import java.math.BigDecimal;

import static com.google.common.truth.Truth.assertThat;

public class 北森ApiResultTest {
    @Test
    public void test401081() {
        val textMatcher1 = new TextMatcher(Util.loadClasspathResAsString("北森-401081-CATA一轮.json"));
        val items1 = textMatcher1.strip(getStripConfig());
        assertThat(items1.toString()).isEqualTo("[" +
                "TextItem(name=综合得分, value=38, desc=null), " +
                "TextItem(name=言语能力, value=78, desc=null), " +
                "TextItem(name=数学能力, value=50, desc=null), " +
                "TextItem(name=逻辑推理, value=18, desc=null), " +
                "TextItem(name=空间能力, value=6, desc=null), " +
                "TextItem(name=测试完成时间, value=2018-10-24 11:38:16, desc=null)]");
    }

    @Test
    public void test389051() {
        val textMatcher1 = new TextMatcher(Util.loadClasspathResAsString("北森-389051-认知能力测试.json"));
        val items1 = textMatcher1.strip(getStripConfig());
        assertThat(items1.toString()).isEqualTo("[" +
                "TextItem(name=综合得分, value=30, desc=null), " +
                "TextItem(name=言语能力, value=48, desc=null), " +
                "TextItem(name=逻辑推理, value=11, desc=null), " +
                "TextItem(name=测试完成时间, value=2018-10-24 11:49:03, desc=null)]");
    }

    @Test
    public void test387249() {
        val textMatcher1 = new TextMatcher(Util.loadClasspathResAsString("北森-387249-管理技能测试.json"));
        val items1 = textMatcher1.strip(getStripConfig());
        assertThat(items1.toString()).isEqualTo("[" +
                "TextItem(name=管理技能总分, value=2.5, desc=null), " +
                "TextItem(name=战略理解与执行, value=2.2, desc=null), " +
                "TextItem(name=目标设置, value=1.5, desc=null), " +
                "TextItem(name=规划安排, value=4, desc=null), " +
                "TextItem(name=时间管理, value=2.3, desc=null), " +
                "TextItem(name=任务分配, value=1.6, desc=null), " +
                "TextItem(name=授权管理, value=1.8, desc=null), " +
                "TextItem(name=团队管理, value=2.5, desc=null), " +
                "TextItem(name=决策判断, value=2.7, desc=null), " +
                "TextItem(name=激励推动, value=4.4, desc=null), " +
                "TextItem(name=培养下属, value=1.5, desc=null), " +
                "TextItem(name=沟通协调, value=3.2, desc=null), " +
                "TextItem(name=关系管理, value=1.7, desc=null), " +
                "TextItem(name=监查反馈, value=2.7, desc=null), " +
                "TextItem(name=应变调控, value=4.4, desc=null), " +
                "TextItem(name=绩效管理, value=1.5, desc=null), " +
                "TextItem(name=测试完成时间, value=2018-10-24 16:23:49, desc=null), " +
                "TextItem(name=计划管理技能, value=2.5, desc=null), " +
                "TextItem(name=组织管理技能, value=2, desc=null), " +
                "TextItem(name=领导管理技能, value=2.7, desc=null), " +
                "TextItem(name=控制管理技能, value=2.9, desc=null)]");
    }

    @Test
    public void roundup() {
        assertThat(new BigDecimal("1.9666667").setScale(1, BigDecimal.ROUND_HALF_UP).toString()).isEqualTo("2.0");
        assertThat(new BigDecimal("2.8666668").setScale(1, BigDecimal.ROUND_HALF_UP).toString()).isEqualTo("2.9");
    }

    @Test
    public void test404889() {
        val textMatcher1 = new TextMatcher(Util.loadClasspathResAsString("北森-404889-高潜.json"));
        val items1 = textMatcher1.strip(getStripConfig());
        assertThat(items1.toString()).isEqualTo("[" +
                "TextItem(name=管理个性测验, value=控制者型, desc=null), " +
                "TextItem(name=注重人际, value=4.7, desc=null), " +
                "TextItem(name=内控力, value=1.9, desc=null), " +
                "TextItem(name=注重达成, value=3, desc=null), " +
                "TextItem(name=内驱力, value=7.6, desc=null), " +
                "TextItem(name=践行抱负, value=4.9, desc=null), " +
                "TextItem(name=人际通达, value=7.9, desc=null), " +
                "TextItem(name=敏锐学习, value=7.6, desc=null), " +
                "TextItem(name=跨界思考, value=6.2, desc=null), " +
                "TextItem(name=抗压性, value=5.3, desc=null), " +
                "TextItem(name=影响的, value=6.4, desc=null), " +
                "TextItem(name=审慎的, value=2.2, desc=null), " +
                "TextItem(name=适应性, value=5.4, desc=null), " +
                "TextItem(name=意志力, value=6.1, desc=null), " +
                "TextItem(name=民主的, value=3.4, desc=null), " +
                "TextItem(name=理性的, value=4.7, desc=null), " +
                "TextItem(name=独立性, value=4.9, desc=null), " +
                "TextItem(name=社交自信, value=5.7, desc=null), " +
                "TextItem(name=对抗性, value=4.4, desc=null), " +
                "TextItem(name=亲和动机, value=9.8, desc=null), " +
                "TextItem(name=创新意识, value=6.9, desc=null), " +
                "TextItem(name=活力, value=7.9, desc=null), " +
                "TextItem(name=支持性, value=4.3, desc=null), " +
                "TextItem(name=成功愿望, value=6, desc=null), " +
                "TextItem(name=条理性, value=3, desc=null), " +
                "TextItem(name=洞察力, value=8, desc=null), " +
                "TextItem(name=同理心, value=9.6, desc=null), " +
                "TextItem(name=权力动机, value=7.8, desc=null), " +
                "TextItem(name=信任的, value=2.6, desc=null), " +
                "TextItem(name=情绪稳定性, value=5.4, desc=null), " +
                "TextItem(name=决断的, value=3.6, desc=null), " +
                "TextItem(name=乐观的, value=4, desc=null), " +
                "TextItem(name=责任感, value=3.9, desc=null), " +
                "TextItem(name=测试完成时间, value=2018-10-24 11:21:11, desc=null)]");
    }

    private TextTripperConfig getStripConfig() {
        val hjson = Util.loadClasspathResAsString("北森-api.hjson");
        val json = JsonValue.readHjson(hjson).toString();
        return JSON.parseObject(json, TextTripperConfig.class);
    }
}
