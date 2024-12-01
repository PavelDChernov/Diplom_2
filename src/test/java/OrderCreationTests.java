import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Assert;
import org.junit.Test;
import service.abstractions.AbstractOrderCreationTest;
import service.json.Ingredients;
import service.json.OrderResponse;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderCreationTests extends AbstractOrderCreationTest {
    @Test
    @DisplayName("Check status code and response body of POST /api/orders (with token) on success")
    @Description("Endpoint returns 200 and correct response body on success")
    public void orderCreateWithTokenCheck200ResponseOnSuccess() {
        response = sendPostOrders(new Ingredients(List.of("61c0c5a71d1f82001bdaaa73","61c0c5a71d1f82001bdaaa70","61c0c5a71d1f82001bdaaa6d")), accessToken);
        compareResponseStatusCode(response,200);
        compareResponseSuccessField(response, true);
        checkResponseBurgerNameFieldIsNotEmpty(response);
        checkResponseBurgerOrderNumFieldIsGreaterThanZero(response);
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/orders (without token)")
    @Description("Endpoint returns 200 and correct response body on success")
    public void orderCreateWithoutTokenCheck200ResponseOnSuccess() {
        response = sendPostOrdersWithoutToken(new Ingredients(List.of("61c0c5a71d1f82001bdaaa6d")));
        compareResponseStatusCode(response,200);
        compareResponseSuccessField(response, true);
        checkResponseBurgerNameFieldIsNotEmpty(response);
        checkResponseBurgerOrderNumFieldIsGreaterThanZero(response);
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/orders without ingredients")
    @Description("Endpoint returns 400 and correct response body without ingredients")
    public void orderCreateCheck400ResponseWithoutIngredients() {
        response = sendPostOrders(new Ingredients(), accessToken);
        compareResponseStatusCode(response,400);
        compareResponseSuccessField(response, false);
        compareResponseMessageField(response, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Check status code of POST /api/orders with incorrect ingredients")
    @Description("Endpoint returns 500 with incorrect ingredients")
    public void orderCreateCheck500ResponseWithIncorrectIngredients() {
        Ingredients ingredients = new Ingredients(List.of("Борщ с капусткой, но не красный"));
        response = sendPostOrders(ingredients, accessToken);
        compareResponseStatusCode(response,500);
    }

    @Step("Compare response status code")
    public void compareResponseStatusCode(Response response, int expectedStatusCode) {
        response.then().assertThat().statusCode(expectedStatusCode);
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

    // проверить, что имя бургера не пустое
    @Step("Check response burger name field is not empty")
    public void checkResponseBurgerNameFieldIsNotEmpty(Response response) {
        OrderResponse responseBody = response.as(OrderResponse.class);
        Assert.assertFalse(responseBody.getName().isEmpty());
    }

    // проверить, что номер заказа > 0
    @Step("Check response burger order number field greater than zero")
    public void checkResponseBurgerOrderNumFieldIsGreaterThanZero(Response response) {
        OrderResponse responseBody = response.as(OrderResponse.class);
        Assert.assertTrue(responseBody.getOrder().getNumber() > 0);
    }

    @Step("Compare response body success field")
    public void compareResponseSuccessField(Response response, boolean expectedSuccess) {
        Assert.assertEquals(expectedSuccess, response.path("success"));
    }

    @Step("Compare response body message field")
    public void compareResponseMessageField(Response response, String expectedMessage) {
        Assert.assertEquals(expectedMessage, response.path("message").toString());
    }
}
