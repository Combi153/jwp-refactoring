package kitchenpos.application;

import static kitchenpos.common.fixture.MenuFixture.메뉴;
import static kitchenpos.common.fixture.MenuGroupFixture.메뉴_그룹;
import static kitchenpos.common.fixture.MenuProductFixture.메뉴_상품;
import static kitchenpos.common.fixture.ProductFixture.상품;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import kitchenpos.common.ServiceTest;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@ServiceTest
class MenuServiceTest {

    private static final BigDecimal PRICE = BigDecimal.valueOf(1).setScale(2, RoundingMode.HALF_UP);

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuGroupDao menuGroupDao;

    @Autowired
    private ProductDao productDao;

    private Long menuGroupId;
    private MenuProduct menuProduct;

    @BeforeEach
    void setUp() {
        menuGroupId = menuGroupDao.save(메뉴_그룹()).getId();
        Long productId = productDao.save(상품(PRICE)).getId();
        menuProduct = 메뉴_상품(productId);
    }

    @Test
    void 메뉴를_생성한다() {
        // given
        Menu menu = 메뉴(menuGroupId, PRICE, List.of(menuProduct));

        // when
        Menu createdMenu = menuService.create(menu);

        // then
        assertSoftly(softly -> {
            softly.assertThat(createdMenu.getId()).isNotNull();
            softly.assertThat(createdMenu).usingRecursiveComparison()
                    .ignoringFields("id", "menuProducts.seq")
                    .isEqualTo(메뉴(menuGroupId, List.of(menuProduct)));
        });
    }

    @Test
    void 메뉴를_생성할_때_메뉴_가격이_0_미만이면_예외를_던진다() {
        // given
        Menu invalidMenu = 메뉴(menuGroupId, BigDecimal.valueOf(-1L), List.of(menuProduct));

        // expect
        assertThatThrownBy(() -> menuService.create(invalidMenu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴를_생성할_때_메뉴_가격이_null이면_예외를_던진다() {
        // given
        Menu invalidMenu = 메뉴(menuGroupId, null, List.of(menuProduct));

        // expect
        assertThatThrownBy(() -> menuService.create(invalidMenu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴를_생성할_때_메뉴_그룹이_존재하지_않으면_예외를_던진다() {
        // given
        Menu invalidMenu = 메뉴(Long.MIN_VALUE, PRICE, List.of(menuProduct));

        // expect
        assertThatThrownBy(() -> menuService.create(invalidMenu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴를_생성할_때_메뉴_상품이_존재하지_않는_상품이면_예외를_던진다() {
        // given
        Menu invalidMenu = 메뉴(menuGroupId, PRICE, List.of());

        // expect
        assertThatThrownBy(() -> menuService.create(invalidMenu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴를_생성할_때_메뉴_가격이_메뉴_상품의_총가격을_초과하면_예외를_던진다() {
        // given
        Menu invalidMenu = 메뉴(menuGroupId, PRICE.add(BigDecimal.ONE), List.of());

        // expect
        assertThatThrownBy(() -> menuService.create(invalidMenu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 전체_메뉴를_조회한다() {
        // given
        Long menuId = menuService.create(메뉴(menuGroupId, PRICE, List.of(menuProduct))).getId();

        // when
        List<Menu> menus = menuService.list();

        // then
        assertThat(menus).usingRecursiveComparison()
                .ignoringFields("menuProducts.seq")
                .isEqualTo(List.of(메뉴(menuId, menuGroupId, PRICE, List.of(menuProduct))));
    }
}
