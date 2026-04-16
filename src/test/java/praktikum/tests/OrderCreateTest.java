package praktikum.tests;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.models.Order;
import praktikum.steps.OrderSteps;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreateTest {
    private final List<String> colors;
    private int track;
    private OrderSteps orderSteps = new OrderSteps();

    public OrderCreateTest(List<String> colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "Цвета: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Arrays.asList("BLACK")},
                {Arrays.asList("GREY")},
                {Arrays.asList("BLACK", "GREY")},
                {Collections.emptyList()}
        });
    }

    @Test
    @DisplayName("Создание заказа с разными вариантами цвета")
    public void createOrderWithColors() {
        Order order = new Order(
                "Алексей", "Смирнов", "ул. Ленина, 5", "Парк культуры",
                "+79998887766", 3, "2025-06-01", colors, "Позвонить за час"
        );
        var response = orderSteps.createOrder(order);
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
        track = orderSteps.getTrackFromResponse(response);
    }

    @After
    public void cancelOrder() {
        if (track != 0) {
            orderSteps.cancelOrderByTrack(track);
        }
    }
}
//
