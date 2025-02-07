package ru.yandex.javacource.lemekhow.schedule;

import ru.yandex.javacource.lemekhow.schedule.manager.FileBackedTaskManager;
import ru.yandex.javacource.lemekhow.schedule.manager.Managers;
import ru.yandex.javacource.lemekhow.schedule.task.Epic;
import ru.yandex.javacource.lemekhow.schedule.task.Status;
import ru.yandex.javacource.lemekhow.schedule.task.Subtask;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.io.File;
import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
       File file = new File("data.csv");
        FileBackedTaskManager fbtm = (FileBackedTaskManager) Managers.getDefaultFileManager(file);
        Task task = new Task("Task", "Write file", Status.NEW);
        fbtm.createTask(task);
        Epic epic = new Epic("Epic", "Clean home", Status.NEW);
        fbtm.createEpic(epic);
        Subtask subtask = new Subtask(epic.getId(), "Subtask", "make homework", Status.DONE);
        Subtask subtask1 = new Subtask(epic.getId(), "sub", "desc", Status.IN_PROGRESS);
        fbtm.createSubtask(subtask1);
        fbtm.createSubtask(subtask);
    }
}
