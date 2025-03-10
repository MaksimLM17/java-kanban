package ru.yandex.javacource.lemekhow.schedule.Server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.lemekhow.schedule.Exception.NotFoundException;
import ru.yandex.javacource.lemekhow.schedule.manager.TaskManager;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

public class HttpHistoryHandler extends BaseHttpHandler{

    public HttpHistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String [] path = exchange.getRequestURI()
                .getPath()
                .split("/");
        try {
            if ((path.length == 2) && (path[1].equals("history")) && (requestMethod.equals("GET"))) {
                getHistory(exchange);
            } else {
                writeResponse(exchange, "Обработка данного метода невозможна, переданный метод " +
                        exchange.getRequestMethod(), HttpURLConnection.HTTP_BAD_METHOD);
            }
        } catch (Exception e) {
            String responseError = e.getMessage();
            System.out.println(responseError);
            writeResponse(exchange, responseError, HttpURLConnection.HTTP_INTERNAL_ERROR);
        } finally {
            exchange.close();
        }

    }

    private void getHistory(HttpExchange exchange) throws IOException {
        try {
            List<Task> history = manager.getHistory();
            String response = gson.toJson(history);
            writeResponse(exchange, response, HttpURLConnection.HTTP_OK);
        } catch (NotFoundException e) {
            writeResponse(exchange, "История задач пуста!", HttpURLConnection.HTTP_NOT_FOUND);
        }
    }
}
