package praktikum.tests;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.models.Courier;
import praktikum.models.Order;
import praktikum.steps.CourierSteps;
import praktikum.steps.OrderSteps;

import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;

public class OrderAcceptTest {
    private CourierSteps courierSteps = new CourierSteps();
    private OrderSteps orderSteps = new OrderSteps();
    private int courierId;
    private int orderId;

    @Before
    public void prepareData() {
        // Создание курьера
        Courier courier = new Courier("accept_" + System.currentTimeMillis(), "pass", "Acc");
        courierSteps.createCourier(courier);
        courierId = courierSteps.getCourierId(courierSteps.loginCourier(courier));

        // Создание заказа
        Order order = new Order(
                "Тест", "Тестов", "Адрес", "Сокол", "+70000000000",
                1, "2025-12-31", Collections.emptyList(), ""
        );
        var createResp = orderSteps.createOrder(order);
        int track = createResp.jsonPath().getInt("track");

        // Получение заказа по треку, чтобы узнать его ID
        var getOrderResp = orderSteps.getOrderByTrack(track);
        orderId = getOrderResp.jsonPath().getInt("order.id");
        // Если поле называется просто "id", используйте:
        if (orderId == 0) {
            orderId = getOrderResp.jsonPath().getInt("id");
        }
    }

    @After
    public void cleanup() {
        if (courierId != 0) {
            courierSteps.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Успешное принятие заказа")
    public void acceptOrderSuccess() {
        var response = orderSteps.acceptOrder(orderId, courierId);
        response.then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Принятие заказа без courierId")
    public void acceptOrderWithoutCourierId() {
        var response = orderSteps.getBaseSpec()
                .pathParam("id", orderId)
                .put(OrderSteps.ACCEPT_ORDER_PATH + "{id}");
        response.then().statusCode(400);
    }

    @Test
    @DisplayName("Принятие заказа с неверным номером")
    public void acceptOrderWrongNumber() {
        var response = orderSteps.acceptOrder(999999, courierId);
        response.then().statusCode(404);
    }
}
//
