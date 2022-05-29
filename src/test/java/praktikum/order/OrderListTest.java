package praktikum.order;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import praktikum.api.OrderClient;
import praktikum.api.UserClient;
import praktikum.entity.Order;
import praktikum.entity.User;
import praktikum.entity.UserCredentials;

import java.util.List;
import java.util.Map;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Тесты: Получение заказов конкретного пользователя")
public class OrderListTest {
    private User user;
    private UserClient userClient;
    public OrderClient orderClient;
    String bearerToken;
    private static final String MESSAGE_UNAUTHORIZED = "You should be authorised";

    @Before
    public void setUp() {
        user = User.getRandom();
        userClient = new UserClient();
        Order order = Order.getRandomBurger();
        orderClient = new OrderClient();
    }

    @Test
    @Description("Проверяю возможность получения списка заказов авторизованного пользователя")
    public void checkingComingListAuthorizedUser() {
        userClient.create(user);
        ValidatableResponse login = userClient.login(UserCredentials.from(user));
        bearerToken = login.extract().path("accessToken");

        ValidatableResponse orderInfo = orderClient.userOrdersList(bearerToken);
        List<Map<String, Object>> ordersList = orderInfo.extract().path("orders");

        orderInfo.assertThat().statusCode(SC_OK);
        orderInfo.assertThat().body("success", equalTo(true));
        assertThat("Orders list empty", ordersList, is(not(0)));
    }

    @Test
    @Description("Проверяю возможность получения списка заказов не авторизованного пользователя")
    public void checkingComingListNotAuthorizedUser() {
        bearerToken = "";

        ValidatableResponse orderInfo = orderClient.userOrdersList(bearerToken);

        orderInfo.assertThat().statusCode(SC_UNAUTHORIZED);
        orderInfo.assertThat().body("success", equalTo(false));
        orderInfo.assertThat().body("message", equalTo(MESSAGE_UNAUTHORIZED));
    }
}
