package ru.yandex.javacource.lemekhow.schedule.Server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.lemekhow.schedule.Exception.NotFoundException;
import ru.yandex.javacource.lemekhow.schedule.manager.TaskManager;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Set;

public class HttpPriorityHandler extends BaseHttpHandler {

    public HttpPriorityHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String [] path = exchange.getRequestURI().getPath().split("/");
        try {
            if ((path.length == 2) && (path[1].equals("prioritized")) && (requestMethod.equals("GET"))) {
                getPrioritized(exchange);
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

    private void getPrioritized(HttpExchange exchange) throws IOException {
        try {
            Set<Task> priority = manager.getPrioritizedTasks();
            String response =  gson.toJson(priority);
            writeResponse(exchange, response, HttpURLConnection.HTTP_OK);
        } catch (NotFoundException e) {
            writeResponse(exchange, "Список задач по приоритету псут", HttpURLConnection.HTTP_NOT_FOUND);
        }
    }
}
