package com.github.bingoohuang.pdf.model;

import lombok.Data;

import java.util.List;

@Data
public class TextTripperRule {
    private List<Integer> pages;
    private List<LabelText> lineLabelTexts;
    private List<LabelText> labelTexts;
    private List<SearchPattern> searchPatterns;
    private List<PatternText> patternTexts;
}
