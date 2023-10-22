package kitchenpos.common.fixture;

import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.Quantity;

@SuppressWarnings("NonAsciiCharacters")
public class OrderLineItemFixture {


    public static OrderLineItem 주문_항목(Long menuId) {
        return new OrderLineItem(menuId, Quantity.valueOf(1L));
    }

    public static OrderLineItem 주문_항목(Long menuId, Long orderId) {
        return new OrderLineItem(menuId, orderId, Quantity.valueOf(1L));
    }

    public static OrderLineItem 주문_항목(Long orderLineItemId, Long menuId, Long orderId) {
        return new OrderLineItem(orderLineItemId, menuId, orderId, Quantity.valueOf(1L));
    }
}
