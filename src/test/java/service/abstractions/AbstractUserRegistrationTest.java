package service.abstractions;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import service.api.BurgerApi;
import service.json.User;
import service.utilities.TestUtilities;

import static service.api.BurgerApi.sendPostAuthRegister;

public class AbstractUserRegistrationTest {
    protected User user = null;
    protected Response response = null;

    @Before
    public void initTestData() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        user = TestUtilities.getNewUser();
    }

    @After
    public void clearTestData() {
        if (user != null) {
            response = BurgerApi.sendPostAuthLogin(user.getEmail(), user.getPassword());
            if (response.getStatusCode() == 200) {
                String accessToken = TestUtilities.getAccessToken(response);
                BurgerApi.sendDeleteAuthUser(accessToken);
            }
            user = null;
            response = null;
        }
    }
}
