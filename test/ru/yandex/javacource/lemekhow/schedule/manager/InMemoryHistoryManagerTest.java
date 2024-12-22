package ru.yandex.javacource.lemekhow.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.lemekhow.schedule.task.Epic;
import ru.yandex.javacource.lemekhow.schedule.task.Status;
import ru.yandex.javacource.lemekhow.schedule.task.Subtask;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private TaskManager taskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;
    private HistoryManager historyManager;



    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        task = new Task("Task", "clean window", 1, Status.NEW);
        epic = new Epic("Epic", "Going", 2, Status.NEW);
        subtask = new Subtask(2, "Subtask", "go to the park", 3, Status.IN_PROGRESS);

    }

    @Test
    void add() {
        taskManager.createTask(task);
        taskManager.getTaskId(task.getId());
        ArrayList<Task> historyTask = taskManager.getHistory();

        assertNotNull(historyTask,"История  пустая");
        assertEquals(1, historyTask.size(),"История  пустая");
    }

    @Test
    void valuesHistoryTasksShouldNotBeUpdateAfterUpdateTask() {
        taskManager.createTask(task);
        taskManager.getTaskId(task.getId());
        Task updateTask = new Task("upTask", "upDescription",1,  Status.DONE);
        taskManager.updateTask(updateTask);
        ArrayList<Task> history = taskManager.getHistory();

        assertEquals(history.get(0), task, "Объекты не равны");
    }

    @Test
    void sizeHistoryTasksShouldNotBeMoreTen() {
        int historyTasksSize = 10;

        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(epic);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.add(subtask);
        historyManager.add(subtask);
        historyManager.add(subtask);
        List<Task> historyTasksSizeTen = historyManager.getHistory();

        assertEquals(historyTasksSize, historyTasksSizeTen.size(), "Значения не равны");

        historyManager.add(task);
        historyManager.add(subtask);
        List<Task> historyTasksAfterOverflow = historyManager.getHistory();

        assertEquals(historyTasksSize, historyTasksAfterOverflow.size(), "Значения после переполнения не равны");
    }
}