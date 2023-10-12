package kitchenpos.application;

import static kitchenpos.common.fixture.OrderFixture.주문;
import static kitchenpos.common.fixture.OrderTableFixture.빈_주문_테이블;
import static kitchenpos.common.fixture.OrderTableFixture.주문_테이블;
import static kitchenpos.common.fixture.TableGroupFixture.단체_지정;
import static kitchenpos.domain.OrderStatus.COMPLETION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import kitchenpos.common.ServiceTest;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@ServiceTest
class TableGroupServiceTest {

    @Autowired
    private TableGroupService tableGroupService;

    @Autowired
    private OrderTableDao orderTableDao;

    @Autowired
    private TableGroupDao tableGroupDao;

    @Autowired
    private OrderDao orderDao;

    @Nested
    class 단체_지정을_생성할_때 {

        private List<OrderTable> orderTables;
        private OrderTable emptyOrderTable;
        private OrderTable filledOrderTable;
        private OrderTable groupedOrderTable;

        @BeforeEach
        void setUp() {
            orderTables = List.of(orderTableDao.save(빈_주문_테이블()), orderTableDao.save(빈_주문_테이블()));
            emptyOrderTable = orderTableDao.save(빈_주문_테이블());
            filledOrderTable = orderTableDao.save(주문_테이블());

            OrderTable orderTable = 주문_테이블();
            Long tableGroupId = tableGroupDao.save(단체_지정(List.of(orderTable))).getId();
            orderTable.setTableGroupId(tableGroupId);
            groupedOrderTable = orderTableDao.save(orderTable);
        }

        @Test
        void 정상적으로_생성한다() {
            // given
            TableGroup tableGroup = 단체_지정(orderTables);

            // when
            TableGroup createdTableGroup = tableGroupService.create(tableGroup);

            // then
            assertThat(createdTableGroup).usingRecursiveComparison()
                    .ignoringFields("id")
                    .ignoringExpectedNullFields()
                    .ignoringFieldsOfTypes(LocalDateTime.class)
                    .isEqualTo(단체_지정(List.of(주문_테이블(), 주문_테이블())));
        }

        @Test
        void 주문_테이블_목록이_비었으면_예외를_던진다() {
            // given
            TableGroup invalidTableGroup = 단체_지정(List.of());

            // expect
            assertThatThrownBy(() -> tableGroupService.create(invalidTableGroup))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 주문_테이블의_개수가_2_미만이면_예외를_던진다() {
            // given
            TableGroup invalidTableGroup = 단체_지정(List.of(emptyOrderTable));

            // expect
            assertThatThrownBy(() -> tableGroupService.create(invalidTableGroup))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 주문_테이블_목록에_중복된_주문_테이블이_있으면_예외를_던진다() {
            // given
            TableGroup invalidTableGroup = 단체_지정(List.of(emptyOrderTable, emptyOrderTable));

            // expect
            assertThatThrownBy(() -> tableGroupService.create(invalidTableGroup))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 주문_테이블_목록에_비어있지_않은_테이블이_있으면_예외를_던진다() {
            // given
            TableGroup invalidTableGroup = 단체_지정(List.of(emptyOrderTable, filledOrderTable));

            // expect
            assertThatThrownBy(() -> tableGroupService.create(invalidTableGroup))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 주문_테이블_목록에_이미_단체_지정된_주문_테이블이_있으면_예외를_던진다() {
            // given
            TableGroup invalidTableGroup = 단체_지정(List.of(emptyOrderTable, groupedOrderTable));

            // expect
            assertThatThrownBy(() -> tableGroupService.create(invalidTableGroup))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class 단체_지정을_해제할_때 {

        private OrderTable emptyOrderTable_A;
        private OrderTable emptyOrderTable_B;

        @BeforeEach
        void setUp() {
            emptyOrderTable_A = orderTableDao.save(빈_주문_테이블());
            emptyOrderTable_B = orderTableDao.save(빈_주문_테이블());
        }

        @Test
        void 정상적으로_해제한다() {
            // given
            TableGroup tableGroup = tableGroupService.create(단체_지정(List.of(emptyOrderTable_A, emptyOrderTable_B)));

            saveOrder(emptyOrderTable_A.getId(), COMPLETION.name());

            // when
            tableGroupService.ungroup(tableGroup.getId());

            // then
            assertThat(tableGroup.getOrderTables()).usingRecursiveComparison()
                    .ignoringFields("id", "tableGroupId")
                    .isEqualTo(List.of(주문_테이블(), 주문_테이블()));
        }

        private Order saveOrder(Long orderTableId, String orderStatus) {
            return orderDao.save(주문(orderTableId, orderStatus));
        }

        @ParameterizedTest
        @ValueSource(strings = {"COOKING", "MEAL"})
        void 주문_테이블_중_조리_혹은_식사_중인_주문_테이블이_있다면_예외를_던진다(String orderStatus) {
            // given
            TableGroup tableGroup = tableGroupService.create(단체_지정(List.of(emptyOrderTable_A, emptyOrderTable_B)));

            saveOrder(emptyOrderTable_A.getId(), orderStatus);
            
            // expect
            assertThatThrownBy(() -> tableGroupService.ungroup(tableGroup.getId()))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
