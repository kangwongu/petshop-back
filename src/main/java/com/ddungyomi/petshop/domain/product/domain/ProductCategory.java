package com.ddungyomi.petshop.domain.product.domain;

import java.util.Arrays;

public enum ProductCategory {

    PRINTING(1, "인쇄류"),
    LIFESTYLE(2, "생활용품"),
    ACCESSORY(3, "액세서리");

    private final Integer code;
    private final String label;

    ProductCategory(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    public static ProductCategory fromCode(Integer code) {
        return Arrays.stream(values())
                .filter(category -> category.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 코드입니다: " + code));
    }

    public Integer code() {
        return code;
    }

    public String label() {
        return label;
    }
}
