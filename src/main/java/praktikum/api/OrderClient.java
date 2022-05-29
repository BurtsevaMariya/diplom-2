package praktikum.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.entity.Order;
import praktikum.config.BurgerRestClient;
import praktikum.utils.Endpoint;

import static io.restassured.RestAssured.given;

public class OrderClient extends BurgerRestClient {

    @Step("Создание заказа")
    public ValidatableResponse createOrder(String token, Order order) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(token.replace("Bearer ", ""))
                .body(order)
                .when()
                .post(Endpoint.ORDER_PATH)
                .then();
    }

    @Step("Список заказов")
    public ValidatableResponse userOrders() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(Endpoint.ORDER_PATH + "/all")
                .then();
    }

    @Step("Список заказов пользователя")
    public ValidatableResponse userOrdersList(String token) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(token.replace("Bearer ", ""))
                .when()
                .get(Endpoint.ORDER_PATH)
                .then();
    }
}
