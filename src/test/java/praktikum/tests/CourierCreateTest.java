package praktikum.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.models.Courier;
import praktikum.steps.CourierSteps;

import static org.hamcrest.Matchers.equalTo;

public class CourierCreateTest {
    private CourierSteps courierSteps;
    private Courier courier;
    private int courierId;

    @Before
    public void setUp() {
        courierSteps = new CourierSteps();
        String uniqueLogin = "courier_" + System.currentTimeMillis();
        courier = new Courier(uniqueLogin, "password123", "Ivan");
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            courierSteps.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Успешное создание курьера")
    public void createCourierSuccess() {
        Response createResponse = courierSteps.createCourier(courier);
        createResponse.then()
                .statusCode(201)
                .body("ok", equalTo(true));

        Response loginResponse = courierSteps.loginCourier(courier);
        courierId = courierSteps.getCourierId(loginResponse);
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void createDuplicateCourierFails() {
        courierSteps.createCourier(courier).then().statusCode(201);
        Response duplicateResponse = courierSteps.createCourier(courier);
        duplicateResponse.then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой.")); // исправлено

        Response loginResponse = courierSteps.loginCourier(courier);
        courierId = courierSteps.getCourierId(loginResponse);
    }

    @Test
    @DisplayName("Создание курьера без пароля возвращает ошибку")
    public void createCourierWithoutPasswordFails() {
        courier.setPassword(null);
        Response response = courierSteps.createCourier(courier);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без логина возвращает ошибку")
    public void createCourierWithoutLoginFails() {
        courier.setLogin(null);
        Response response = courierSteps.createCourier(courier);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}