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
import service.json.OrdersResponse;
import service.json.User;

import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class OrdersGetTests {
    private final Ingredients INGREDIENTS = new Ingredients(List.of("61c0c5a71d1f82001bdaaa73", "61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa6d"));
    private final int ORDERS_NUM = 1;
    private User user = null;
    private String accessToken = null;
    private Response response = null;

    @Before
    public void initTestData() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        user = getNewUser();
        response = sendPostAuthRegister(user);
        compareResponseStatusCode(response, 200);
        accessToken = getAccessToken(response);
        for (int i = 0; i < ORDERS_NUM; i++) {
            response = sendPostOrders(INGREDIENTS, accessToken);
            compareResponseStatusCode(response, 200);
        }
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/orders (with token) on success")
    @Description("Endpoint returns 200 and correct response body on success")
    public void orderGetWithTokenCheck200ResponseOnSuccess() {
        response = sendGetOrders(accessToken);
        compareResponseStatusCode(response,200);
        checkSuccessfullResponseBody(response, ORDERS_NUM, INGREDIENTS);
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/orders (without token)")
    @Description("Endpoint returns 401 and correct response body without token")
    public void orderGetWithoutTokenCheck401ResponseOnSuccess() {
        response = sendGetOrdersWithoutToken();
        compareResponseStatusCode(response,401);
        checkErrorResponseBody(response, "You should be authorised");
    }

    @After
    public void clearTestData() {
        // Удаляется только созданный пользователь, т.к. нет ручки для удаления/отмены созданного заказа
        if (accessToken != null) {
            sendDeleteAuthUser(accessToken);
        }
        user = null;
        accessToken = null;
        response = null;
    }

    @Step("Get new user")
    public User getNewUser() {
        return new User(String.format("gettestmailbox%s@qasometestmail.su", new Random().nextInt(100500)).toLowerCase(), "derP@r0l", "Яков");
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
    public void checkSuccessfullResponseBody(Response response, int expectedOrdersNum, Ingredients expectedIngredients) {
        OrdersResponse responseBody = response.as(OrdersResponse.class);
        Assert.assertTrue(responseBody.isSuccess());
        Assert.assertTrue(responseBody.getTotal() > 0);
        Assert.assertTrue(responseBody.getTotalToday() > 0);
        Assert.assertEquals(expectedOrdersNum, responseBody.getOrders().size());
        for (int i = 0; i < expectedOrdersNum; i++) {
            Assert.assertEquals(expectedIngredients.getIngredients().size(), responseBody.getOrders().get(i).getIngredients().size());
            for (int j = 0; j < expectedIngredients.getIngredients().size(); j++) {
                Assert.assertTrue(responseBody.getOrders().get(i).getIngredients().contains(expectedIngredients.getIngredients().get(j)));
            }
            Assert.assertFalse(responseBody.getOrders().get(i).get_id().isEmpty());
            Assert.assertFalse(responseBody.getOrders().get(i).getStatus().isEmpty());
            Assert.assertTrue(responseBody.getOrders().get(i).getNumber() > 0);
            Assert.assertFalse(responseBody.getOrders().get(i).getCreatedAt().isEmpty());
            Assert.assertFalse(responseBody.getOrders().get(i).getUpdatedAt().isEmpty());
        }
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

    @Step("Send GET /api/orders")
    public Response sendGetOrders(String accessToken) {
        Response response =
                given()
                        .header("authorization", accessToken)
                        .get("/api/orders");
        return response;
    }

    @Step("Send GET /api/orders without token")
    public Response sendGetOrdersWithoutToken() {
        Response response =
                given()
                        .get("/api/orders");
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
