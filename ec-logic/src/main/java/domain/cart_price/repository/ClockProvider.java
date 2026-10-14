package domain.cart_price.repository;

import java.time.LocalDateTime;

public interface ClockProvider {
    LocalDateTime now();
}
