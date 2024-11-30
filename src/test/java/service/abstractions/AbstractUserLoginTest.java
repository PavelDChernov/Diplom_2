package service.abstractions;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import service.api.BurgerApi;
import service.json.AuthData;
import service.json.User;
import service.utilities.TestUtilities;

public class AbstractUserLoginTest {
    protected User user = null;
    protected Response response = null;
    protected AuthData authData = null;

    @Before
    public void initTestData() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        user = TestUtilities.getNewUser();
        response = BurgerApi.sendPostAuthRegister(user);
        TestUtilities.compareResponseStatusCode(response,200);
        authData = TestUtilities.getUserAuthData(user);
    }

    @After
    public void clearTestData() {
        if (user != null) {
            response = BurgerApi.sendPostAuthLogin(new AuthData(user.getEmail(), user.getPassword()));
            if (response.getStatusCode() == 200) {
                String accessToken = TestUtilities.getAccessToken(response);
                BurgerApi.sendDeleteAuthUser(accessToken);
            }
            user = null;
            response = null;
        }
    }
}
