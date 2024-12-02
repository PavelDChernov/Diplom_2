package service.abstractions;

import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import service.api.BurgerApi;
import service.json.User;
import service.utilities.TestUtilities;

public class AbstractUserRegistrationTest extends AbstractTest {
    protected User user;
    protected Response response;

    @Before
    public void initTestData() {
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
        }
    }
}
