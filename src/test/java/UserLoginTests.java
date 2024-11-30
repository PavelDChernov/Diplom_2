import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Assert;
import org.junit.Test;
import service.abstractions.AbstractUserLoginTest;
import service.json.AuthData;
import service.json.AuthorizedUserData;
import service.json.ErrorResponse;
import service.json.User;

import static io.restassured.RestAssured.given;

public class UserLoginTests extends AbstractUserLoginTest {
    @Test
    @DisplayName("Check status code and response body of POST /api/auth/login on success")
    @Description("Endpoint returns 200 and correct response body on success")
    public void userLoginCheck200ResponseOnSuccess() {
        response = sendPostAuthLogin(authData);
        compareResponseStatusCode(response,200);
        checkSuccessfullResponseBody(response, user);
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/auth/login on incorrect email")
    @Description("Endpoint returns 401 and correct response body if incorrect email provided on login")
    public void userLoginCheck401ResponseOnIncorrectEmail() {
        response = sendPostAuthLogin(new AuthData("a1" + authData.getEmail(), authData.getPassword()));
        compareResponseStatusCode(response,401);
        checkErrorResponseBody(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/auth/login on incorrect password")
    @Description("Endpoint returns 401 and correct response body if incorrect password provided on login")
    public void userLoginCheck401ResponseOnIncorrectPassword() {
        response = sendPostAuthLogin(new AuthData(authData.getEmail(), authData.getPassword() + "Z9"));
        compareResponseStatusCode(response,401);
        checkErrorResponseBody(response, "email or password are incorrect");
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
