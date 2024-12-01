import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Assert;
import org.junit.Test;
import service.abstractions.AbstractOrdersGetTest;
import service.json.Ingredients;
import service.json.Order;
import service.json.OrdersResponse;

import static io.restassured.RestAssured.given;

public class OrdersGetTests extends AbstractOrdersGetTest {
    @Test
    @DisplayName("Check status code and response body of POST /api/orders (with token) on success")
    @Description("Endpoint returns 200 and correct response body on success")
    public void orderGetWithTokenCheck200ResponseOnSuccess() {
        response = sendGetOrders(accessToken);
        compareResponseStatusCode(response,200);
        compareResponseSuccessField(response, true);
        checkResponseTotalFieldIsGreaterThanZero(response);
        checkResponseTotalTodayFieldIsGreaterThanZero(response);
        compareResponseOrdersFieldSize(response, ORDERS_NUM);
        compareResponseOrdersFieldIngredients(response, INGREDIENTS);
        checkResponseOrdersFieldOrder_idAreNotEmpty(response);
        checkResponseOrdersFieldOrderStatusAreNotEmpty(response);
        checkResponseOrdersFieldOrderNumberAreGreaterThanZero(response);
        checkResponseOrdersFieldOrderCreatedAtAreNotEmpty(response);
        checkResponseOrdersFieldOrderUpdatedAtAreNotEmpty(response);
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/orders (without token)")
    @Description("Endpoint returns 401 and correct response body without token")
    public void orderGetWithoutTokenCheck401ResponseOnSuccess() {
        response = sendGetOrders(null);
        compareResponseStatusCode(response,401);
        compareResponseSuccessField(response, false);
        compareResponseMessageField(response, "You should be authorised");
    }

    @Step("Send GET /api/orders")
    public Response sendGetOrders(String accessToken) {
        if (accessToken == null) {
            Response response =
                    given()
                            .get("/api/orders");
            return response;
        }
        Response response =
                given()
                        .header("authorization", accessToken)
                        .get("/api/orders");
        return response;
    }

    @Step("Compare response status code")
    public void compareResponseStatusCode(Response response, int expectedStatusCode) {
        response.then().assertThat().statusCode(expectedStatusCode);
    }

    @Step("Compare response orders field size")
    public void compareResponseOrdersFieldSize(Response response, int expectedOrdersNum) {
        OrdersResponse responseBody = response.as(OrdersResponse.class);
        Assert.assertEquals(expectedOrdersNum, responseBody.getOrders().size());
    }

    @Step("Check response total field greater than zero")
    public void checkResponseTotalFieldIsGreaterThanZero(Response response) {
        OrdersResponse responseBody = response.as(OrdersResponse.class);
        Assert.assertTrue(responseBody.getTotal() > 0);
    }

    @Step("Check response totalToday field greater than zero")
    public void checkResponseTotalTodayFieldIsGreaterThanZero(Response response) {
        OrdersResponse responseBody = response.as(OrdersResponse.class);
        Assert.assertTrue(responseBody.getTotalToday() > 0);
    }

    // проверить, что заказы содержат переданные ингредиенты
    @Step("Compare ingredients in orders[].order.ingredients[] field")
    public void compareResponseOrdersFieldIngredients(Response response, Ingredients expectedIngredients) {
        OrdersResponse responseBody = response.as(OrdersResponse.class);
        for (Order order : responseBody.getOrders()) {
            for (int i = 0; i < expectedIngredients.getIngredients().size(); i++) {
                Assert.assertTrue(order.getIngredients().contains(expectedIngredients.getIngredients().get(i)));
            }
        }
    }

    @Step("Check orders field orders[].order._id fields are not empty")
    public void checkResponseOrdersFieldOrder_idAreNotEmpty(Response response) {
        OrdersResponse responseBody = response.as(OrdersResponse.class);
        for (Order order : responseBody.getOrders()) {
            Assert.assertFalse(order.get_id().isEmpty());
        }
    }

    @Step("Check orders field orders[].order.status fields are not empty")
    public void checkResponseOrdersFieldOrderStatusAreNotEmpty(Response response) {
        OrdersResponse responseBody = response.as(OrdersResponse.class);
        for (Order order : responseBody.getOrders()) {
            Assert.assertFalse(order.getStatus().isEmpty());
        }
    }

    @Step("Check orders field orders[].order.number fields are greater than zero")
    public void checkResponseOrdersFieldOrderNumberAreGreaterThanZero(Response response) {
        OrdersResponse responseBody = response.as(OrdersResponse.class);
        for (Order order : responseBody.getOrders()) {
            Assert.assertTrue(order.getNumber() > 0);
        }
    }

    @Step("Check orders field orders[].order.createdAt fields are not empty")
    public void checkResponseOrdersFieldOrderCreatedAtAreNotEmpty(Response response) {
        OrdersResponse responseBody = response.as(OrdersResponse.class);
        for (Order order : responseBody.getOrders()) {
            Assert.assertFalse(order.getCreatedAt().isEmpty());
        }
    }

    @Step("Check orders field orders[].order.updatedAt fields are not empty")
    public void checkResponseOrdersFieldOrderUpdatedAtAreNotEmpty(Response response) {
        OrdersResponse responseBody = response.as(OrdersResponse.class);
        for (Order order : responseBody.getOrders()) {
            Assert.assertFalse(order.getUpdatedAt().isEmpty());
        }
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
