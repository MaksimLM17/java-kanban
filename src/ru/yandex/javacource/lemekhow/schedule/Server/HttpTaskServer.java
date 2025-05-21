package ru.yandex.javacource.lemekhow.schedule.Server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacource.lemekhow.schedule.Server.handler.*;
import ru.yandex.javacource.lemekhow.schedule.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer(TaskManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new HttpTaskHandler(manager));
        server.createContext("/epics", new HttpEpicHandler(manager));
        server.createContext("/subtasks", new HttpSubtaskHandler(manager));
        server.createContext("/history", new HttpHistoryHandler(manager));
        server.createContext("/prioritized", new HttpPriorityHandler(manager));
    }

    public void startServer() {
        System.out.println("Сервер стартовал на порту: " + PORT);
        server.start();
    }

    public void stopServer(int delay) {
        server.stop(delay);
    }


}
