package ru.yandex.javacource.lemekhow.schedule.Server.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.lemekhow.schedule.Exception.ManagerSaveException;
import ru.yandex.javacource.lemekhow.schedule.Exception.NotFoundException;
import ru.yandex.javacource.lemekhow.schedule.manager.TaskManager;
import ru.yandex.javacource.lemekhow.schedule.task.Epic;
import ru.yandex.javacource.lemekhow.schedule.task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class HttpEpicHandler extends BaseHttpHandler {

    public HttpEpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        try {
            switch (endpoint) {
                case GET_EPICS: {
                    getEpics(exchange);
                    break;
                }
                case GET_EPICID: {
                    getEpicId(exchange);
                    break;
                }
                case GET_SUBTASKS_EPICID: {
                    getSubtasksByEpicId(exchange);
                    break;
                }
                case POST_EPIC: {
                    createEpic(exchange);
                    break;
                }
                case POST_EPICID: {
                    updateEpic(exchange);
                    break;
                }
                case DELETE_EPICS: {
                    deleteEpicId(exchange);
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
                if (Pattern.matches("^/epics/\\d+/subtasks$", requestPath)) {
                    return Endpoint.GET_SUBTASKS_EPICID;
                } else if (Pattern.matches(("^/epics/\\d+$"), requestPath)) {
                    return Endpoint.GET_EPICID;
                } else {
                    return Endpoint.GET_EPICS;
                }
            }
            case "POST":
                if (Pattern.matches("^/epics$", requestPath)) {
                    return Endpoint.POST_EPIC;
                } else {
                    return Endpoint.POST_EPICID;
                }

            case "DELETE":
                return Endpoint.DELETE_EPICS;
            default:
                return Endpoint.UNKNOWN;
        }
    }

    private void getEpics(HttpExchange exchange) throws IOException {
        try {
            List<Epic> epics = manager.getEpics();
            String response = gson.toJson(epics);
            writeResponse(exchange, response, HttpURLConnection.HTTP_OK);
        } catch (NotFoundException e) {
            writeResponse(exchange,"Невозможно получить доступ к пустой хеш-таблице эпиков.",
                    HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void getEpicId(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdHandler(exchange);
        if (id.isPresent()) {
           Epic epic = manager.getEpicId(id.get());
            try {
                String response = gson.toJson(epic);
                writeResponse(exchange, response, HttpURLConnection.HTTP_OK);
            } catch (NotFoundException e) {
                writeResponse(exchange, "Эпик с id: " + id + " не найден", HttpURLConnection.HTTP_NOT_FOUND);
            }
        } else {
            writeResponse(exchange, "Получен некорректный id эпика", HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void createEpic(HttpExchange exchange) throws IOException {
        if (!checkHeader(exchange)) {
            writeResponse(exchange, "Передан запрос в неверном формате, ожидаемый формат JSON",
                    HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }
        try (InputStream is = exchange.getRequestBody()) {
            Optional<Epic> epic = parseEpic(is);
            if (epic.isPresent()) {
                try {
                    manager.createEpic(epic.get());
                    writeResponse(exchange, "Эпик успешно добавлен", HttpURLConnection.HTTP_CREATED);
                } catch (ManagerSaveException e) {
                    writeResponse(exchange, "Переданы некорректные значения для добавления эпика",
                            HttpURLConnection.HTTP_BAD_REQUEST);
                }
            } else {
                writeResponse(exchange, "Получен пустой запрос для создания эпика",
                        HttpURLConnection.HTTP_BAD_REQUEST);
            }
        }
    }

    private void updateEpic(HttpExchange exchange) throws IOException {
        if (!checkHeader(exchange)) {
            writeResponse(exchange, "Передан запрос в неверном формате, ожидаемый формат JSON",
                    HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }
        Optional<Integer> idOpt = getIdHandler(exchange);
        if (idOpt.isPresent()) {
            int id = idOpt.get();
            try (InputStream is = exchange.getRequestBody()) {
                Optional<Epic> epic = parseEpic(is);
                if (epic.isPresent()) {
                    try {
                        manager.updateEpic(epic.get());
                        writeResponse(exchange, "Эпик с id: " + id + " успешно обновлен",
                                HttpURLConnection.HTTP_CREATED);
                    } catch (NotFoundException e) {
                        writeResponse(exchange, "Эпик с id = " + id + " не найден",
                                HttpURLConnection.HTTP_NOT_FOUND);
                    }
                }
            }
        } else {
            writeResponse(exchange, "Получен некорректный id эпика", HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void deleteEpicId(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = getIdHandler(exchange);
        if (idOpt.isPresent()) {
            int id = idOpt.get();
            try {
                manager.removeEpicId(id);
                writeResponse(exchange, "Эпик с id: " + id + " удален",
                        HttpURLConnection.HTTP_OK);
            } catch (NotFoundException e) {
                writeResponse(exchange, "Эпик с id = " + id + " не найден",
                        HttpURLConnection.HTTP_NOT_FOUND);
            }
        } else {
            writeResponse(exchange, "Получен некорректный id эпика", HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void getSubtasksByEpicId(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = getIdHandler(exchange);
        if (idOpt.isPresent()) {
            int id = idOpt.get();
            try {
                List<Subtask> subtasks = manager.getSubtasksByEpic(id);
                String response = gson.toJson(subtasks);
                writeResponse(exchange, response, HttpURLConnection.HTTP_OK);
            } catch (NotFoundException e) {
                writeResponse(exchange, "По переданному id = " + id + " эпик не найден",
                        HttpURLConnection.HTTP_NOT_FOUND);
            }
        } else {
            writeResponse(exchange, "Получен некорректный id задачи", HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private Optional<Epic> parseEpic(InputStream inputStream) throws IOException {
        String body = new String(inputStream.readAllBytes(), DEFAULT);
        JsonElement jsonElement = JsonParser.parseString(body);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Epic epic = gson.fromJson(jsonObject, Epic.class);

        if (epic != null) {
            return Optional.of(epic);
        } else {
            return Optional.empty();
        }
    }

    enum Endpoint {
        GET_EPICS,
        GET_EPICID,
        GET_SUBTASKS_EPICID,
        POST_EPIC,
        POST_EPICID,
        DELETE_EPICS,
        UNKNOWN;
    }
}

