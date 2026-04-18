package praktikum.tests;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.models.Order;
import praktikum.steps.OrderSteps;

import java.util.Collections;

import static org.hamcrest.Matchers.notNullValue;

public class OrderGetByNumberTest {
    private OrderSteps orderSteps = new OrderSteps();
    private int track;

    @Before
    public void createOrder() {
        Order order = new Order(
                "Петр", "Петров", "ул. Пушкина, 10", "Маяковская",
                "+79161234567", 2, "2025-07-15", Collections.emptyList(), ""
        );
        var resp = orderSteps.createOrder(order);
        track = orderSteps.getTrackFromResponse(resp);
    }

    @After
    public void cancelOrder() {
        if (track != 0) {
            orderSteps.cancelOrderByTrack(track);
        }
    }

    @Test
    @DisplayName("Получение заказа по существующему треку")
    public void getOrderByValidTrack() {
        var response = orderSteps.getOrderByTrack(track);
        response.then()
                .statusCode(200)
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Запрос без трека")
    public void getOrderWithoutTrack() {
        var response = orderSteps.getBaseSpec()
                .get(OrderSteps.GET_ORDER_BY_NUMBER_PATH);
        response.then().statusCode(400);
    }

    @Test
    @DisplayName("Несуществующий трек")
    public void getOrderByInvalidTrack() {
        var response = orderSteps.getOrderByTrack(0);
        response.then().statusCode(404);
    }
}
//
