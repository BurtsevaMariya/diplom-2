package praktikum.user;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.api.UserClient;
import praktikum.entity.User;
import praktikum.entity.UserCredentials;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Тесты: Логин пользователя")
public class LoginTest {
    private UserClient userClient;
    private User user;
    private static final String MESSAGE_UNAUTHORIZED = "email or password are incorrect";

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandom();
    }

    @Test
    @DisplayName("Проверяю возможность авторизации пользователя")
    @Description("Тестирую endpoint: /api/auth/login")
    public void checkingUserLogin() {
        userClient.create(user);

        ValidatableResponse validatableResponse = userClient.login(UserCredentials.from(user));
        String updateToken = validatableResponse.extract().path("updateToken");

        assertThat("Courier ID incorrect", updateToken, is(not(0)));
        validatableResponse.assertThat().statusCode(SC_OK);
        validatableResponse.assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Проверяю возможность авторизации пользователя без email")
    @Description("Тестирую endpoint: /api/auth/login")
    public void checkingUserNotLogin() {
        userClient.create(user);

        ValidatableResponse validatableResponse = userClient.login(new UserCredentials(null, user.password));

        validatableResponse.assertThat().statusCode(SC_UNAUTHORIZED);
        validatableResponse.assertThat().body("success", equalTo(false));
        validatableResponse.assertThat().body("message", equalTo(MESSAGE_UNAUTHORIZED));
    }

    @Test
    @DisplayName("Проверяю возможность авторизации пользователя без пароля")
    @Description("Тестирую endpoint: /api/auth/login")
    public void checkingUserNotPassword() {
        userClient.create(user);

        ValidatableResponse validatableResponse = userClient.login(new UserCredentials(user.email, null));

        validatableResponse.assertThat().statusCode(SC_UNAUTHORIZED);
        validatableResponse.assertThat().body("message", equalTo(MESSAGE_UNAUTHORIZED));
    }

    @Test
    @DisplayName("Проверяю возможность авторизации пользователя с невалидным логином")
    @Description("Тестирую endpoint: /api/auth/login")
    public void checkingUserInvalidLogin() {
        userClient.create(user);

        ValidatableResponse validatableResponse = userClient.login(new UserCredentials("987654321", user.password));

        validatableResponse.assertThat().statusCode(SC_UNAUTHORIZED);
        validatableResponse.assertThat().body("success", equalTo(false));
        validatableResponse.assertThat().body("message", equalTo(MESSAGE_UNAUTHORIZED));
    }

    @Test
    @DisplayName("Проверяю возможность авторизации пользователя с невалидным паролем")
    @Description("Тестирую endpoint: /api/auth/login")
    public void checkingUserInvalidPassword() {
        userClient.create(user);

        ValidatableResponse validatableResponse = userClient.login(new UserCredentials(user.email, "987654321"));

        validatableResponse.assertThat().statusCode(SC_UNAUTHORIZED);
        validatableResponse.assertThat().body("message", equalTo(MESSAGE_UNAUTHORIZED));
    }

    @After
    public void tearDown() {
        userClient.delete();
    }
}
