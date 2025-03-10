package ru.yandex.javacource.lemekhow.schedule.Server.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;

import ru.yandex.javacource.lemekhow.schedule.Exception.InvalidTimeException;
import ru.yandex.javacource.lemekhow.schedule.Exception.NotFoundEpicException;
import ru.yandex.javacource.lemekhow.schedule.Exception.NotFoundException;
import ru.yandex.javacource.lemekhow.schedule.manager.TaskManager;
import ru.yandex.javacource.lemekhow.schedule.task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class HttpSubtaskHandler extends BaseHttpHandler {

    public HttpSubtaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        try {
            switch (endpoint) {
                case GET_SUBTASKS: {
                    getSubtasks(exchange);
                    break;
                }
                case GET_SUBTASKID: {
                    getSubtaskId(exchange);
                    break;
                }
                case POST_SUBTASK: {
                    createSubtask(exchange);
                    break;
                }
                case POST_SUBTASKID: {
                    updateSubtask(exchange);
                    break;
                }
                case DELETE_SUBTASKS: {
                    deleteSubtaskId(exchange);
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
              if (Pattern.matches(("^/subtasks/\\d+$"), requestPath)) {
                    return Endpoint.GET_SUBTASKID;
                } else {
                    return Endpoint.GET_SUBTASKS;
                }
            }
            case "POST":
                if (Pattern.matches("^/subtasks$", requestPath)) {
                    return Endpoint.POST_SUBTASK;
                } else {
                    return Endpoint.POST_SUBTASKID;
                }

            case "DELETE":
                return Endpoint.DELETE_SUBTASKS;
            default:
                return Endpoint.UNKNOWN;
        }
    }

    private void getSubtasks(HttpExchange exchange) throws IOException {
        try {
            List<Subtask> subtasks = manager.getSubtasks();
            String response = gson.toJson(subtasks);
            writeResponse(exchange, response, HttpURLConnection.HTTP_OK);
        } catch (NotFoundException e) {
            writeResponse(exchange,"По данному запросу подзадачи не найдены," +
                            " добавьте подзадачи в менеджер или изменить запрос",
                    HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void getSubtaskId(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdHandler(exchange);
        if (id.isPresent()) {
            Subtask subtask = manager.getSubtaskId(id.get());
            try {
                String response = gson.toJson(subtask);
                writeResponse(exchange, response, HttpURLConnection.HTTP_OK);
            } catch (NotFoundException e) {
                writeResponse(exchange, "Подзадача с id: " + id + " не найдена", HttpURLConnection.HTTP_NOT_FOUND);
            }
        } else {
            writeResponse(exchange, "Получен некорректный id подзадачи", HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void createSubtask(HttpExchange exchange) throws IOException {
        if (!checkHeader(exchange)) {
            writeResponse(exchange, "Передан запрос в неверном формате, ожидаемый формат JSON",
                    HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }
        try (InputStream is = exchange.getRequestBody()) {
            Optional<Subtask> subtask = parseSubtask(is);
            if (subtask.isPresent()) {
                try {
                    manager.createSubtask(subtask.get());
                    writeResponse(exchange, "Подзадача успешно добавлен", HttpURLConnection.HTTP_CREATED);
                } catch (InvalidTimeException e) {
                    writeResponse(exchange, "Пересечение задач по времени выполнения, невозможно добавить подзадачу",
                            HttpURLConnection.HTTP_NOT_ACCEPTABLE);
                } catch (NotFoundEpicException e) {
                    writeResponse(exchange, "Передан несуществующий эпик, невозможно создать подзадачу вне эпика",
                            HttpURLConnection.HTTP_NOT_FOUND);
                }
            } else {
                writeResponse(exchange, "Получен пустой запрос для создания подзадачи",
                        HttpURLConnection.HTTP_BAD_REQUEST);
            }
        }
    }

    private void updateSubtask(HttpExchange exchange) throws IOException {
        if (!checkHeader(exchange)) {
            writeResponse(exchange, "Передан запрос в неверном формате, ожидаемый формат JSON",
                    HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }
        Optional<Integer> idOpt = getIdHandler(exchange);
        if (idOpt.isPresent()) {
            int id = idOpt.get();
            try (InputStream is = exchange.getRequestBody()) {
                Optional<Subtask> subtask = parseSubtask(is);
                if (subtask.isPresent()) {
                    try {
                        manager.updateSubtask(subtask.get());
                        writeResponse(exchange, "Подзадача с id: " + id + " успешно обновлена",
                                HttpURLConnection.HTTP_CREATED);
                    } catch (NotFoundException e) {
                        writeResponse(exchange, "Подзадача с id = " + id + " не найдена",
                                HttpURLConnection.HTTP_NOT_FOUND);
                    } catch (InvalidTimeException e) {
                        writeResponse(exchange,
                                "Пересечение задач по времени выполнения, невозможно обновить подзадачу",
                                HttpURLConnection.HTTP_NOT_ACCEPTABLE);
                    } catch (NotFoundEpicException e) {
                        writeResponse(exchange, "Передан несуществующий эпик, невозможно обновить подзадачу вне эпика",
                                HttpURLConnection.HTTP_NOT_FOUND);
                    }
                } else {
                    writeResponse(exchange, "Получен пустой запрос для обновления подзадачи",
                            HttpURLConnection.HTTP_BAD_REQUEST);
                }
            }
        } else {
            writeResponse(exchange, "Получен некорректный id подзадачи", HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void deleteSubtaskId(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = getIdHandler(exchange);
        if (idOpt.isPresent()) {
            int id = idOpt.get();
            try {
                manager.removeSubtaskId(id);
                writeResponse(exchange, "Подзадача с id: " + id + " удалена.",
                        HttpURLConnection.HTTP_OK);
            } catch (NotFoundException e) {
                writeResponse(exchange, "Подзадача с id = " + id + " не найдена!",
                        HttpURLConnection.HTTP_NOT_FOUND);
            }
        } else {
            writeResponse(exchange, "Получен некорректный id подзадачи", HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private Optional<Subtask> parseSubtask(InputStream inputStream) throws IOException {
        String body = new String(inputStream.readAllBytes(), DEFAULT);
        JsonElement jsonElement = JsonParser.parseString(body);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Subtask subtask = gson.fromJson(jsonObject, Subtask.class);

        if (subtask != null) {
            return Optional.of(subtask);
        } else {
            return Optional.empty();
        }
    }

    enum Endpoint {
        GET_SUBTASKS,
        GET_SUBTASKID,
        POST_SUBTASK,
        POST_SUBTASKID,
        DELETE_SUBTASKS,
        UNKNOWN;
    }
}

