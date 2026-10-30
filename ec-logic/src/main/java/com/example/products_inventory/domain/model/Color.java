package com.example.products_inventory.domain.model;

public record Color(String value) {
    public Color {
        if (!value.equals("赤") && !value.equals("青")) {
            throw new IllegalArgumentException("不正なカラーです");
        }

    }
}
