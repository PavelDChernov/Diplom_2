package service.abstractions;

import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import service.api.BurgerApi;
import service.json.User;
import service.utilities.TestUtilities;

public class AbstractOrderCreationTest extends AbstractTest {
    protected User user;
    protected Response response;
    protected String accessToken;

    @Before
    public void initTestData() {
        user = TestUtilities.getNewUser();
        response = BurgerApi.sendPostAuthRegister(user);
        TestUtilities.compareResponseStatusCode(response,200);
        accessToken = TestUtilities.getAccessToken(response);
    }

    @After
    public void clearTestData() {
        // Удаляется только созданный пользователь, т.к. нет ручки для удаления/отмены созданного заказа
        if (accessToken != null) {
            BurgerApi.sendDeleteAuthUser(accessToken);
        }
    }
}
