package praktikum.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCredentials {
    public static Faker faker = new Faker();
    public String email;
    public String password;

    public static UserCredentials from(User user) {
        return new UserCredentials(user.email, user.password);
    }

    public UserCredentials setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserCredentials setPassword(String password) {
        this.password = password;
        return this;
    }

    public static UserCredentials getUserWithEmail(User user) {
        return new UserCredentials().setEmail(user.email);
    }

    public static UserCredentials getUserWithRandomEmail() {
        return new UserCredentials().setEmail(faker.internet().emailAddress());
    }

    public static UserCredentials getUserWithRandomPassword() {
        return new UserCredentials().setPassword(faker.internet().password());
    }

    @Override
    public String toString() {
        return String.format("Пользователь { Email:%s, Пароль:%s }", this.email, this.password);
    }
}
