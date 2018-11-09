package com.github.bingoohuang.pdf;

import com.github.bingoohuang.utils.text.matcher.PatternApplyAware;
import lombok.Data;

@Data
public class ValuesItem implements PatternApplyAware {
    private String name;
    private String score;

    @Override public void apply(String[] groups) {
        this.name = groups[0];
        this.score = groups[1];
    }
}
