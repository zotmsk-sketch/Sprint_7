package praktikum.tests;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import praktikum.models.Courier;
import praktikum.steps.CourierSteps;

import static org.hamcrest.Matchers.equalTo;

public class CourierDeleteTest {
    private CourierSteps courierSteps = new CourierSteps();

    @Test
    @DisplayName("Успешное удаление курьера")
    public void deleteCourierSuccess() {
        String login = "del_" + System.currentTimeMillis();
        Courier courier = new Courier(login, "pass", "Del");
        courierSteps.createCourier(courier);
        int id = courierSteps.getCourierId(courierSteps.loginCourier(courier));
        var response = courierSteps.deleteCourier(id);
        response.then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Удаление курьера без id")
    public void deleteWithoutId() {
        var response = courierSteps.getBaseSpec()
                .delete(CourierSteps.COURIER_PATH);
        response.then().statusCode(404); // исправлено с 405 на 404
    }

    @Test
    @DisplayName("Удаление несуществующего курьера")
    public void deleteNonExistent() {
        var response = courierSteps.deleteCourier(999999);
        response.then()
                .statusCode(404)
                .body("message", equalTo("Курьера с таким id нет."));
    }
}