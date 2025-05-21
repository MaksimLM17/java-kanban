package ru.yandex.javacource.lemekhow.schedule;

import ru.yandex.javacource.lemekhow.schedule.Server.HttpTaskServer;
import ru.yandex.javacource.lemekhow.schedule.manager.Managers;
import ru.yandex.javacource.lemekhow.schedule.manager.TaskManager;
import ru.yandex.javacource.lemekhow.schedule.task.Epic;
import ru.yandex.javacource.lemekhow.schedule.task.Status;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;


public class Main {
   public static void main(String[] args) throws IOException {
       TaskManager manager = Managers.getDefault();
      manager.createTask(new Task("name", "fff", Status.NEW,
               LocalDateTime.of(2014,8, 15,14, 20),
               Duration.ofMinutes(356)));
       manager.createEpic(new Epic("Epic", "Cleaning", Status.NEW,
               LocalDateTime.of(2025, 2, 21, 9, 53), Duration.ofMinutes(50)));
       manager.getTaskId(1);
       manager.getEpicId(2);

           HttpTaskServer server = new HttpTaskServer(manager);
           server.startServer();

       }

    }

