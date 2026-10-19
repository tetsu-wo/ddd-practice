package com.example.domain.model.products_inventory;

public record Size(String value) {
    public Size {
        if (!value.equals("S") && !value.equals("M") && !value.equals("L")) {
            throw new IllegalArgumentException("不正なサイズです。");
        }
    }
}
