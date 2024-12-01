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
import service.json.User;

import static io.restassured.RestAssured.given;

@RunWith(Parameterized.class)
public class UserRegistrationParameterizedTests {
    private final User user;
    private Response response;

    public UserRegistrationParameterizedTests(User user) {
        this.user = user;
    }

    @Parameterized.Parameters
    public static Object[][] dataForTest() {
        return new Object[][]{
                { new User(null,                          "derP@r0l",   "Иннокентий")   },
                { new User("failbox100500@qatestmail.su", null,         "Boris")        },
                { new User("failbox500100@qatestmail.su", "derP@r0l",   null)           },
        };
    }

    @Before
    public void initTestData() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        response = sendPostAuthRegister(user);
    }

    @Test
    @DisplayName("Check status code and response body of POST /api/auth/register on registration error")
    @Description("Endpoint returns correct status code and response body if no email provided on registration")
    public void userRegistrationCheckResponseOnError() {
        compareResponseStatusCode(response,403);
        compareResponseSuccessField(response,false);
        compareResponseMessageField(response, "Email, password and name are required fields");
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

    @Step("Compare response body success field")
    public void compareResponseSuccessField(Response response, boolean expectedSuccess) {
        Assert.assertEquals(expectedSuccess, response.path("success"));
    }

    @Step("Compare response body message field")
    public void compareResponseMessageField(Response response, String expectedMessage) {
        Assert.assertEquals(expectedMessage, response.path("message").toString());
    }
}
