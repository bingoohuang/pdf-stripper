package com.github.bingoohuang.mail;

import com.github.bingoohuang.pdf.*;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class Pop3MailFetcherTest {
    @Test @SneakyThrows
    public void test() {
        val mailFetcher = new Pop3MailFetcher("pop.ym.163.com", "110",
                "hn.test@raiyee.com", "E5540A062508", new MailMatcher() {
            @Override public int messageStart() {
                return 3;
            }

            @Override public int messageEnd() {
                return 3;
            }

            @Override public boolean matchAttachmentFileName(String fileName) {
                return StringUtils.contains(fileName, "EcHPIHDSMVPIFR-Global");
            }
        });

        List<Pop3MailMessage> messages = mailFetcher.fetchMails();
        assertThat(messages).hasSize(1);
        val message = messages.get(0);

        assertThat(message.getFrom()).isNotEmpty();
        assertThat(message.getSubject()).isNotEmpty();
        assertThat(message.getMessageNumber()).isEqualTo(3);
        assertThat(message.getSendDateTime()).isLessThan(DateTime.now());

        val contentMatcher = new TextMatcher(message.getContent());
        val userId = contentMatcher.findLabelText("User ID",
                new TextMatcherOption(":", null, "Group Name"));

        assertThat(userId).isEqualTo("HF322468");

        assertThat(message.getAttachments()).hasSize(1);

        val pdf = message.getAttachments().get(0);
        assertThat(pdf.getFileName()).contains("EcHPIHDSMVPIFR-Global");
        @Cleanup val pdDoc = PdfStripper.loadPdDocument(pdf.getInputStream());
        val text = PdfStripper.stripText(pdDoc, PdfPagesSelect.onPages(0));
        val textMatcher = new TextMatcher(text);
        val items = textMatcher.searchPattern("(\\S+)\\s+(\\d+)", ValuesItem.class,
                TextMatcherOption.builder().startAnchor("简报").endAnchor("©").build());

        assertThat(items.toString()).isEqualTo("[" +
                "ValuesItem(name=调适, score=44), " +
                "ValuesItem(name=抱负, score=7), " +
                "ValuesItem(name=社交, score=7), " +
                "ValuesItem(name=人际敏感度, score=3), " +
                "ValuesItem(name=审慎, score=80), " +
                "ValuesItem(name=好奇, score=94), " +
                "ValuesItem(name=学习方式, score=84), " +
                "ValuesItem(name=激动, score=37), " +
                "ValuesItem(name=多疑, score=53), " +
                "ValuesItem(name=谨慎, score=99), " +
                "ValuesItem(name=内敛, score=100), " +
                "ValuesItem(name=消极, score=68), " +
                "ValuesItem(name=自大, score=70), " +
                "ValuesItem(name=狡猾, score=12), " +
                "ValuesItem(name=戏剧化, score=1), " +
                "ValuesItem(name=幻想, score=49), " +
                "ValuesItem(name=苛求, score=69), " +
                "ValuesItem(name=恭顺, score=73), " +
                "ValuesItem(name=认可, score=25), " +
                "ValuesItem(name=权力, score=6), " +
                "ValuesItem(name=享乐, score=77), " +
                "ValuesItem(name=利他, score=23), " +
                "ValuesItem(name=归属, score=1), " +
                "ValuesItem(name=传统, score=4), " +
                "ValuesItem(name=保障, score=34), " +
                "ValuesItem(name=商业, score=27), " +
                "ValuesItem(name=美感, score=92), " +
                "ValuesItem(name=科学, score=93)]");

        val pdfListener = new HogonPdfListener();
        PdfStripper.stripCustom(pdDoc, PdfPagesSelect.onPages(1), pdfListener);

        assertThat(pdfListener.itemScores()).isEqualTo("效度:4\n" +
                "调适.同理心:3\n" +
                "调适.不焦虑:3\n" +
                "调适.不内疚:1\n" +
                "调适.冷静:3\n" +
                "调适.性情平和:4\n" +
                "调适.无抱怨:4\n" +
                "调适.信赖他人:3\n" +
                "调适.依附感:2\n" +
                "抱负.好竞争:1\n" +
                "抱负.自信:1\n" +
                "抱负.成就感:2\n" +
                "抱负.领导力:1\n" +
                "抱负.认同:4\n" +
                "抱负.无社交焦虑:2\n" +
                "社交.喜欢派对:0\n" +
                "社交.喜欢人群:0\n" +
                "社交.寻求体验:3\n" +
                "社交.表现欲:3\n" +
                "社交.风趣:0\n" +
                "人际敏感度.容易相处:2\n" +
                "人际敏感度.敏感:2\n" +
                "人际敏感度.关怀:2\n" +
                "人际敏感度.喜欢他人:0\n" +
                "人际敏感度.无恶意:4\n" +
                "审慎.循规蹈矩:3\n" +
                "审慎.精通掌握:3\n" +
                "审慎.尽责:4\n" +
                "审慎.不独立自主:4\n" +
                "审慎.计划性:4\n" +
                "审慎.控制冲动:3\n" +
                "审慎.避免麻烦:2\n" +
                "好奇.科学能力:4\n" +
                "好奇.好奇心:2\n" +
                "好奇.寻求刺激:4\n" +
                "好奇.智力游戏:4\n" +
                "好奇.想出主意:3\n" +
                "好奇.文化:4\n" +
                "学习方式.教育:4\n" +
                "学习方式.数学能力:4\n" +
                "学习方式.记忆力:3\n" +
                "学习方式.阅读:4\n" +
                "激动.情绪无常:2\n" +
                "激动.容易失望:2\n" +
                "激动.缺乏目标:3\n" +
                "多疑.愤世嫉俗:2\n" +
                "多疑.不信任/猜忌:3\n" +
                "多疑.记仇:2\n" +
                "谨慎.回避:4\n" +
                "谨慎.胆怯:4\n" +
                "谨慎.不果断坚决:4\n" +
                "内敛.内向:4\n" +
                "内敛.不善交际:4\n" +
                "内敛.强硬:4\n" +
                "消极.消极抵抗:4\n" +
                "消极.不被赏识:2\n" +
                "消极.容易激怒:2\n" +
                "自大.特权:3\n" +
                "自大.过度自信:3\n" +
                "自大.自认天资聪颖:2\n" +
                "狡猾.爱冒险:1\n" +
                "狡猾.冲动:1\n" +
                "狡猾.工于心计:3\n" +
                "戏剧化.在公众场合自信:1\n" +
                "戏剧化.容易分心:1\n" +
                "戏剧化.自我炫耀:1\n" +
                "幻想.想法怪异:2\n" +
                "幻想.异常敏感:2\n" +
                "幻想.创造性思维:2\n" +
                "苛求.高标准:3\n" +
                "苛求.完美主义:2\n" +
                "苛求.有条理:2\n" +
                "恭顺.优柔寡断:3\n" +
                "恭顺.阿谀奉承:3\n" +
                "恭顺.顺从依赖:2\n" +
                "认可.生活方式:2\n" +
                "认可.信念:1\n" +
                "认可.职业偏好:3\n" +
                "认可.反感:1\n" +
                "认可.偏好的共事者:4\n" +
                "权力.生活方式:1\n" +
                "权力.信念:2\n" +
                "权力.职业偏好:2\n" +
                "权力.反感:2\n" +
                "权力.偏好的共事者:1\n" +
                "享乐.生活方式:4\n" +
                "享乐.信念:1\n" +
                "享乐.职业偏好:4\n" +
                "享乐.反感:2\n" +
                "享乐.偏好的共事者:3\n" +
                "利他.生活方式:1\n" +
                "利他.信念:2\n" +
                "利他.职业偏好:4\n" +
                "利他.反感:1\n" +
                "利他.偏好的共事者:4\n" +
                "归属.生活方式:0\n" +
                "归属.信念:1\n" +
                "归属.职业偏好:1\n" +
                "归属.反感:1\n" +
                "归属.偏好的共事者:3\n" +
                "传统.生活方式:1\n" +
                "传统.信念:1\n" +
                "传统.职业偏好:3\n" +
                "传统.反感:1\n" +
                "传统.偏好的共事者:2\n" +
                "保障.生活方式:2\n" +
                "保障.信念:2\n" +
                "保障.职业偏好:3\n" +
                "保障.反感:3\n" +
                "保障.偏好的共事者:2\n" +
                "商业.生活方式:2\n" +
                "商业.信念:1\n" +
                "商业.职业偏好:4\n" +
                "商业.反感:1\n" +
                "商业.偏好的共事者:2\n" +
                "美感.生活方式:4\n" +
                "美感.信念:4\n" +
                "美感.职业偏好:4\n" +
                "美感.反感:4\n" +
                "美感.偏好的共事者:4\n" +
                "科学.生活方式:4\n" +
                "科学.信念:3\n" +
                "科学.职业偏好:4\n" +
                "科学.反感:4\n" +
                "科学.偏好的共事者:4");
    }
}

