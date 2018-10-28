package com.github.bingoohuang.text.model;

public interface FiltersAware {
    default String getNameFilters() {
        return null;
    }

    default String getValueFilters() {
        return null;
    }
}
