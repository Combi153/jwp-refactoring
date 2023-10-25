package kitchenpos.domain.table;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import kitchenpos.domain.order.Order;

@Entity
public class OrderTable {

    private static final int MINIMUM_NUMBER_OF_GUESTS = 0;

    @GeneratedValue(strategy = IDENTITY)
    @Id
    private Long id;

    private Long tableGroupId;

    @Column(nullable = false)
    private int numberOfGuests;

    @Column(name = "empty", nullable = false)
    private boolean isEmpty;

    @OneToMany(mappedBy = "orderTable", fetch = EAGER, cascade = PERSIST)
    private List<Order> orders;

    OrderTable(Long id, Long tableGroupId, int numberOfGuests, boolean isEmpty) {
        this.id = id;
        this.tableGroupId = tableGroupId;
        this.numberOfGuests = numberOfGuests;
        this.isEmpty = isEmpty;
        this.orders = new ArrayList<>();
    }

    OrderTable(Long tableGroupId, int numberOfGuests, boolean isEmpty) {
        this(null, tableGroupId, numberOfGuests, isEmpty);
    }

    protected OrderTable() {
    }

    public static OrderTable of(int numberOfGuests, boolean isEmpty) {
        return new OrderTable(null, numberOfGuests, isEmpty);
    }

    public void add(Order order) {
        orders.add(order);
        if (!Objects.equals(this, order.getOrderTable())) {
            order.register(this);
        }
    }

    public boolean contains(Order order) {
        return orders.contains(order);
    }

    public void changeIsEmpty(boolean isEmpty) {
        validateOrderTableIsGrouped();
        validateOrderTableHasCookingOrMealOrder();
        this.isEmpty = isEmpty;
    }

    private void validateOrderTableIsGrouped() {
        if (isGrouped()) {
            throw new IllegalArgumentException("단체 지정된 주문 테이블은 비어있는지 여부를 변경할 수 없습니다.");
        }
    }

    private void validateOrderTableHasCookingOrMealOrder() {
        if (hasCookingOrMealOrder()) {
            throw new IllegalArgumentException("조리 혹은 식사 중인 주문이 존재하는 주문 테이블은 비어있는지 여부를 변경할 수 없습니다.");
        }
    }

    public boolean isGrouped() {
        return Objects.nonNull(tableGroupId);
    }

    public void changeNumberOfGuests(int numberOfGuests) {
        validateMinimum(numberOfGuests);
        validateOrderTableIsEmpty();
        this.numberOfGuests = numberOfGuests;
    }

    private void validateMinimum(int numberOfGuests) {
        if (numberOfGuests < MINIMUM_NUMBER_OF_GUESTS) {
            throw new IllegalArgumentException("손님 수는 " + MINIMUM_NUMBER_OF_GUESTS + " 미만일 수 없습니다.");
        }
    }

    private void validateOrderTableIsEmpty() {
        if (isEmpty) {
            throw new IllegalArgumentException("빈 주문 테이블에 손님 수를 변경할 수 없습니다.");
        }
    }

    public boolean isFilled() {
        return !isEmpty;
    }

    public void group(Long tableGroupId) {
        this.tableGroupId = tableGroupId;
        this.isEmpty = false;
    }

    public void ungroup() {
        this.tableGroupId = null;
        this.isEmpty = false;
    }

    public boolean hasCookingOrMealOrder() {
        return orders.stream().anyMatch(Order::isCookingOrMealStatus);
    }

    public void validateTableCanTakeOrder() {
        if (isEmpty) {
            throw new IllegalArgumentException("빈 주문 테이블은 주문을 받을 수 없습니다");
        }
    }

    public Long getId() {
        return id;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Long getTableGroupId() {
        return tableGroupId;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
