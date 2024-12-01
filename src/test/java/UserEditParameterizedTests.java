import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import service.abstractions.AbstractUserEditTest;
import service.json.*;

import java.util.Random;

import static io.restassured.RestAssured.given;

@RunWith(Parameterized.class)
public class UserEditParameterizedTests extends AbstractUserEditTest {
    private final String newEmail;
    private final String newPassword;
    private final String newName;

    public UserEditParameterizedTests(String newEmail, String newPassword, String newName) {
        this.newEmail = newEmail;
        this.newPassword = newPassword;
        this.newName = newName;
    }

    @Parameterized.Parameters
    public static Object[][] dataForTest() {
        return new Object[][]{
                { String.format("newmailbox%s@qasometestmail.su", new Random().nextInt(100500)), "newPa$$w0rd1", "Парфирий" },
                { String.format("newmailbox%s@qasometestmail.su", new Random().nextInt(100500)), null, null },
                { null, "8nOt@pAs$w0rD8", null },
                { null, null, "Иннокентий" },
        };
    }

    @Test
    @DisplayName("Check status code, response body and user data of PATCH /api/auth/user on success")
    @Description("Endpoint returns 200 and correct response body on success, new data saved")
    public void userEditCheck200ResponseOnSuccess() {
        User newUserData = prepareNewUserData(newEmail, newPassword, newName, user);
        response = sendPatchAuthUser(newUserData, accessToken);
        compareResponseStatusCode(response,200);
        compareResponseSuccessField(response, true);
        compareResponseUserNameField(response, newUserData.getName());
        compareResponseUserEmailField(response, newUserData.getEmail());
        if (newEmail != null || newPassword != null) {
            response = sendPostAuthLogin(new AuthData(newUserData.getEmail(), newUserData.getPassword()));
            compareResponseStatusCode(response, 200);
            accessToken = getAccessToken(response);
        }
        response = sendGetAuthUser(accessToken);
        compareResponseStatusCode(response,200);
        compareResponseUserNameField(response, newUserData.getName());
        compareResponseUserEmailField(response, newUserData.getEmail());
    }

    @Test
    @DisplayName("Check status code, response body and user data of PATCH /api/auth/user without authorization token")
    @Description("Endpoint returns 401 and correct response body on success, new data not saved")
    public void userEditCheck401ResponseOnNoToken() {
        User newUserData = prepareNewUserData(newEmail, newPassword, newName, user);
        response = sendPatchAuthUser(newUserData, null);
        compareResponseStatusCode(response,401);
        compareResponseSuccessField(response, false);
        compareResponseMessageField(response, "You should be authorised");
        if (newEmail != null || newPassword != null) {
            response = sendPostAuthLogin(new AuthData(newUserData.getEmail(), newUserData.getPassword()));
            compareResponseStatusCode(response, 401);
            response = sendPostAuthLogin(new AuthData(user.getEmail(), user.getPassword()));
            compareResponseStatusCode(response,200);
            accessToken = getAccessToken(response);
        }
        response = sendGetAuthUser(accessToken);
        compareResponseStatusCode(response,200);
        compareResponseUserNameField(response, user.getName());
        compareResponseUserEmailField(response, user.getEmail());
    }

    @Step("Compare response status code")
    public void compareResponseStatusCode(Response response, int expectedStatusCode) {
        response.then().assertThat().statusCode(expectedStatusCode);
    }

    // подготовить новые данные пользователя
    @Step("Prepare new user data based on parameters")
    public User prepareNewUserData(String newEmail, String newPassword, String newName, User currentUser) {
        User newUserData = new User(currentUser.getEmail(), currentUser.getPassword(), currentUser.getName());
        if (newEmail != null) {
            newUserData.setEmail(newEmail.toLowerCase());
        }
        if (newPassword != null) {
            newUserData.setPassword(newPassword);
        }
        if (newName != null) {
            newUserData.setName(newName);
        }
        return newUserData;
    }

    @Step("Send PATCH /api/auth/user")
    public Response sendPatchAuthUser(User newUserData, String accessToken) {
        if (accessToken == null) {
            Response response =
                    given()
                            .contentType(ContentType.JSON)
                            .and()
                            .body(newUserData)
                            .when()
                            .patch("/api/auth/user");
            return response;
        }
        Response response =
                given()
                        .header("authorization", accessToken)
                        .and()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(newUserData)
                        .when()
                        .patch("/api/auth/user");
        return response;
    }

    @Step("Send GET /api/auth/user")
    public Response sendGetAuthUser(String accessToken) {
        Response response =
                given()
                        .header("authorization", accessToken)
                        .get("/api/auth/user");
        return response;
    }

    @Step("Compare response user name field")
    public void compareResponseUserNameField(Response response, String expectedName) {
        Assert.assertEquals(expectedName, response.path("user.name").toString());
    }

    @Step("Compare response user email field")
    public void compareResponseUserEmailField(Response response, String expectedEmail) {
        Assert.assertEquals(expectedEmail.toLowerCase(), response.path("user.email").toString());
    }

    @Step("Compare response body success field")
    public void compareResponseSuccessField(Response response, boolean expectedSuccess) {
        Assert.assertEquals(expectedSuccess, response.path("success"));
    }

    @Step("Compare response body message field")
    public void compareResponseMessageField(Response response, String expectedMessage) {
        Assert.assertEquals(expectedMessage, response.path("message"));
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

    // получить токен авторизации из ответа
    @Step("Get access token from response")
    public String getAccessToken(Response response) {
        return response.path("accessToken");
    }
}
