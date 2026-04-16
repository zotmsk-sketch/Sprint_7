package praktikum.client;

import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.http.params.CoreConnectionPNames;

import static io.restassured.RestAssured.given;

public class ScooterClient {
    public static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    public static final String COURIER_PATH = "/api/v1/courier";
    public static final String COURIER_LOGIN_PATH = "/api/v1/courier/login";
    public static final String ORDERS_PATH = "/api/v1/orders";
    public static final String ACCEPT_ORDER_PATH = "/api/v1/orders/accept/";
    public static final String GET_ORDER_BY_NUMBER_PATH = "/api/v1/orders/track";

    public RequestSpecification getBaseSpec() {
        return given()
                .config(RestAssuredConfig.config()
                        .httpClient(HttpClientConfig.httpClientConfig()
                                .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000)
                                .setParam(CoreConnectionPNames.SO_TIMEOUT, 60000)))
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL);
    }
}