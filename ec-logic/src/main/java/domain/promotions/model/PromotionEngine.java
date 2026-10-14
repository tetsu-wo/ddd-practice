package domain.promotions.model;

import domain.promotions.repository.DiscountStrategy;
import domain.promotions.repository.ShippingCalculator;

import java.math.BigDecimal;
import java.util.List;

public class PromotionEngine {
    private final ShippingCalculator shippingCalculator;

    public PromotionEngine(ShippingCalculator shippingCalculator) {
        this.shippingCalculator = shippingCalculator;
    }

    public OrderSummary calculate(Cart cart, List<DiscountStrategy> strategies, BigDecimal couponDiscountAmount) {
        // 1. 商品単体の値引き・まとめ買い割引の適用
        BigDecimal totalBundleDiscount = strategies.stream()
                .map(strategy -> strategy.calculateDiscount(cart))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal amountAfterBundle = cart.getRawTotalAmount().subtract(totalBundleDiscount);

        // 2. クーポン割引の適用 (まとめ買い後の金額から引く。マイナス値は0に丸める)
        BigDecimal finalProductAmount = amountAfterBundle.subtract(couponDiscountAmount);
        if (finalProductAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalProductAmount = BigDecimal.ZERO;
        }

        // 3. 送料無料判定 (すべての割引が引かれた最終的な商品金額で判定)
        BigDecimal shippingFee = shippingCalculator.calculateShipping(finalProductAmount);

        // 4. 最終請求金額の算出
        BigDecimal finalOrderAmount = finalProductAmount.add(shippingFee);

        return new OrderSummary(
                cart.getRawTotalAmount(),
                totalBundleDiscount,
                couponDiscountAmount,
                finalProductAmount,
                shippingFee,
                finalOrderAmount
        );
    }
}

