package com.github.bingoohuang.api;

import com.alibaba.fastjson.JSON;
import com.github.bingoohuang.utils.text.matcher.TextMatcher;
import com.github.bingoohuang.utils.text.matcher.model.TextTripperConfig;
import com.github.bingoohuang.utils.lang.Classpath;
import lombok.val;
import org.hjson.JsonValue;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class 智联ApiResultTest {
    @Test
    public void page238() {
        val hjson = Classpath.loadResAsString("智联-api.hjson");
        val json = JsonValue.readHjson(hjson).toString();
        val config = JSON.parseObject(json, TextTripperConfig.class);
        val textMatcher1 = new TextMatcher(Classpath.loadResAsString("智联-238-api-result-formatted.json"));
        val items1 = textMatcher1.strip(config);
        assertThat(items1.toString()).isEqualTo("[" +
                "TextItem(name=自我情绪认知, value=1, desc=null), " +
                "TextItem(name=自我驱动, value=1, desc=null), " +
                "TextItem(name=自我妥协, value=9.1, desc=null), " +
                "TextItem(name=敏感易变, value=9, desc=null), " +
                "TextItem(name=合理控制, value=1, desc=null), " +
                "TextItem(name=他人情绪感知, value=2.3, desc=null), " +
                "TextItem(name=个体关怀, value=1, desc=null), " +
                "TextItem(name=团队激励, value=2.9, desc=null), " +
                "TextItem(name=灵活适应, value=1, desc=null), " +
                "TextItem(name=消极回避, value=9.2, desc=null), " +
                "TextItem(name=自我情绪认知, value=1, desc=null), " +
                "TextItem(name=自我情绪激励, value=1, desc=null), " +
                "TextItem(name=自我情绪调控, value=1, desc=null), " +
                "TextItem(name=他人情绪感知, value=1.2, desc=null), " +
                "TextItem(name=他人情绪激励, value=1.3, desc=null), " +
                "TextItem(name=人际关系处理, value=1, desc=null), " +
                "TextItem(name=自我情绪管理能力, value=1, desc=null), " +
                "TextItem(name=他人情绪管理能力, value=1, desc=null), " +
                "TextItem(name=总分, value=1, desc=null)]");

        val textMatcher2 = new TextMatcher(Classpath.loadResAsString("智联-238-api-result-compact.json"));
        val items2 = textMatcher2.strip(config);
        assertThat(items1).isEqualTo(items2);


        val textMatcher3 = new TextMatcher(Classpath.loadResAsString("智联-2998-api-result-formatted.json"));
        val items3 = textMatcher3.strip(config);
        assertThat(items3.toString()).isEqualTo("[" +
                "TextItem(name=管理他人, value=4.3, desc=null), " +
                "TextItem(name=获得成就, value=4.2, desc=null), " +
                "TextItem(name=施展才华, value=4.8, desc=null), " +
                "TextItem(name=独立自主, value=4.8, desc=null), " +
                "TextItem(name=他人认可, value=4.3, desc=null), " +
                "TextItem(name=薪酬福利, value=4.3, desc=null), " +
                "TextItem(name=培训机会, value=4.6, desc=null), " +
                "TextItem(name=上级支持, value=4.6, desc=null), " +
                "TextItem(name=公平公正, value=4.5, desc=null), " +
                "TextItem(name=晋升机会, value=4.8, desc=null), " +
                "TextItem(name=工作稳定, value=4.5, desc=null), " +
                "TextItem(name=服务他人, value=4.3, desc=null), " +
                "TextItem(name=工作强度, value=4.5, desc=null), " +
                "TextItem(name=同事关系, value=4.2, desc=null), " +
                "TextItem(name=总分, value=0, desc=null)]");


        val textMatcher4 = new TextMatcher(Classpath.loadResAsString("智联-2998-api-result-compact.json"));
        val items4 = textMatcher4.strip(config);
        assertThat(items3).isEqualTo(items4);

        val textMatcher5 = new TextMatcher(Classpath.loadResAsString("智联-3577-api-result.json"));
        val items5 = textMatcher5.strip(config);
        assertThat(items5.toString()).isEqualTo("[TextItem(name=总分, value=1, desc=null)]");
    }
}
