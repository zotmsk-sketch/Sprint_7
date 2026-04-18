package praktikum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import praktikum.client.ScooterClient;
import praktikum.models.Order;

public class OrderSteps extends ScooterClient {

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    @Step("Создание заказа: имя = {order.firstName}, цвет = {order.color}")
    public Response createOrder(Order order) {
        return getBaseSpec().body(order).post(ORDERS_PATH);
    }

    @Step("Получение списка заказов")
    public Response getOrdersList() {
        return retry(() -> getBaseSpec().get(ORDERS_PATH));
    }

    @Step("Принятие заказа: orderId = {orderId}, courierId = {courierId}")
    public Response acceptOrder(int orderId, int courierId) {
        return getBaseSpec()
                .pathParam("id", orderId)
                .queryParam("courierId", courierId)
                .put(ACCEPT_ORDER_PATH + "{id}");
    }

    @Step("Получение заказа по треку: track = {track}")
    public Response getOrderByTrack(int track) {
        return getBaseSpec()
                .queryParam("t", track)
                .get(GET_ORDER_BY_NUMBER_PATH);
    }

    @Step("Отмена заказа по треку (если есть ручка)")
    public Response cancelOrderByTrack(int track) {
        return getBaseSpec()
                .queryParam("track", track)
                .put("/api/v1/orders/cancel");
    }

    @Step("Получение track из ответа при создании заказа")
    public int getTrackFromResponse(Response response) {
        return response.jsonPath().getInt("track");
    }

    // Вспомогательный метод для повторных попыток
    private Response retry(RequestExecutor executor) {
        Exception lastException = null;
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                return executor.execute();
            } catch (Exception e) {
                lastException = e;
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException("Не удалось выполнить запрос после " + MAX_RETRIES + " попыток", lastException);
    }

    @FunctionalInterface
    private interface RequestExecutor {
        Response execute();
    }
}
//
