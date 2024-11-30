import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import service.json.ErrorResponse;
import service.json.User;

import static io.restassured.RestAssured.given;

@RunWith(Parameterized.class)
public class UserRegistrationParameterizedTests {
    private final User user;
    private final int expectedStatus;
    private final String expectedError;

    public UserRegistrationParameterizedTests(User user, int expectedStatus, String expectedError) {
        this.user = user;
        this.expectedStatus = expectedStatus;
        this.expectedError = expectedError;
    }

    @Parameterized.Parameters
    public static Object[][] dataForTest() {
        return new Object[][]{
                { new User(null, "derP@r0l", "Иннокентий"), 403, "Email, password and name are required fields" },
                { new User("failbox100500@qatestmail.su", null, "Boris"), 403, "Email, password and name are required fields" },
                { new User("failbox500100@qatestmail.su", "derP@r0l", null), 403, "Email, password and name are required fields" },
        };
    }

    @Before
    public void initTestData() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/auth/register on registration error")
    @Description("Endpoint returns correct status code and response body if no email provided on registration")
    public void userRegistrationCheckResponseOnError() {
        Response response = sendPostAuthRegister(user);
        compareResponseStatusCode(response,expectedStatus);
        checkErrorResponseBody(response, expectedError);
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

    @Step("Check error response body")
    public void checkErrorResponseBody(Response response, String expectedError) {
        ErrorResponse responseBody = response.as(ErrorResponse.class);
        Assert.assertFalse(responseBody.isSuccess());
        Assert.assertEquals(expectedError, responseBody.getMessage());
    }
}
