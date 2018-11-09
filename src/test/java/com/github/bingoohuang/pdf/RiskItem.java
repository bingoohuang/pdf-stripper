package com.github.bingoohuang.pdf;

import com.github.bingoohuang.utils.text.matcher.PatternApplyAware;
import lombok.Data;

@Data
public class RiskItem implements PatternApplyAware {
    private String name;
    private String level; // 高中低
    private int score;

    @Override public void apply(String[] groups) {
        this.name = groups[0];
        this.level = groups[1];
        this.score = Integer.parseInt(groups[2]);
    }
}
