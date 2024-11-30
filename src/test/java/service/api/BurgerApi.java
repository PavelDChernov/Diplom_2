package service.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import service.json.AuthData;
import service.json.Ingredients;
import service.json.User;

import static io.restassured.RestAssured.given;

public class BurgerApi {
    // вызвать POST /api/auth/register
    public static Response sendPostAuthRegister(User user) {
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(user)
                        .when()
                        .post("/api/auth/register");
        return response;
    }

    // вызвать POST /api/auth/login
    public static Response sendPostAuthLogin(AuthData authData) {
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(authData)
                        .when()
                        .post("/api/auth/login");
        return response;
    }

    // вызвать POST /api/auth/login
    public static Response sendPostAuthLogin(String email, String password) {
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(new AuthData(email, password))
                        .when()
                        .post("/api/auth/login");
        return response;
    }

    // вызвать DELETE /api/auth/user
    public static Response sendDeleteAuthUser(String accessToken) {
        Response response =
                given()
                        .header("authorization", accessToken)
                        .delete("/api/auth/user");
        return response;
    }

    // вызвать POST /api/orders
    public static Response sendPostOrders(Ingredients ingredients, String accessToken) {
        Response response =
                given()
                        .header("authorization", accessToken)
                        .and()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(ingredients)
                        .when()
                        .post("/api/orders");
        return response;
    }
}
