package com.example.products_inventory.domain.model;

public record Size(String value) {
    public Size {
        if (!value.equals("S") && !value.equals("M") && !value.equals("L")) {
            throw new IllegalArgumentException("不正なサイズです。");
        }
    }
}
