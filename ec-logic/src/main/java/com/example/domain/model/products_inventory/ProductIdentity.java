package com.example.domain.model.products_inventory;

import java.time.LocalDateTime;

public record ProductIdentity(String id, String name, LocalDateTime salesStartPeriod, LocalDateTime salesEndPeriod) {
    // コンパクト・コンストラクタ（Colorの時と同じ仕組み）
    public ProductIdentity {
        // どちらかが未入力（null）の場合はエラー
        if (salesStartPeriod == null || salesEndPeriod == null) {
            throw new IllegalArgumentException("販売期間の開始日時と終了日時は必須です。");
        }

        // 開始日時が、終了日時よりも「後（after）」になっていたらエラー
        if (salesStartPeriod.isAfter(salesEndPeriod)) {
            throw new IllegalArgumentException("販売開始日時は、販売終了日時より前の時間を指定してください。");
        }

    }
}
