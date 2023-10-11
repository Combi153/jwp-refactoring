package kitchenpos.common.fixture;

import java.math.BigDecimal;
import java.math.RoundingMode;
import kitchenpos.domain.Product;

@SuppressWarnings("NonAsciiCharacters")
public class ProductFixture {

    public static Product 상품() {
        return new Product("productName", BigDecimal.valueOf(1).setScale(2, RoundingMode.HALF_UP));
    }

    public static Product 상품(Long productId) {
        return new Product(productId, "productName", BigDecimal.valueOf(1).setScale(2, RoundingMode.HALF_UP));
    }

    public static Product 상품(BigDecimal price) {
        return new Product("productName", price);
    }

    public static Product 상품(String productName, BigDecimal price) {
        return new Product("productName", price);
    }
}
