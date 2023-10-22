package kitchenpos.common.fixture;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Money;

@SuppressWarnings("NonAsciiCharacters")
public class MenuFixture {

    public static Menu 메뉴(Long menuId, Long menuGroupId) {
        return new Menu(
                menuId,
                "menuName",
                Money.valueOf(BigDecimal.valueOf(1).setScale(2, RoundingMode.HALF_UP)),
                menuGroupId,
                List.of()
        );
    }

    public static Menu 메뉴(Long menuGroupId) {
        return new Menu(
                "menuName",
                Money.valueOf(BigDecimal.valueOf(1).setScale(2, RoundingMode.HALF_UP)),
                menuGroupId,
                List.of()
        );
    }

    public static Menu 메뉴(Long menuGroupId, List<MenuProduct> menuProducts) {
        return new Menu(
                "menuName",
                Money.valueOf(BigDecimal.valueOf(1).setScale(2, RoundingMode.HALF_UP)),
                menuGroupId,
                menuProducts
        );
    }

    public static Menu 메뉴(Long menuGroupId, BigDecimal price, List<MenuProduct> menuProducts) {
        return new Menu(
                "menuName",
                Money.valueOf(price),
                menuGroupId,
                menuProducts
        );
    }

    public static Menu 메뉴(Long menuId, Long menuGroupId, BigDecimal price, List<MenuProduct> menuProducts) {
        return new Menu(
                menuId,
                "menuName",
                Money.valueOf(price),
                menuGroupId,
                menuProducts
        );
    }
}
