package service.abstractions;

import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import service.api.BurgerApi;
import service.json.Ingredients;
import service.json.User;
import service.utilities.TestUtilities;
import java.util.Random;

public class AbstractOrdersGetTest extends AbstractTest {
    protected static final int ORDERS_NUM = 2;
    protected static final int MAX_INGREDIENTS = 5;

    protected User user;
    protected String accessToken;
    protected Response response;
    protected Ingredients ingredients;

    @Before
    public void initTestData() {
        ingredients = TestUtilities.getNewIngredientsList(new Random().nextInt(MAX_INGREDIENTS) + 1);
        user = TestUtilities.getNewUser();
        response = BurgerApi.sendPostAuthRegister(user);
        TestUtilities.compareResponseStatusCode(response, 200);
        accessToken = TestUtilities.getAccessToken(response);
        for (int i = 0; i < ORDERS_NUM; i++) {
            response = BurgerApi.sendPostOrders(ingredients, accessToken);
            TestUtilities.compareResponseStatusCode(response, 200);
        }
    }

    @After
    public void clearTestData() {
        // Удаляется только созданный пользователь, т.к. нет ручки для удаления/отмены созданного заказа
        if (accessToken != null) {
            BurgerApi.sendDeleteAuthUser(accessToken);
        }
    }
}
