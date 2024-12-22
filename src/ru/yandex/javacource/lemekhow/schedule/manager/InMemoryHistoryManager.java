package ru.yandex.javacource.lemekhow.schedule.manager;

import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
   private  ArrayList<Task> historyTasks = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (historyTasks.size() == 10) {
            historyTasks.removeFirst();
        }
        historyTasks.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyTasks;
    }


}
