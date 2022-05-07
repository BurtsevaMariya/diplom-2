package praktikum.order;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import praktikum.api.OrderClient;
import praktikum.api.UserClient;
import praktikum.entity.Order;
import praktikum.entity.User;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Тесты: Создание заказа")
public class OrderTest {
    private User user;
    private UserClient userClient;
    private Order order;
    public OrderClient orderClient;
    String bearerToken;
    private static final String MESSAGE_BAD_REQUEST = "Ingredient ids must be provided";

    @Before
    public void setUp() {
        user = User.getRandom();
        userClient = new UserClient();
        order = Order.getRandomBurger();
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Проверяю возможность создания заказа для зарегистрированного пользователя")
    @Description("Тестирую endpoint: /api/orders")
    public void checkingCreateOrderRegistered() {
        ValidatableResponse userResponse = userClient.create(user);
        bearerToken = userResponse.extract().path("accessToken");

        ValidatableResponse orderResponse = orderClient.createOrder(bearerToken, order);
        int orderNumber = orderResponse.extract().path("order.number");

        orderResponse.assertThat().statusCode(SC_OK);
        orderResponse.assertThat().body("success", equalTo(true));
        assertThat("The order number is missing", orderNumber, is(not(0)));
    }

    @Test
    @DisplayName("Проверяю возможность создания заказа для не зарегистрированного пользователя")
    @Description("Тестирую endpoint: /api/orders")
    public void checkingCreateOrderNotRegistered() {
        bearerToken = "";
        ValidatableResponse orderResponse = orderClient.createOrder(bearerToken, order);
        int orderNumber = orderResponse.extract().path("order.number");

        orderResponse.assertThat().statusCode(SC_OK);
        orderResponse.assertThat().body("success", equalTo(true));
        assertThat("The order number is missing", orderNumber, is(not(0)));
    }

    @Test
    @DisplayName("Проверяю возможность создания заказа без ингредиентов")
    @Description("Тестирую endpoint: /api/orders")
    public void checkingCreateOrderNotIngredient() {
        ValidatableResponse userResponse = userClient.create(user);
        bearerToken = userResponse.extract().path("accessToken");

        ValidatableResponse orderResponse = orderClient.createOrder(bearerToken, Order.getNullIngredients());

        orderResponse.assertThat().statusCode(SC_BAD_REQUEST);
        orderResponse.assertThat().body("success", equalTo(false));
        orderResponse.assertThat().body("message", equalTo(MESSAGE_BAD_REQUEST));
    }

    @Test
    @DisplayName("Проверяю возможность создания заказа с не валидными хешем ингредиентов")
    @Description("Тестирую endpoint: /api/orders")
    public void checkingCreateOrderIncorrectIngredient() {
        ValidatableResponse userResponse = userClient.create(user);
        bearerToken = userResponse.extract().path("accessToken");

        ValidatableResponse orderResponse = orderClient.createOrder(bearerToken, Order.getIncorrectIngredients());

        orderResponse.assertThat().statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}
