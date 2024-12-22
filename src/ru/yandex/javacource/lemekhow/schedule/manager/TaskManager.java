package ru.yandex.javacource.lemekhow.schedule.manager;

import ru.yandex.javacource.lemekhow.schedule.task.Epic;
import ru.yandex.javacource.lemekhow.schedule.task.Status;
import ru.yandex.javacource.lemekhow.schedule.task.Subtask;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.util.ArrayList;

public interface TaskManager {

    ArrayList<Task> getHistory();

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasksByEpic(int epicId);

    ArrayList<Subtask> getSubtasks();

    void removeAllTask();

    void removeAllEpic();

    void removeAllSubtask();

    Task getTaskId(int taskId);

    Epic getEpicId(int epicId);

    Subtask getSubtaskId(int subtaskId);

    int createTask(Task task);

    int createEpic(Epic epic);

    Integer createSubtask(Subtask subtask);

    void updateTask(Task upTask);

    void updateEpic(Epic upEpic);

    void updateSubtask(Subtask upSubtask);

    Task removeTaskId(int taskId);

    Epic removeEpicId(int epicId);

    Subtask removeSubtaskId(int subtaskId);

    Integer generateId();

    void updateEpicStatus(int epicId);
}
