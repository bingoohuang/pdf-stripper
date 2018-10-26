package com.github.bingoohuang.pdf.model;

import lombok.Data;

import java.util.List;

@Data
public class TextTripperConfig {
    private String stripChars;
    private List<TextTripperRule> rules;
    private List<TextTripperEval> evals;
}
