package praktikum.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import praktikum.models.Courier;
import praktikum.steps.CourierSteps;

import static org.hamcrest.Matchers.*;

public class CourierLoginTest {
    private CourierSteps courierSteps;
    private Courier courier;
    private int courierId;

    @Before
    public void createTestCourier() {
        courierSteps = new CourierSteps();
        String login = "login_" + System.currentTimeMillis();
        courier = new Courier(login, "validPass", "TestName");
        courierSteps.createCourier(courier).then().statusCode(201);
        Response loginResponse = courierSteps.loginCourier(courier);
        courierId = courierSteps.getCourierId(loginResponse);
    }

    @After
    public void deleteTestCourier() {
        if (courierId != 0) {
            courierSteps.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Успешный логин курьера")
    public void loginSuccess() {
        Response response = courierSteps.loginCourier(courier);
        response.then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    public void loginWrongPassword() {
        Courier wrongCourier = new Courier(courier.getLogin(), "wrongPass", null);
        Response response = courierSteps.loginCourier(wrongCourier);
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин с неверным логином")
    public void loginWrongLogin() {
        Courier wrongCourier = new Courier("nonexistent", courier.getPassword(), null);
        Response response = courierSteps.loginCourier(wrongCourier);
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    // Тест временно отключён из-за бага API:
    // при запросе без пароля сервер зависает и возвращает 504 Gateway Timeout вместо 400.
    // проверен в Postman
    // Баг зарегистрирован. После исправления сервера тест можно включить.
    @Ignore("Баг API: запрос без пароля приводит к таймауту (504), а не к 400")
    @Test
    @DisplayName("Логин без пароля")
    public void loginWithoutPassword() {
        courier.setPassword(null);
        Response response = courierSteps.loginCourier(courier);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}
//
