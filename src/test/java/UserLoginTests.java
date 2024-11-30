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

public class UserLoginTests {
    private User user = null;
    private Response response = null;

    @Before
    public void initTestData() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        user = getNewUser();
        response = sendPostAuthRegister(user);
        compareResponseStatusCode(response,200);
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/auth/login on success")
    @Description("Endpoint returns 200 and correct response body on success")
    public void userLoginCheck200ResponseOnSuccess() {
        AuthData authData = getUserAuthData(user);
        response = sendPostAuthLogin(authData);
        compareResponseStatusCode(response,200);
        checkSuccessfullResponseBody(response, user);
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/auth/login on incorrect email")
    @Description("Endpoint returns 401 and correct response body if incorrect email provided on login")
    public void userLoginCheck401ResponseOnIncorrectEmail() {
        AuthData authData = getUserAuthData(user);
        response = sendPostAuthLogin(new AuthData("a1" + authData.getEmail(), authData.getPassword()));
        compareResponseStatusCode(response,401);
        checkErrorResponseBody(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/auth/login on incorrect password")
    @Description("Endpoint returns 401 and correct response body if incorrect password provided on login")
    public void userLoginCheck401ResponseOnIncorrectPassword() {
        AuthData authData = getUserAuthData(user);
        response = sendPostAuthLogin(new AuthData(authData.getEmail(), authData.getPassword() + "Z9"));
        compareResponseStatusCode(response,401);
        checkErrorResponseBody(response, "email or password are incorrect");
    }

    @After
    public void clearTestData() {
        if (user != null) {
            response = sendPostAuthLogin(new AuthData(user.getEmail(), user.getPassword()));
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
        return new User(String.format("mailboxqa%s@sometestmail.su", new Random().nextInt(100500)).toLowerCase(), "derP@r0l", "Яков");
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

    @Step("Get user auth data")
    public AuthData getUserAuthData(User user) {
        return new AuthData(user.getEmail(), user.getPassword());
    }

    @Step("Compare response status code")
    public void compareResponseStatusCode(Response response, int expectedStatusCode) {
        response.then().assertThat().statusCode(expectedStatusCode);
    }

    @Step("Send POST /api/auth/login")
    public Response sendPostAuthLogin(AuthData authData) {
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(authData)
                        .when()
                        .post("/api/auth/login");
        return response;
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
