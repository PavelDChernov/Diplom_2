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
import service.json.AuthData;
import service.json.AuthorizedUserData;
import service.json.ErrorResponse;
import service.json.User;

import java.util.Random;

import static io.restassured.RestAssured.given;

public class UserRegistrationNonParameterizedTests {
    private User user = null;
    private Response response = null;

    @Before
    public void initTestData() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/auth/register on success")
    @Description("Endpoint returns 200 and correct response body on success")
    public void userRegistrationCheck200ResponseOnSuccess() {
        user = getNewUser();
        response = sendPostAuthRegister(user);
        compareResponseStatusCode(response,200);
        checkSuccessfullResponseBody(response, user);
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/auth/register if user already exists")
    @Description("Endpoint returns 403 and correct response body if registering user already exists")
    public void userRegistrationCheck403ResponseIfAlreadyExists() {
        user = getNewUser();
        response = sendPostAuthRegister(user);
        compareResponseStatusCode(response,200);
        response = sendPostAuthRegister(user);
        compareResponseStatusCode(response,403);
        checkErrorResponseBody(response, "User already exists");
    }

    @After
    public void clearTestData() {
        if (user != null) {
            response = sendPostAuthLogin(user.getEmail(), user.getPassword());
            if (response.getStatusCode() == 200) {
                String accessToken = getAccessToken(response);
                sendDeleteAuthUser(accessToken);
            }
            user = null;
            response = null;
        }
    }

    @Step("Get new user")
    public User getNewUser() {
        return new User(String.format("mailbox%s@sometestmail.su", new Random().nextInt(100500)).toLowerCase(), "derP@r0l", "Яков");
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

    @Step("Check successfull response body")
    public void checkSuccessfullResponseBody(Response response, User user) {
        AuthorizedUserData responseBody = response.as(AuthorizedUserData.class);
        Assert.assertTrue(responseBody.isSuccess());
        Assert.assertFalse(responseBody.getAccessToken().isEmpty());
        Assert.assertFalse(responseBody.getRefreshToken().isEmpty());
        Assert.assertEquals(user.getName(), responseBody.getUser().getName());
        Assert.assertEquals(user.getEmail().toLowerCase(), responseBody.getUser().getEmail());
    }

    @Step("Check error response body")
    public void checkErrorResponseBody(Response response, String expectedError) {
        ErrorResponse responseBody = response.as(ErrorResponse.class);
        Assert.assertFalse(responseBody.isSuccess());
        Assert.assertEquals(expectedError, responseBody.getMessage());
    }

    @Step("Send POST /api/auth/login")
    public Response sendPostAuthLogin(String email, String password) {
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(new AuthData(email, password))
                        .when()
                        .post("/api/auth/login");
        return response;
    }

    @Step("Get access token from response")
    public String getAccessToken(Response response) {
        return response.path("accessToken");
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
