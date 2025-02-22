package ru.yandex.javacource.lemekhow.schedule;

import ru.yandex.javacource.lemekhow.schedule.manager.Managers;
import ru.yandex.javacource.lemekhow.schedule.manager.TaskManager;
import ru.yandex.javacource.lemekhow.schedule.task.Status;
import ru.yandex.javacource.lemekhow.schedule.task.Task;
import java.time.Duration;
import java.time.LocalDateTime;


public class Main {
    public static void main(String[] args)  {
        TaskManager manager = Managers.getDefault();
        manager.createTask(new Task("name", "fff", 2, Status.NEW,
                        LocalDateTime.of(2014,8, 15,14, 20),
                        Duration.ofMinutes(356)));
        manager.updateTask(new Task("new Name", "dddd",1, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(390)));
        System.out.println(manager.getPrioritizedTasks());
        System.out.println("--------------------------");
        System.out.println(manager.getTasks());
    }
}
