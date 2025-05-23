package ru.yandex.javacource.lemekhow.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.lemekhow.schedule.task.Epic;
import ru.yandex.javacource.lemekhow.schedule.task.Status;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private TaskManager taskManager;
    private Task task;
    private Epic epic;


    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        task = new Task("Task", "clean window", 1, Status.NEW);
        epic = new Epic("Epic", "Going", 2, Status.NEW);
    }

    @Test
    void add() {
        int id = taskManager.createTask(task);
        taskManager.getTaskId(id);
        List<Task> historyTask = taskManager.getHistory();

        assertNotNull(historyTask,"История  пустая");
        assertEquals(1, historyTask.size(),"История  пустая");
    }

    @Test
    void valuesHistoryTasksShouldNotBeUpdateAfterUpdateTask() {
        int id = taskManager.createTask(task);
        taskManager.getTaskId(id);
        Task updateTask = new Task("upTask", "upDescription",id,  Status.DONE);
        taskManager.updateTask(updateTask);

        List<Task> history = taskManager.getHistory();
        Task historyTask = history.get(0);
        String name = historyTask.getName();
        String description = historyTask.getDescription();
        int idTask = historyTask.getId();
        Status status = historyTask.getStatus();

        assertEquals(name, task.getName(), "Имена не равны");
        assertEquals(description, task.getDescription(), "Описания не равны");
        assertEquals(idTask, task.getId(), "Id не равны");
        assertEquals(status, task.getStatus(), "Статусы не равны");
    }

    @Test
    void valuesHistoryTasksShouldNotBeUpdateAfterUpdateEpic() {
        int id = taskManager.createEpic(epic);
        taskManager.getEpicId(id);
        Epic updateEpic = new Epic("upEpic", "upDescription",id,  Status.NEW);
        taskManager.updateEpic(updateEpic);

        List<Task> history = taskManager.getHistory();
        Task historyTask = history.get(0);
        String name = historyTask.getName();
        String description = historyTask.getDescription();
        int idTask = historyTask.getId();
        Status status = historyTask.getStatus();

        assertEquals(name, epic.getName(), "Имена не равны");
        assertEquals(description, epic.getDescription(), "Описания не равны");
        assertEquals(idTask, epic.getId(), "Id не равны");
        assertEquals(status, epic.getStatus(), "Статусы не равны");
    }
}