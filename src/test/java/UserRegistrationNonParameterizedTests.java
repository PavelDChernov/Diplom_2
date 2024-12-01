import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Assert;
import org.junit.Test;
import service.abstractions.AbstractUserRegistrationTest;
import service.api.BurgerApi;
import service.json.AuthorizedUserData;
import service.json.User;
import service.utilities.TestUtilities;

import static io.restassured.RestAssured.given;

public class UserRegistrationNonParameterizedTests extends AbstractUserRegistrationTest {
    @Test
    @DisplayName("Check status code and response body of POST /api/auth/register on success")
    @Description("Endpoint returns 200 and correct response body on success")
    public void userRegistrationCheck200ResponseOnSuccess() {
        response = sendPostAuthRegister(user);
        compareResponseStatusCode(response,200);
        compareResponseSuccessField(response, true);
        checkResponseAccessTokenFieldNotEmpty(response);
        checkResponseRefreshTokenFieldNotEmpty(response);
        compareResponseUserNameField(response, user.getName());
        compareResponseUserEmailField(response, user.getEmail());
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/auth/register if user already exists")
    @Description("Endpoint returns 403 and correct response body if registering user already exists")
    public void userRegistrationCheck403ResponseIfAlreadyExists() {
        // подготовка
        response = BurgerApi.sendPostAuthRegister(user);
        TestUtilities.compareResponseStatusCode(response,200);
        // тест
        response = sendPostAuthRegister(user);
        compareResponseStatusCode(response,403);
        compareResponseSuccessField(response, false);
        compareResponseMessageField(response, "User already exists");
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

    // проверить, что передан accessToken
    @Step("Check that response body access token field is not empty")
    public void checkResponseAccessTokenFieldNotEmpty(Response response) {
        AuthorizedUserData responseBody = response.as(AuthorizedUserData.class);
        Assert.assertFalse(responseBody.getAccessToken().isEmpty());
    }

    // проверить, что передан refreshToken
    @Step("Check that response body access token field is not empty")
    public void checkResponseRefreshTokenFieldNotEmpty(Response response) {
        AuthorizedUserData responseBody = response.as(AuthorizedUserData.class);
        Assert.assertFalse(responseBody.getRefreshToken().isEmpty());
    }

    @Step("Compare response user name field")
    public void compareResponseUserNameField(Response response, String expectedName) {
        AuthorizedUserData responseBody = response.as(AuthorizedUserData.class);
        Assert.assertEquals(expectedName, responseBody.getUser().getName());
    }

    @Step("Compare response user email field")
    public void compareResponseUserEmailField(Response response, String expectedEmail) {
        AuthorizedUserData responseBody = response.as(AuthorizedUserData.class);
        Assert.assertEquals(expectedEmail.toLowerCase(), responseBody.getUser().getEmail());
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
