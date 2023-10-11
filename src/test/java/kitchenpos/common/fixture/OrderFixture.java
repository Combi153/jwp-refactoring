package kitchenpos.common.fixture;

import java.time.LocalDateTime;
import java.util.List;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;

@SuppressWarnings("NonAsciiCharacters")
public class OrderFixture {

    public static Order 주문(Long orderTableId) {
        return new Order(orderTableId, OrderStatus.COOKING.name(), LocalDateTime.MAX, List.of());
    }

    public static Order 주문(Long orderTableId, String orderStatus) {
        return new Order(orderTableId, orderStatus, LocalDateTime.MAX, List.of());
    }

    public static Order 주문(Long orderId, Long orderTableId) {
        return new Order(orderId, orderTableId, OrderStatus.COOKING.name(), LocalDateTime.MAX, List.of());
    }
}
