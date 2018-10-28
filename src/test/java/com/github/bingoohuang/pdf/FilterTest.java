package com.github.bingoohuang.pdf;

import com.github.bingoohuang.text.Filter;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class FilterTest {
    @Test
    public void filter() {
        assertThat(Filter.filter("1.0", "@trimTrailingZeroes")).isEqualTo("1");
    }
}
