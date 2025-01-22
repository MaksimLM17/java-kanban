package ru.yandex.javacource.lemekhow.schedule.manager;

import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);
    void remove(int id);

    List<Task> getHistory();
}
