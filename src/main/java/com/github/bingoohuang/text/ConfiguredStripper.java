package com.github.bingoohuang.text;

import com.github.bingoohuang.text.model.TextItem;
import com.github.bingoohuang.text.model.TextTripperConfig;
import com.github.bingoohuang.text.model.TextTripperRule;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.mvel2.MVEL;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ConfiguredStripper {
    private final Map<Integer, String> pagedText;
    private final TextTripperConfig config;

    private List<TextItem> items = Lists.newArrayList();
    private Map<String, String> temps = Maps.newHashMap();

    public List<TextItem> strip() {
        processRules();
        processEvals();
        return items;
    }

    private void processEvals() {
        if (config.getEvals() == null) return;

        for (val e : config.getEvals()) {
            if (StringUtils.isNotEmpty(e.getCondition())
                    && !MVEL.evalToBoolean(e.getCondition(), temps)) break;

            val value = MVEL.evalToString(e.getExpr(), temps);
            items.add(new TextItem(e.getName(), value, null));
        }
    }

    private void processRules() {
        if (config.getRules() == null) return;

        for (val rule : config.getRules()) {
            val text = rule.getPages() == null
                    ? pagedText.get(0) : getPagesText(rule.getPages());
            val textMatcher = new TextMatcher(text);

            new RuleExecutor(rule, textMatcher).execute();
        }
    }

    private String getPagesText(List<Integer> pages) {
        val text = new StringBuilder();
        pages.forEach(x -> text.append(pagedText.get(x)));
        return text.toString();
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
                val value = textMatcher.findPatternText(l.getPattern(), createTextMatcherOption(l), l.getIndex(), l.getValueIndex());
                val filteredValue = Filter.filter(value, l.getValueFilters());
                val name = Filter.filter(l.getName(), l.getNameFilters());

                if (l.isTemp()) {
                    temps.put(name, filteredValue);
                } else {
                    items.add(new TextItem(name, filteredValue, null));
                }
            }
        }

        private void processSearchPatterns() {
            if (rule.getSearchPatterns() == null) return;

            for (val l : rule.getSearchPatterns()) {
                textMatcher.searchPattern(l.getPattern(), patternGroups -> {
                    val name = patternGroups[l.getNameIndex() - 1];
                    if (!MameMatcher.nameMatch(name, l.getNameMatchers())) {
                        return;
                    }
                    val value = patternGroups[l.getValueIndex() - 1];

                    val filterName = Filter.filter(name, l.getNameFilters());
                    val filterValue = Filter.filter(value, l.getValueFilters());

                    if (l.isTemp()) {
                        temps.put(filterName, filterValue);
                    } else {
                        val desc = l.getDescIndex() > 0 ? patternGroups[l.getDescIndex() - 1] : null;
                        items.add(new TextItem(filterName, filterValue, desc));
                    }

                    if (StringUtils.isNotEmpty(l.getTempVarsMap())) {
                        val parts = Splitter.onPattern("\\s+").splitToList(l.getTempVarsMap());
                        for (int i = 0, ii = parts.size(); i + 1 < ii; i += 2) {
                            if (parts.get(i).equals(filterName)) {
                                temps.put(parts.get(i + 1), filterValue);
                                break;
                            }
                        }
                    }

                }, createTextMatcherOption(l));
            }
        }

        private void processLabelTexts() {
            if (rule.getLabelTexts() == null) return;

            for (val l : rule.getLabelTexts()) {
                val value = textMatcher.findLabelText(l.getLabel(), createTextMatcherOption(l));
                val filteredValue = Filter.filter(value, l.getValueFilters());

                if (l.isTemp()) {
                    temps.put(l.getName(), filteredValue);
                } else {
                    items.add(new TextItem(l.getName(), filteredValue, null));
                }
            }
        }

        private void processLineLabelTexts() {
            if (rule.getLineLabelTexts() == null) return;

            for (val l : rule.getLineLabelTexts()) {
                String value = textMatcher.findLineLabelText(l.getLabel(), createTextMatcherOption(l));

                val name = Filter.filter(l.getName(), l.getNameFilters());
                value = Filter.filter(value, l.getValueFilters());

                if (l.isTemp()) {
                    temps.put(name, value);
                } else {
                    items.add(new TextItem(name, value, null));
                }
            }
        }

        private TextMatcherOption createTextMatcherOption(AnchorAware l) {
            return TextMatcherOption.builder()
                    .startAnchor(l.getStartAnchor())
                    .endAnchor(l.getEndAnchor())
                    .stripChars(config.getStripChars())
                    .build();
        }

    }
}
