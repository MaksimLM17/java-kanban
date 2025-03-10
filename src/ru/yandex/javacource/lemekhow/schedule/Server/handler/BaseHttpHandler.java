package ru.yandex.javacource.lemekhow.schedule.Server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.lemekhow.schedule.manager.Managers;
import ru.yandex.javacource.lemekhow.schedule.manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final Charset DEFAULT = StandardCharsets.UTF_8;
    protected Gson gson;
    protected TaskManager manager;

    protected BaseHttpHandler(TaskManager manager) {
        this.gson = Managers.getDefaultGson();
        this.manager = manager;
    }

    protected void writeResponse(HttpExchange exchange,
                                 String textResponse,
                                 int codeResponse) throws IOException {
        byte [] responseBytes = textResponse.getBytes(DEFAULT);
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(codeResponse, responseBytes.length);
            os.write(textResponse.getBytes(DEFAULT));
        }
        exchange.close();
    }

    protected Optional<Integer> getIdHandler(HttpExchange exchange) {
        String [] path = exchange.getRequestURI().getPath().split("/");
        try {
            int id = parseInt(path[2]);
            if (id == -1) {
            return Optional.empty();
            }
            return Optional.of(parseInt(path[2]));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    protected boolean checkHeader(HttpExchange exchange) {
        Headers headers = exchange.getRequestHeaders();
        List<String> contentTypeValues = headers.get("Content-type");
        return (contentTypeValues != null) && (contentTypeValues.contains("application/json"));
    }


    protected int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
