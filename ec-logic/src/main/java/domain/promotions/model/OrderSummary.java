package domain.promotions.model;

import java.math.BigDecimal;

public record OrderSummary(
        BigDecimal rawTotal,            // 割引前合計
        BigDecimal bundleDiscount,       // まとめ買い割引額
        BigDecimal couponDiscount,       // クーポン割引額
        BigDecimal productFinalAmount,   // 最終商品金額
        BigDecimal shippingFee,          // 送料
        BigDecimal finalOrderAmount      // 最終請求金額（総合計）
) {}

