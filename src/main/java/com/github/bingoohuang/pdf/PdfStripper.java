package com.github.bingoohuang.pdf;

import com.github.bingoohuang.pdf.model.TextItem;
import com.github.bingoohuang.pdf.model.TextTripperConfig;
import com.github.bingoohuang.pdf.model.TextTripperRule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.output.NullWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.mvel2.MVEL;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class PdfStripper {
    static {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
        System.setProperty("org.apache.pdfbox.rendering.UsePureJavaCMYKConversion", "true");
    }

    /**
     * 自定义提取。
     *
     * @param pdDoc       PDF文档对象
     * @param pageSelect  指定页面
     * @param pdfListener 监听器
     */
    @SneakyThrows
    public static void stripCustom(PDDocument pdDoc, PdfPagesSelect pageSelect, PdfListener pdfListener) {
        @Cleanup val doc = pageSelect.getPDDocument(pdDoc);

        val tree = new PdfDom(pdfListener);

        Writer output = pdfListener.createHtml()
                ? new PrintWriter("debug" + System.currentTimeMillis() + ".html", "utf-8")
                : new NullWriter();
        tree.writeText(doc, output);
        output.close();
    }

    /**
     * 从PDF中提取布局好的文本。
     *
     * @param pdDoc      PDF文档对象
     * @param pageSelect 指定页面
     * @return 布局好的文本
     */
    @SneakyThrows
    public static String stripText(PDDocument pdDoc, PdfPagesSelect pageSelect) {
        @Cleanup val doc = pageSelect.getPDDocument(pdDoc);

        return new PDFLayoutTextStripper().getText(doc);
    }


    /**
     * 提取图片。
     *
     * @param pdDoc        PDF文档对象
     * @param pageIndex    指定页面索引
     * @param imageIndices 图片序号
     * @return 图片列表
     */
    @SneakyThrows
    public static List<PdfImage> stripImages(PDDocument pdDoc, int pageIndex, int... imageIndices) {
        val page = pdDoc.getPage(pageIndex);
        val images = Lists.<PdfImage>newArrayList();
        val resources = page.getResources();

        int imageIndex = 0;
        for (val xobj : resources.getXObjectNames()) {
            if (resources.isImageXObject(xobj)) {
                val img = (PDImageXObject) resources.getXObject(xobj);
                if (imageIndices.length > 0 && !Ints.contains(imageIndices, imageIndex++)) continue;

                images.add(new PdfImage(img.getImage(), xobj.getName(), img.getSuffix(), img.getHeight(), img.getWidth()));
            }
        }

        return images;
    }


    /**
     * 根据文本提取配置，从文本中获取指定属性。
     *
     * @param pdDoc  PDF文档对象
     * @param config 文本提取配置
     * @return 文本对象
     */
    public static List<TextItem> strip(PDDocument pdDoc, TextTripperConfig config) {
        return new ConfiguredStripper(pdDoc, config).strip();
    }

    private static class ConfiguredStripper {
        private final PDDocument pdDoc;
        private final TextTripperConfig config;
        private final List<TextItem> items = Lists.newArrayList();
        private final Map<String, String> temps = Maps.newHashMap();

        ConfiguredStripper(PDDocument pdDoc, TextTripperConfig config) {
            this.pdDoc = pdDoc;
            this.config = config;
        }

        List<TextItem> strip() {
            processRules();
            proessEvals();
            return items;
        }

        private void proessEvals() {
            if (config.getEvals() == null) return;

            for (val e : config.getEvals()) {
                val value = MVEL.evalToString(e.getExpr(), temps);
                items.add(new TextItem(e.getName(), value, null));
            }
        }

        private void processRules() {
            if (config.getRules() == null) return;

            for (val rule : config.getRules()) {
                val pages = rule.getPages().stream().mapToInt(i -> i - 1).toArray();
                val text = stripText(pdDoc, PdfPagesSelect.onPages(pages));
                val textMatcher = new TextMatcher(text);

                new RuleExecutor(rule, textMatcher).execute();
            }
        }


        @RequiredArgsConstructor
        private class RuleExecutor {
            private final TextTripperRule rule;
            private final TextMatcher textMatcher;

            void execute() {
                processLineLabelTexts();
                processLabelTexts();
                processSearchPatterns();
                processPatternTexts();
            }

            private void processPatternTexts() {
                if (rule.getPatternTexts() == null) return;

                for (val l : rule.getPatternTexts()) {
                    val value = textMatcher.findPatternText(l.getPattern(), TextMatcherOption.builder()
                            .startAnchor(l.getStartAnchor())
                            .endAnchor(l.getEndAnchor())
                            .stripChars(config.getStripChars())
                            .build(), l.getIndex());
                    if (l.isTemp()) {
                        temps.put(l.getName(), value);
                    } else {
                        items.add(new TextItem(l.getName(), value, null));
                    }
                }
            }

            private void processSearchPatterns() {
                if (rule.getSearchPatterns() == null) return;

                for (val l : rule.getSearchPatterns()) {
                    textMatcher.searchPattern(l.getPattern(), patternGroups -> {
                        val name = patternGroups[l.getNameIndex() - 1];
                        val value = patternGroups[l.getValueIndex() - 1];
                        if (l.isTemp()) {
                            temps.put(name, value);
                        } else {
                            val desc = l.getDescIndex() > 0 ? patternGroups[l.getDescIndex() - 1] : null;
                            items.add(new TextItem(name, value, desc));
                        }
                    }, TextMatcherOption.builder()
                            .startAnchor(l.getStartAnchor())
                            .endAnchor(l.getEndAnchor())
                            .stripChars(config.getStripChars())
                            .build());
                }
            }

            private void processLabelTexts() {
                if (rule.getLabelTexts() == null) return;

                for (val l : rule.getLabelTexts()) {
                    val value = textMatcher.findLabelText(l.getLabel(), TextMatcherOption.builder()
                            .startAnchor(l.getStartAnchor())
                            .endAnchor(l.getEndAnchor())
                            .stripChars(config.getStripChars())
                            .build());
                    if (l.isTemp()) {
                        temps.put(l.getName(), value);
                    } else {
                        items.add(new TextItem(l.getName(), value, null));
                    }
                }
            }

            private void processLineLabelTexts() {
                if (rule.getLineLabelTexts() == null) return;

                for (val l : rule.getLineLabelTexts()) {
                    String value = textMatcher.findLineLabelText(l.getLabel(), TextMatcherOption.builder()
                            .startAnchor(l.getStartAnchor())
                            .endAnchor(l.getEndAnchor())
                            .stripChars(config.getStripChars())
                            .build());
                    if (StringUtils.contains(l.getOptions(), "compactBlanks")) {
                        value = compactBlanks(value);
                    }
                    if (l.isTemp()) {
                        temps.put(l.getName(), value);
                    } else {
                        items.add(new TextItem(l.getName(), value, null));
                    }
                }
            }

        }
    }

    private static String compactBlanks(String value) {
        return value.replaceAll("\\s\\s+", " ");
    }
}
