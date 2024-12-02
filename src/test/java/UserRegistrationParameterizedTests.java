import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import service.abstractions.AbstractTest;
import service.api.BurgerApi;
import service.json.User;

import java.util.Random;

@RunWith(Parameterized.class)
public class UserRegistrationParameterizedTests extends AbstractTest {
    private final User user;
    private Response response;

    public UserRegistrationParameterizedTests(User user) {
        this.user = user;
    }

    @Parameterized.Parameters
    public static Object[][] dataForTest() {
        return new Object[][]{
                { new User(
                           null,
                        "derP@r0l",
                           "Иннокентий"
                          )
                },
                { new User(
                            String.format("newmailbox%s@qasometestmail.su", new Random().nextInt(100500)),
                   null,
                      "Boris"
                          )
                },
                { new User(
                            String.format("newmailbox%s@qasometestmail.su", new Random().nextInt(100500)),
                   "n07P@$$W0rD",
                      null
                          )
                },
        };
    }

    @Before
    public void initTestData() {
        sendPostAuthRegister(user);
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
    public void sendPostAuthRegister(User user) {
        response = BurgerApi.sendPostAuthRegister(user);
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
