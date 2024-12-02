package service.abstractions;

import io.restassured.RestAssured;
import org.junit.Before;

public class AbstractTest {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";

    @Before
    public void initRestAssured() {
        RestAssured.baseURI = BASE_URI;
    }
}
