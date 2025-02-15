package ru.yandex.javacource.lemekhow.schedule;

import ru.yandex.javacource.lemekhow.schedule.manager.FileBackedTaskManager;
import ru.yandex.javacource.lemekhow.schedule.task.Epic;
import ru.yandex.javacource.lemekhow.schedule.task.Subtask;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.io.File;
import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
       File file = new File("data.csv");
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        for (Task tasks : taskManager.getTasks()) {
            System.out.println(tasks);
        }
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic);
        }
        for (Subtask subtask2 : taskManager.getSubtasks()) {
            System.out.println(subtask2);
        }
    }
}
