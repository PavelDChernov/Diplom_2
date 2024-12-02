import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.qameta.allure.Description;
import org.junit.Assert;
import org.junit.Test;
import service.abstractions.AbstractUserLoginTest;
import service.api.BurgerApi;
import service.json.AuthData;
import service.json.AuthorizedUserData;

public class UserLoginTests extends AbstractUserLoginTest {
    @Test
    @DisplayName("Check status code and response body of POST /api/auth/login on success")
    @Description("Endpoint returns 200 and correct response body on success")
    public void userLoginCheck200ResponseOnSuccess() {
        sendPostAuthLogin(authData);
        compareResponseStatusCode(response,200);
        compareResponseSuccessField(response, true);
        checkResponseAccessTokenFieldNotEmpty(response);
        checkResponseRefreshTokenFieldNotEmpty(response);
        compareResponseUserNameField(response, user.getName());
        compareResponseUserEmailField(response, user.getEmail());
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/auth/login on incorrect email")
    @Description("Endpoint returns 401 and correct response body if incorrect email provided on login")
    public void userLoginCheck401ResponseOnIncorrectEmail() {
        sendPostAuthLogin(new AuthData("a1" + authData.getEmail(), authData.getPassword()));
        compareResponseStatusCode(response,401);
        compareResponseSuccessField(response, false);
        compareResponseMessageField(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/auth/login on incorrect password")
    @Description("Endpoint returns 401 and correct response body if incorrect password provided on login")
    public void userLoginCheck401ResponseOnIncorrectPassword() {
        sendPostAuthLogin(new AuthData(authData.getEmail(), authData.getPassword() + "Z9"));
        compareResponseStatusCode(response,401);
        compareResponseSuccessField(response, false);
        compareResponseMessageField(response, "email or password are incorrect");
    }

    @Step("Compare response status code")
    public void compareResponseStatusCode(Response response, int expectedStatusCode) {
        response.then().assertThat().statusCode(expectedStatusCode);
    }

    @Step("Send POST /api/auth/login")
    public void sendPostAuthLogin(AuthData authData) {
        response = BurgerApi.sendPostAuthLogin(authData);
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
