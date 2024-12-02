package service.utilities;

import io.restassured.response.Response;
import service.api.BurgerApi;
import java.util.Random;
import service.json.*;

public class TestUtilities {
    // сгенерировать данные нового пользователя
    public static User getNewUser() {
        return new User(String.format("testmailbox%s@qasometestmail.su", new Random().nextInt(100500)).toLowerCase(), "derP@r0l", "Яков");
    }

    // получить логин и пароль пользователя
    public static AuthData getUserAuthData(User user) {
        return new AuthData(user.getEmail(), user.getPassword());
    }

    // сравнить фактический и ожидаемый код ответа
    public static void compareResponseStatusCode(Response response, int expectedStatusCode) {
        response.then().assertThat().statusCode(expectedStatusCode);
    }

    // получить токен авторизации из ответа
    public static String getAccessToken(Response response) {
        return response.path("accessToken");
    }

    // получить набор ингредиентов
    public static Ingredients getNewIngredientsList(int listSize) {
        IngredientsData availableIngredients = BurgerApi.sendGetIngredients().as(IngredientsData.class);
        Ingredients ingredientsList = new Ingredients();
        int avaliableSize = availableIngredients.getData().size();
        for (int i = 0; i < listSize; i++) {
            ingredientsList.addIngredient(availableIngredients.getData().get(new Random().nextInt(avaliableSize)).get_id());
        }
        return ingredientsList;
    }
}