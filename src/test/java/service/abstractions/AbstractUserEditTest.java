package service.abstractions;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import service.api.BurgerApi;
import service.json.User;
import service.utilities.TestUtilities;

public class AbstractUserEditTest {
    protected User user = null;
    protected Response response = null;
    protected String accessToken = null;

    @Before
    public void initTestData() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        user = TestUtilities.getNewUser();
        response = BurgerApi.sendPostAuthRegister(user);
        TestUtilities.compareResponseStatusCode(response,200);
        accessToken = TestUtilities.getAccessToken(response);
    }

    @After
    public void clearTestData() {
        if (accessToken != null) {
            BurgerApi.sendDeleteAuthUser(accessToken);
        }
        user = null;
        response = null;
        accessToken = null;
    }
}
