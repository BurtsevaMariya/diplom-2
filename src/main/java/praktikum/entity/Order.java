package praktikum.entity;

import com.github.javafaker.Faker;
import io.restassured.response.ValidatableResponse;
import praktikum.config.BurgerRestClient;
import praktikum.utils.Endpoint;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.apache.commons.lang3.RandomUtils.nextInt;

public class Order extends BurgerRestClient {

    public ArrayList<Object> ingredients;
    public static Faker faker = new Faker();

    public Order(ArrayList<Object> ingredients) {
        this.ingredients = ingredients;
    }

    public static Order getRandomBurger() {
        ValidatableResponse response = given()
                .spec(getBaseSpec())
                .when()
                .get(Endpoint.INGRIDIENTS_PATH)
                .then()
                .statusCode(SC_OK);

        ArrayList<Object> ingredients = new ArrayList<>();
        int bunIndex = nextInt(0, 2);
        int mainIndex = nextInt(0, 9);
        int sauceIndex = nextInt(0, 4);

        List<Object> bunIngredients = response.extract().jsonPath().getList("data.findAll{it.type == 'bun'}._id");
        List<Object> mainIngredients = response.extract().jsonPath().getList("data.findAll{it.type == 'main'}._id");
        List<Object> sauceIngredients = response.extract().jsonPath().getList("data.findAll{it.type == 'sauce'}._id");
        ingredients.add(bunIngredients.get(bunIndex));
        ingredients.add(mainIngredients.get(mainIndex));
        ingredients.add(sauceIngredients.get(sauceIndex));
        return new Order(ingredients);
    }

    public static Order getNullIngredients() {
        ArrayList<Object> ingredients = new ArrayList<>();
        return new Order(ingredients);
    }

    public static Order getIncorrectIngredients() {
        ArrayList<Object> ingredients = new ArrayList<>();
        ingredients.add(faker.internet().uuid());
        ingredients.add(faker.internet().uuid());
        ingredients.add(faker.internet().uuid());
        return new Order(ingredients);
    }
}
