package praktikum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import praktikum.client.ScooterClient;
import praktikum.models.Courier;

public class CourierSteps extends ScooterClient {

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    @Step("Создание курьера: логин = {courier.login}, пароль = {courier.password}, имя = {courier.firstName}")
    public Response createCourier(Courier courier) {
        return retry(() -> getBaseSpec().body(courier).post(COURIER_PATH));
    }

    @Step("Логин курьера: логин = {courier.login}, пароль = {courier.password}")
    public Response loginCourier(Courier courier) {
        return retry(() -> getBaseSpec().body(courier).post(COURIER_LOGIN_PATH));
    }

    @Step("Удаление курьера с id = {courierId}")
    public Response deleteCourier(int courierId) {
        return getBaseSpec()
                .pathParam("id", courierId)
                .delete("/api/v1/courier/{id}");
    }

    @Step("Получение id курьера из ответа")
    public int getCourierId(Response response) {
        return response.jsonPath().getInt("id");
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
