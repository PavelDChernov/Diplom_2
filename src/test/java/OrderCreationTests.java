import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import service.json.ErrorResponse;
import service.json.Ingredients;
import service.json.OrderResponse;
import service.json.User;

import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class OrderCreationTests {
    private User user = null;
    private Response response = null;
    private String accessToken = null;

    @Before
    public void initTestData() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        user = getNewUser();
        response = sendPostAuthRegister(user);
        compareResponseStatusCode(response,200);
        accessToken = getAccessToken(response);
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/orders (with token) on success")
    @Description("Endpoint returns 200 and correct response body on success")
    public void orderCreateWithTokenCheck200ResponseOnSuccess() {
        response = sendPostOrders(new Ingredients(List.of("61c0c5a71d1f82001bdaaa73","61c0c5a71d1f82001bdaaa70","61c0c5a71d1f82001bdaaa6d")), accessToken);
        compareResponseStatusCode(response,200);
        checkSuccessfullResponseBody(response);
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/orders (without token)")
    @Description("Endpoint returns 200 and correct response body on success")
    public void orderCreateWithoutTokenCheck200ResponseOnSuccess() {
        response = sendPostOrdersWithoutToken(new Ingredients(List.of("61c0c5a71d1f82001bdaaa6d")));
        compareResponseStatusCode(response,200);
        checkSuccessfullResponseBody(response);
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/orders without ingredients")
    @Description("Endpoint returns 400 and correct response body without ingredients")
    public void orderCreateCheck400ResponseWithoutIngredients() {
        response = sendPostOrders(new Ingredients(), accessToken);
        compareResponseStatusCode(response,400);
        checkErrorResponseBody(response, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Check status code of POST /api/orders with incorrect ingredients")
    @Description("Endpoint returns 500 with incorrect ingredients")
    public void orderCreateCheck500ResponseWithIncorrectIngredients() {
        Ingredients ingredients = new Ingredients(List.of("Борщ с капусткой, но не красный"));
        response = sendPostOrders(ingredients, accessToken);
        compareResponseStatusCode(response,500);
    }

    @After
    public void clearTestData() {
        // Удаляется только созданный пользователь, т.к. нет ручки для удаления/отмены созданного заказа
        if (accessToken != null) {
            sendDeleteAuthUser(accessToken);
        }
        user = null;
        response = null;
        accessToken = null;
    }

    @Step("Get new user")
    public User getNewUser() {
        return new User(String.format("testmailbox%s@qasometestmail.su", new Random().nextInt(100500)).toLowerCase(), "derP@r0l", "Яков");
    }

    @Step("Send POST /api/auth/register")
    public Response sendPostAuthRegister(User user) {
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(user)
                        .when()
                        .post("/api/auth/register");
        return response;
    }

    @Step("Compare response status code")
    public void compareResponseStatusCode(Response response, int expectedStatusCode) {
        response.then().assertThat().statusCode(expectedStatusCode);
    }

    @Step("Check successfull response body")
    public void checkSuccessfullResponseBody(Response response) {
        OrderResponse responseBody = response.as(OrderResponse.class);
        Assert.assertFalse(responseBody.getName().isEmpty());
        Assert.assertTrue(responseBody.getOrder().getNumber() > 0);
        Assert.assertTrue(responseBody.isSuccess());
    }

    @Step("Check error response body")
    public void checkErrorResponseBody(Response response, String expectedError) {
        ErrorResponse responseBody = response.as(ErrorResponse.class);
        Assert.assertFalse(responseBody.isSuccess());
        Assert.assertEquals(expectedError, responseBody.getMessage());
    }

    @Step("Get access token from response")
    public String getAccessToken(Response response) {
        return response.path("accessToken");
    }

    @Step("Send POST /api/orders")
    public Response sendPostOrders(Ingredients ingredients, String accessToken) {
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

    @Step("Send POST /api/orders without token")
    public Response sendPostOrdersWithoutToken(Ingredients ingredients) {
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(ingredients)
                        .when()
                        .post("/api/orders");
        return response;
    }

    @Step("Send DELETE /api/auth/user")
    public Response sendDeleteAuthUser(String accessToken) {
        Response response =
                given()
                        .header("authorization", accessToken)
                        .delete("/api/auth/user");
        return response;
    }
}
