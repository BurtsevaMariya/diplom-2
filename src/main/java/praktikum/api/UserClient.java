package praktikum.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.entity.Token;
import praktikum.entity.User;
import praktikum.entity.UserCredentials;
import praktikum.config.BurgerRestClient;
import praktikum.utils.Endpoint;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;

public class UserClient extends BurgerRestClient {

    @Step("Создание пользователя")
    public ValidatableResponse create(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(Endpoint.USER_PATH + "/register")
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse login(UserCredentials userCredentials) {
        return given()
                .spec(getBaseSpec())
                .body(userCredentials)
                .when()
                .post(Endpoint.USER_PATH + "/login")
                .then();
    }

    @Step("Разлогин пользователя")
    public ValidatableResponse exit(String refreshToken) {
        return given()
                .spec(getBaseSpec())
                .body(refreshToken)
                .when()
                .post(Endpoint.USER_PATH + "/logout")
                .then();
    }

    @Step("Удаление пользователя")
    public void delete() {
        if (Token.getAccessToken() == null) {
            return;
        }
        given()
                .spec(getBaseSpec())
                .auth().oauth2(Token.getAccessToken())
                .when()
                .delete(Endpoint.USER_PATH)
                .then()
                .statusCode(SC_ACCEPTED);
    }

    @Step("Информация о пользователе")
    public ValidatableResponse userInfo(String token) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(token.replace("Bearer ", ""))
                .when()
                .get(Endpoint.USER_PATH + "/user")
                .then();
    }

    @Step("Изменение информации о пользователе")
    public ValidatableResponse userInfoChange(String token, UserCredentials userCredentials) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(token.replace("Bearer ", ""))
                .body(userCredentials)
                .when()
                .patch(Endpoint.USER_PATH + "/user")
                .then();
    }
}
