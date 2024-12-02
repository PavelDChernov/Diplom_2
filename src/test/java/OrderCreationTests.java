import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.qameta.allure.Description;
import org.junit.Assert;
import org.junit.Test;
import service.abstractions.AbstractOrderCreationTest;
import service.api.BurgerApi;
import service.json.Ingredients;
import service.json.OrderResponse;
import service.utilities.TestUtilities;
import java.util.List;
import java.util.Random;

public class OrderCreationTests extends AbstractOrderCreationTest {
    private static final int MAX_INGREDIENTS = 5;

    @Test
    @DisplayName("Check status code and response body of POST /api/orders (with token) on success")
    @Description("Endpoint returns 200 and correct response body on success")
    public void orderCreateWithTokenCheck200ResponseOnSuccess() {
        // подготовка
        Ingredients ingredients = TestUtilities.getNewIngredientsList(new Random().nextInt(MAX_INGREDIENTS) + 1);
        // тест
        sendPostOrders(ingredients, accessToken);
        compareResponseStatusCode(response,200);
        compareResponseSuccessField(response, true);
        checkResponseBurgerNameFieldIsNotEmpty(response);
        checkResponseBurgerOrderNumFieldIsGreaterThanZero(response);
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/orders (without token)")
    @Description("Endpoint returns 200 and correct response body on success")
    public void orderCreateWithoutTokenCheck200ResponseOnSuccess() {
        // подготовка
        Ingredients ingredients = TestUtilities.getNewIngredientsList(new Random().nextInt(MAX_INGREDIENTS) + 1);
        // тест
        sendPostOrders(ingredients, null);
        compareResponseStatusCode(response,200);
        compareResponseSuccessField(response, true);
        checkResponseBurgerNameFieldIsNotEmpty(response);
        checkResponseBurgerOrderNumFieldIsGreaterThanZero(response);
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/orders without ingredients")
    @Description("Endpoint returns 400 and correct response body without ingredients")
    public void orderCreateCheck400ResponseWithoutIngredients() {
        sendPostOrders(new Ingredients(), accessToken);
        compareResponseStatusCode(response,400);
        compareResponseSuccessField(response, false);
        compareResponseMessageField(response, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Check status code of POST /api/orders with incorrect ingredients")
    @Description("Endpoint returns 500 with incorrect ingredients")
    public void orderCreateCheck500ResponseWithIncorrectIngredients() {
        sendPostOrders(new Ingredients(List.of("Борщ с капусткой, но не красный")), accessToken);
        compareResponseStatusCode(response,500);
    }

    @Step("Compare response status code")
    public void compareResponseStatusCode(Response response, int expectedStatusCode) {
        response.then().assertThat().statusCode(expectedStatusCode);
    }

    @Step("Send POST /api/orders")
    public void sendPostOrders(Ingredients ingredients, String accessToken) {
        response = BurgerApi.sendPostOrders(ingredients, accessToken);
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
