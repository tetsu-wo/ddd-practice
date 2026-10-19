package com.example.domain.model.products_inventory; // SKUと同じパッケージ

import java.util.ArrayList;
import java.util.List;

public class ProductTestFactory {
    /**
     * インフラ層から呼べる、ダミーのSKUリストを作成するパブリックメソッド
     */
    public static List<SKU> createDummySkus() {
        List<SKU> skus = new ArrayList<>();
        // 同じパッケージ内なので、パッケージプライベートなSKUをnewできます
        // (※実際のSKUの引数に合わせて調整してください)
        skus.add(new SKU(new SkuIdentity("SKU-001", new Color("赤"),new Size("M")), 3000));
        return skus;
    }
}

