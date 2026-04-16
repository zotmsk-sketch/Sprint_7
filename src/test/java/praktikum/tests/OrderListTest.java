package praktikum.tests;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import praktikum.steps.OrderSteps;

import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest {
    private OrderSteps orderSteps = new OrderSteps();

    @Test
    @DisplayName("Получение списка заказов возвращает массив")
    public void getOrdersListReturnsArray() {
        var response = orderSteps.getOrdersList(); // здесь retry
        response.then()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders", instanceOf(List.class));
    }
}