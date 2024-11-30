import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Assert;
import org.junit.Test;
import service.abstractions.AbstractOrdersGetTest;
import service.json.ErrorResponse;
import service.json.Ingredients;
import service.json.OrdersResponse;

import static io.restassured.RestAssured.given;

public class OrdersGetTests extends AbstractOrdersGetTest {
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
        response = sendGetOrders(null);
        compareResponseStatusCode(response,401);
        checkErrorResponseBody(response, "You should be authorised");
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
}
