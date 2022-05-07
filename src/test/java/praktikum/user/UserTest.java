package praktikum.user;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import praktikum.api.UserClient;
import praktikum.entity.User;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

@Epic("Тесты: Создание пользователя")
public class UserTest {
    private UserClient userClient;
    private static final String MESSAGE_FORBIDDEN = "User already exists";
    private static final String MESSAGE_FORBIDDEN_EMPTY_FIELD = "Email, password and name are required fields";

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Проверяю возможность регистрации пользователя")
    @Description("Тестирую endpoint: /api/auth/register")
    public void checkUserCanBeCreated() {
        User user = User.getRandom();

        ValidatableResponse validatableResponse = userClient.create(user);

        validatableResponse.assertThat().statusCode(SC_OK);
        validatableResponse.assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Проверяю возможность регистрации уже зарегистрированного пользователя")
    @Description("Тестирую endpoint: /api/auth/register")
    public void checkUserCannotIdenticalTwiceCreated() {
        User user = User.getRandom();

        userClient.create(user);
        ValidatableResponse validatableResponse = userClient.create(user);

        validatableResponse.assertThat().statusCode(SC_FORBIDDEN);
        validatableResponse.assertThat().body("success", equalTo(false));
        validatableResponse.assertThat().body("message", equalTo(MESSAGE_FORBIDDEN));
    }

    @Test
    @DisplayName("Проверяю возможность регистрации пользователя без имени")
    @Description("Тестирую endpoint: /api/auth/register")
    public void checkCreateUserWithoutName() {
        User user = User.getRandom(true, true, false);

        ValidatableResponse validatableResponse = userClient.create(user);

        validatableResponse.assertThat().statusCode(SC_FORBIDDEN);
        validatableResponse.assertThat().body("success", equalTo(false));
        validatableResponse.assertThat().body("message", equalTo(MESSAGE_FORBIDDEN_EMPTY_FIELD));
    }

    @Test
    @DisplayName("Проверяю возможность регистрации пользователя без пароля")
    @Description("Тестирую endpoint: /api/auth/register")
    public void checkCreateUserWithoutPassword() {
        User user = User.getRandom(true, false, true);

        ValidatableResponse validatableResponse = userClient.create(user);

        validatableResponse.assertThat().statusCode(SC_FORBIDDEN);
        validatableResponse.assertThat().body("success", equalTo(false));
        validatableResponse.assertThat().body("message", equalTo(MESSAGE_FORBIDDEN_EMPTY_FIELD));
    }
}
