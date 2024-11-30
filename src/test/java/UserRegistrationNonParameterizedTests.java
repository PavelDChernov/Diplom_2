import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Assert;
import org.junit.Test;
import service.abstractions.AbstractUserRegistrationTest;
import service.json.AuthorizedUserData;
import service.json.ErrorResponse;
import service.json.User;

import static io.restassured.RestAssured.given;

public class UserRegistrationNonParameterizedTests extends AbstractUserRegistrationTest {
    @Test
    @DisplayName("Check status code and response body of POST /api/auth/register on success")
    @Description("Endpoint returns 200 and correct response body on success")
    public void userRegistrationCheck200ResponseOnSuccess() {
        response = sendPostAuthRegister(user);
        compareResponseStatusCode(response,200);
        checkSuccessfullResponseBody(response, user);
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/auth/register if user already exists")
    @Description("Endpoint returns 403 and correct response body if registering user already exists")
    public void userRegistrationCheck403ResponseIfAlreadyExists() {
        response = sendPostAuthRegister(user);
        compareResponseStatusCode(response,200);
        response = sendPostAuthRegister(user);
        compareResponseStatusCode(response,403);
        checkErrorResponseBody(response, "User already exists");
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
    public void compareResponseStatusCode(Response response, int expectedStatus) {
        response.then().assertThat().statusCode(expectedStatus);
    }

    // проверить поля ответа при успехе
    @Step("Check successfull response body")
    public void checkSuccessfullResponseBody(Response response, User user) {
        AuthorizedUserData responseBody = response.as(AuthorizedUserData.class);
        Assert.assertTrue(responseBody.isSuccess());
        Assert.assertFalse(responseBody.getAccessToken().isEmpty());
        Assert.assertFalse(responseBody.getRefreshToken().isEmpty());
        Assert.assertEquals(user.getName(), responseBody.getUser().getName());
        Assert.assertEquals(user.getEmail().toLowerCase(), responseBody.getUser().getEmail());
    }

    // проверить поля ответа при ошибке
    @Step("Check error response body")
    public void checkErrorResponseBody(Response response, String expectedError) {
        ErrorResponse responseBody = response.as(ErrorResponse.class);
        Assert.assertFalse(responseBody.isSuccess());
        Assert.assertEquals(expectedError, responseBody.getMessage());
    }
}
