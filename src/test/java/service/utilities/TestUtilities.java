package service.utilities;

import io.restassured.response.Response;
import service.json.AuthData;
import service.json.User;

import java.util.Random;

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
}