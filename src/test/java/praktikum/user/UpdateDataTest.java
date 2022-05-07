package praktikum.user;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import praktikum.api.UserClient;
import praktikum.entity.User;
import praktikum.entity.UserCredentials;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

@Epic("Тесты: Изменение данных пользователя")
public class UpdateDataTest {
    private User user;
    private UserClient userClient;
    String bearerToken;
    private static final String MESSAGE_UNAUTHORIZED = "You should be authorised";
    private static final String MESSAGE_FORBIDDEN = "User with such email already exists";

    @Before
    public void setUp() {
        user = User.getRandom();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Проверяю возможность редактирования данных у авторизованного пользователя")
    @Description("Сменить почту")
    public void checkingAuthorizedUserEditingEmail() {
        userClient.create(user);
        ValidatableResponse login = userClient.login(UserCredentials.from(user));
        bearerToken = login.extract().path("accessToken");

        ValidatableResponse info = userClient.userInfoChange(bearerToken, UserCredentials.getUserWithRandomEmail());

        info.assertThat().statusCode(SC_OK);
        info.assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Проверяю возможность редактирования данных у авторизованного пользователя")
    @Description("Сменить пароль")
    public void checkingAuthorizedUserEditingPassword() {
        userClient.create(user);
        ValidatableResponse login = userClient.login(UserCredentials.from(user));
        bearerToken = login.extract().path("accessToken");

        ValidatableResponse info = userClient.userInfoChange(bearerToken, UserCredentials.getUserWithRandomPassword());

        info.assertThat().statusCode(SC_OK);
        info.assertThat().body("success", equalTo(true));

    }

    @Test
    @DisplayName("Проверяю возможность замены почты на уже зарегистрированную")
    @Description("Зарегистрированная почта")
    public void checkingEditingEmailToAuthorizedEmail() {
        userClient.create(user);
        ValidatableResponse login = userClient.login(UserCredentials.from(user));
        bearerToken = login.extract().path("accessToken");

        ValidatableResponse info = userClient.userInfoChange(bearerToken, UserCredentials.getUserWithEmail(user));

        info.assertThat().statusCode(SC_FORBIDDEN);
        info.assertThat().body("success", equalTo(false));
        info.assertThat().body("message", equalTo(MESSAGE_FORBIDDEN));
    }

    @Test
    @DisplayName("Проверяю возможность редактирования данных у не авторизованного пользователя")
    @Description("Изменить почту")
    public void checkingNotAuthorizedUserEditingEmail() {
        bearerToken = "";

        ValidatableResponse info = userClient.userInfoChange(bearerToken, UserCredentials.getUserWithRandomEmail());

        info.assertThat().statusCode(SC_UNAUTHORIZED);
        info.assertThat().body("success", equalTo(false));
        info.assertThat().body("message", equalTo(MESSAGE_UNAUTHORIZED));
    }

    @Test
    @DisplayName("Проверяю возможность редактирования данных у не авторизованного пользователя")
    @Description("Изменить пароль")
    public void checkingNotAuthorizedUserEditingPassword() {
        bearerToken = "";

        ValidatableResponse info = userClient.userInfoChange(bearerToken, UserCredentials.getUserWithRandomPassword());

        info.assertThat().statusCode(SC_UNAUTHORIZED);
        info.assertThat().body("success", equalTo(false));
        info.assertThat().body("message", equalTo(MESSAGE_UNAUTHORIZED));
    }
}
