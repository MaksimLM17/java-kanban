package ru.yandex.javacource.lemekhow.schedule.manager;

import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
   private final List<Task> history = new ArrayList<>();
   private final static int MAX_SIZE = 10;

    @Override
    public void add(Task task) {
        if(task == null) {
            return;
        }
        if (history.size() == MAX_SIZE) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }


}
