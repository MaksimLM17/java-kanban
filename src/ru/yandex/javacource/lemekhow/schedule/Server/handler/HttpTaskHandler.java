package ru.yandex.javacource.lemekhow.schedule.Server.handler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.lemekhow.schedule.Exception.InvalidTimeException;
import ru.yandex.javacource.lemekhow.schedule.Exception.NotFoundException;
import ru.yandex.javacource.lemekhow.schedule.manager.TaskManager;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class HttpTaskHandler extends BaseHttpHandler {

    public HttpTaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        try {
            switch (endpoint) {
                case GET_TASKS: {
                    getTasks(exchange);
                    break;
                }
                case GET_TASKID: {
                    getTaskId(exchange);
                    break;
                }
                case POST_TASK: {
                    createTask(exchange);
                    break;
                }
                case POST_TASKID: {
                    updateTask(exchange);
                    break;
                }
                case DELETE_TASKS: {
                    deleteTaskId(exchange);
                    break;
                }
                default: {
                    writeResponse(exchange, "Обработка данного метода невозможна, переданный метод " +
                            exchange.getRequestMethod(), HttpURLConnection.HTTP_BAD_METHOD);
                }
            }
        } catch (Exception e) {
            String responseError = e.getMessage();
            System.out.println(responseError);
            writeResponse(exchange, responseError, HttpURLConnection.HTTP_INTERNAL_ERROR);
        } finally {
            exchange.close();
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        switch (requestMethod) {
            case "GET": {
                if (Pattern.matches("^/tasks$", requestPath)) {
                    return Endpoint.GET_TASKS;
                } else {
                    return Endpoint.GET_TASKID;
                }
            }
            case "POST":
                if (Pattern.matches("^/tasks$", requestPath)) {
                    return Endpoint.POST_TASK;
                } else {
                    return Endpoint.POST_TASKID;
                }

            case "DELETE":
                return Endpoint.DELETE_TASKS;
            default:
                return Endpoint.UNKNOWN;
        }
    }

    private void getTasks(HttpExchange exchange) throws IOException {
        try {
            List<Task> tasks = manager.getTasks();
            String response = gson.toJson(tasks);
            writeResponse(exchange, response, HttpURLConnection.HTTP_OK);
        } catch (NotFoundException e) {
            writeResponse(exchange,"Невозможно получить доступ к пустой хеш-таблице задач.",
                    HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void getTaskId(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdHandler(exchange);
        if (id.isPresent()) {
            Task task = manager.getTaskId(id.get());
            try {
                String response = gson.toJson(task);
                writeResponse(exchange, response, HttpURLConnection.HTTP_OK);
            } catch (NotFoundException e) {
                writeResponse(exchange, "Задача с id: " + id + " не найдена", HttpURLConnection.HTTP_NOT_FOUND);
            }
        } else {
            writeResponse(exchange, "Получен некорректный id задачи", HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void createTask(HttpExchange exchange) throws IOException {
        if (!checkHeader(exchange)) {
            writeResponse(exchange, "Передан запрос в неверном формате, ожидаемый формат JSON",
                    HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }
        try (InputStream is = exchange.getRequestBody()) {
            Optional<Task> task = parseTask(is);
            if (task.isPresent()) {
                try {
                    manager.createTask(task.get());
                    writeResponse(exchange, "Задача успешно добавлена", HttpURLConnection.HTTP_CREATED);
                } catch (InvalidTimeException e) {
                    writeResponse(exchange, "Пересечение задач по времени выполнения, невозможно добавить задачу",
                            HttpURLConnection.HTTP_NOT_ACCEPTABLE);
                }
            } else {
                writeResponse(exchange, "Получен пустой запрос для создания задачи", HttpURLConnection.HTTP_BAD_REQUEST);
            }
        }
    }

    private void updateTask(HttpExchange exchange) throws IOException {
        if (!checkHeader(exchange)) {
            writeResponse(exchange, "Передан запрос в неверном формате, ожидаемый формат JSON",
                    HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }
        Optional<Integer> idOpt = getIdHandler(exchange);
        if (idOpt.isPresent()) {
            int id = idOpt.get();
            try (InputStream is = exchange.getRequestBody()) {
                Optional<Task> task = parseTask(is);
                if (task.isPresent()) {
                    try {
                        manager.updateTask(task.get());
                        writeResponse(exchange, "Задача с id: " + id + " успешно обновлена",
                                HttpURLConnection.HTTP_CREATED);
                    } catch (NotFoundException e) {
                        writeResponse(exchange, "Задача с id = " + id + " не найдена",
                                HttpURLConnection.HTTP_NOT_FOUND);
                    } catch (InvalidTimeException e) {
                        writeResponse(exchange,
                                "Пересечение задач по времени выполнения, невозможно обновить задачу",
                                HttpURLConnection.HTTP_NOT_ACCEPTABLE);
                    }
                }
            }
        } else {
            writeResponse(exchange, "Получен некорректный id задачи", HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void deleteTaskId(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = getIdHandler(exchange);
        if (idOpt.isPresent()) {
            int id = idOpt.get();
            try {
                manager.removeTaskId(id);
                writeResponse(exchange, "Задача с id: " + id + " удалена",
                            HttpURLConnection.HTTP_OK);
            } catch (NotFoundException e) {
                writeResponse(exchange, "Задача с id = " + id + " не найдена",
                        HttpURLConnection.HTTP_NOT_FOUND);
            }
        } else {
            writeResponse(exchange, "Получен некорректный id задачи", HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private Optional<Task> parseTask(InputStream inputStream) throws IOException {
        String body = new String(inputStream.readAllBytes(), DEFAULT);
        JsonElement jsonElement = JsonParser.parseString(body);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Task task = gson.fromJson(jsonObject, Task.class);

        if (task != null) {
            return Optional.of(task);
        } else {
            return Optional.empty();
        }
    }

    enum Endpoint {
        GET_TASKS,
        GET_TASKID,
        POST_TASK,
        POST_TASKID,
        DELETE_TASKS,
        UNKNOWN;
    }
}

