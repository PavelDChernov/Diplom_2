package service.abstractions;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import service.api.BurgerApi;
import service.json.Ingredients;
import service.json.User;
import service.utilities.TestUtilities;

import java.util.List;

public class AbstractOrdersGetTest {
    protected final Ingredients INGREDIENTS = new Ingredients(List.of("61c0c5a71d1f82001bdaaa73", "61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa6d"));
    protected final int ORDERS_NUM = 1;
    protected User user = null;
    protected String accessToken = null;
    protected Response response = null;

    @Before
    public void initTestData() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        user = TestUtilities.getNewUser();
        response = BurgerApi.sendPostAuthRegister(user);
        TestUtilities.compareResponseStatusCode(response, 200);
        accessToken = TestUtilities.getAccessToken(response);
        for (int i = 0; i < ORDERS_NUM; i++) {
            response = BurgerApi.sendPostOrders(INGREDIENTS, accessToken);
            TestUtilities.compareResponseStatusCode(response, 200);
        }
    }

    @After
    public void clearTestData() {
        // Удаляется только созданный пользователь, т.к. нет ручки для удаления/отмены созданного заказа
        if (accessToken != null) {
            BurgerApi.sendDeleteAuthUser(accessToken);
        }
        user = null;
        accessToken = null;
        response = null;
    }
}
