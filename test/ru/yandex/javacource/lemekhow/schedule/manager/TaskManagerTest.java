package ru.yandex.javacource.lemekhow.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.lemekhow.schedule.Exception.NotFoundException;
import ru.yandex.javacource.lemekhow.schedule.task.Epic;
import ru.yandex.javacource.lemekhow.schedule.task.Status;
import ru.yandex.javacource.lemekhow.schedule.task.Subtask;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

 abstract class TaskManagerTest<T extends TaskManager> {
    protected abstract TaskManager getTaskManager();

    protected TaskManager taskManager;
    protected Task task;
    protected Task task1;
    protected Epic epic;
    protected Epic epic1;


    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        task = new Task("Task", "clean window", Status.NEW,
                LocalDateTime.of(2025, 1, 20, 6, 40), Duration.ofMinutes(50));
        task1 = new Task("Task1", "clean", Status.IN_PROGRESS,
                LocalDateTime.of(2025, 3, 20, 8, 55), Duration.ofMinutes(300));
        epic = new Epic("Epic", "Cleaning", Status.NEW,
                LocalDateTime.of(2025, 4, 21, 9, 53), Duration.ofMinutes(50));
        epic1 = new Epic("Epic1", "Clean", Status.NEW,
                LocalDateTime.of(2025, 5, 21, 10, 59), Duration.ofMinutes(50));
    }

    @Test
    void addTask() {
        final int taskId = taskManager.createTask(task);
        Task savedTask = taskManager.getTaskId(taskId);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");
        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertNotNull(task.getId(), "id == null");
        assertEquals(1, tasks.size(), "Неверное количество задач" );
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают");
    }

    @Test
    void removeAllTask() {
        taskManager.createTask(task);
        taskManager.createTask(task1);
        List<Task> tasks = taskManager.getTasks();

        assertEquals(2, tasks.size(), "Значения не равны");

        taskManager.removeAllTask();
        List<Task> tasksAfterDelete = new ArrayList<>();
        try {
            tasksAfterDelete = taskManager.getTasks();
        } catch (NotFoundException ignored) {
        }

        assertTrue(tasksAfterDelete.isEmpty(), "Список не пуст");
    }
    @Test
    void updateTask() {
        int id = taskManager.createTask(task);
        Task taskUpdate = new Task("Task1", "clean", id, Status.NEW);

        taskManager.updateTask(taskUpdate);
        Task taskAfterUpdate = taskManager.getTaskId(id);

        assertEquals(taskUpdate, taskAfterUpdate, "Объекты не равны");
    }

    @Test
    void removeTaskId() {
        taskManager.createTask(task);
        taskManager.createTask(task1);
        taskManager.removeTaskId(task1.getId());
        List<Task> tasks = taskManager.getTasks();

        assertEquals(1, tasks.size(), "Значения не равны");
    }

    @Test
    void addEpic() {
        int id = taskManager.createEpic(epic);
        Epic saveEpic = taskManager.getEpicId(id);

        assertNotNull(saveEpic, "Эпик пуст");
        assertEquals(saveEpic, epic, "Эпики не равны");

        List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпик не найден");
        assertEquals(epics, taskManager.getEpics(), "Эпики не равны в списках");
        assertEquals(1, epics.size(), "Значения не совпадают");
        assertEquals(epic, epics.get(0), "Эпик созданный не равен сохраненному в список");
    }

    @Test
    void addEpicInHashMap() {
        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);
        List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "epics = null");
        assertEquals(2, epics.size(), "Значения не равны");
    }

    @Test
    void removeAllEpic() {
        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);
        Subtask subtask = new Subtask(epic.getId(), "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));
        Subtask subtask1 = new Subtask(epic.getId(), "Subtask1", "descrip", Status.DONE,
                LocalDateTime.of(2025, 2, 20, 14, 30), Duration.ofMinutes(190));

        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask1);
        List<Epic> epics = taskManager.getEpics();
        List<Integer> subtasksIds = epic.getSubtaskIds();

        assertNotNull(epics, "epics == null");
        assertEquals(2, subtasksIds.size(), "Значения не равны");

        taskManager.removeAllEpic();
        List<Epic> epicsAfterRemove = new ArrayList<>();
        try {
            epicsAfterRemove = taskManager.getEpics();
        } catch (NotFoundException ignored) {

        }

        List<Integer> subtasksIdsAfterRemove = epic.getSubtaskIds();

        assertTrue(epicsAfterRemove.isEmpty(), "Список не пуст");
        assertTrue(subtasksIdsAfterRemove.isEmpty(), "Список не пуст");
    }

    @Test
    void getEpicId() {
        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);
        Epic epicReceived = taskManager.getEpicId(epic.getId());
        Epic epic1Received = taskManager.getEpicId(epic1.getId());

        assertNotNull(epicReceived, "epicReceived == null");
        assertEquals(epic, epicReceived, "Объекты не равны");
        assertEquals(epic1.getId(), epic1Received.getId(), "id не равны");
    }

    @Test
    void updateEpic() {
        int id = taskManager.createEpic(epic);
        Epic epicUpdate = new Epic("Epic1", "Clean", id, Status.NEW);
        Subtask subtask = new Subtask(id, "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));
        Subtask subtask1 = new Subtask(id, "Subtask1", "descrip", Status.DONE,
                LocalDateTime.of(2025, 2, 20, 14, 30), Duration.ofMinutes(190));

        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask1);
        taskManager.updateEpic(epicUpdate);
        Epic epicAfterUpdate  = taskManager.getEpicId(epicUpdate.getId());
        List<Integer> subtaskIds = epicAfterUpdate.getSubtaskIds();

        assertEquals(epicUpdate.getName(), epicAfterUpdate.getName(), "Поле 'name' не одинаково" );
        assertEquals(epicUpdate.getId(), epicAfterUpdate.getId(), "id !=");
        assertEquals(2, subtaskIds.size());
    }

    @Test
    void removeEpicId() {
        int id = taskManager.createEpic(epic);
        int id1 = taskManager.createEpic(epic1);
        Subtask subtask = new Subtask(id, "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));
        Subtask subtask1 = new Subtask(id1, "Subtask1", "descrip", Status.DONE,
                LocalDateTime.of(2025, 2, 22, 14, 30), Duration.ofMinutes(190));

        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask1);
        List<Epic> epicsBeforeDelete = taskManager.getEpics();
        List<Subtask> subtasksBeforeDelete = taskManager.getSubtasks();

        assertFalse(epicsBeforeDelete.isEmpty(), "Список эпиков пуст");
        assertFalse(subtasksBeforeDelete.isEmpty(), "Список подзадач пуст");

        taskManager.removeEpicId(id);
        List<Epic> epicsAfterRemove = taskManager.getEpics();
        List<Subtask> subtaskAfterRemoveEpicId = taskManager.getSubtasks();
        int actualSize = 1;

        assertEquals(epicsAfterRemove.size(), actualSize, "Список эпиков не пуст");
        assertFalse(subtaskAfterRemoveEpicId.isEmpty(), "Список подзадач пуст");
    }

    @Test
    void updateStatusByEpic() {
        int id = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(id, "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));
        Subtask subtask1 = new Subtask(id, "Subtask1", "descrip", Status.DONE,
                LocalDateTime.of(2025, 2, 20, 14, 30), Duration.ofMinutes(190));

        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask1);

        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Статусы не равны");
    }

    @Test
    void addSubtask() {
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));

        int idSubtask = taskManager.createSubtask(subtask);
        Subtask createdSubtask = taskManager.getSubtaskId(idSubtask);

        assertNotNull(subtask.getEpicId(), "epicID == null");
        assertNotNull(subtask.getId(), "id == null");
        assertEquals(createdSubtask, subtask, "Сабтаски не равны");

        List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "epics == null");
    }

    @Test
    void addSubtaskInHashMap() {
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));

        taskManager.createSubtask(subtask);
        List<Subtask> subtasks = taskManager.getSubtasks();

        assertFalse(subtasks.isEmpty(), "Список пуст");
        assertEquals(1, subtasks.size(), "Значения не равны");
    }

    @Test
    void removeAllSubtasks() {
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));
        Subtask subtask1 = new Subtask(idEpic, "Subtask1", "descrip", Status.DONE,
                LocalDateTime.of(2025, 2, 20, 14, 30), Duration.ofMinutes(190));

        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask1);

        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Статусы не равны");

        taskManager.removeAllSubtask();
        List<Subtask> subtasksAfterRemove = new ArrayList<>();
        try {
            subtasksAfterRemove = taskManager.getSubtasks();
        } catch (NotFoundException ignored) {

        }

        List<Integer> subtasksIds = epic.getSubtaskIds();

        assertNotSame(epic.getStatus(), Status.IN_PROGRESS, "Статусы равны");
        assertTrue(subtasksAfterRemove.isEmpty(), "Список не пуст");
        assertTrue(subtasksIds.isEmpty(), "Список не пуст");
    }

    @Test
    void getSubtaskId(){
        int idEpic =  taskManager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));

        int idSubtask = taskManager.createSubtask(subtask);
        Subtask subtaskGetId = taskManager.getSubtaskId(idSubtask);

        assertNotNull(subtaskGetId, "subtaskGetId == null");
        assertEquals(subtaskGetId, subtask, "Объекты не равны");
    }

    @Test
    void updateSubtask() {
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));
        int idSubtask = taskManager.createSubtask(subtask);
        Subtask subtask1 = new Subtask(idEpic, "Subtask1", "descrip", idSubtask, Status.DONE,
                LocalDateTime.of(2025, 2, 20, 14, 30), Duration.ofMinutes(190));

        assertSame(epic.getStatus(), Status.IN_PROGRESS, "Статусы не равны");

        taskManager.updateSubtask(subtask1);
        List<Subtask> subtasks = taskManager.getSubtasks();
        Subtask subtaskAfterUpdateSubtask = subtasks.get(0);

        assertEquals(subtask1, subtaskAfterUpdateSubtask, "Объекты не равны");
        assertNotSame(epic.getStatus(), Status.NEW, "Статусы равны");
    }

    @Test
    void removeSubtaskId() {
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "Subtask1", "descrip", Status.DONE,
                LocalDateTime.of(2025, 2, 20, 14, 30), Duration.ofMinutes(190));
        Subtask subtask1 = new Subtask(idEpic, "Subtasks", "descrip", Status.NEW,
                LocalDateTime.of(2025, 2, 22, 18, 30), Duration.ofMinutes(10));

        int idSubtask = taskManager.createSubtask(subtask);

        assertSame(epic.getStatus(), Status.DONE, "Статусы равны");

        int idSubtask1 = taskManager.createSubtask(subtask1);
        taskManager.removeSubtaskId(idSubtask);
        List<Subtask> subtasks = taskManager.getSubtasks();
        int actualSize = 1;

        assertEquals(subtasks.size(), actualSize, "Список не пуст");
        assertNotSame(epic.getStatus(), Status.DONE, "Статусы не равны");

    }


    @Test
    void getSubtaskIdByEpic() {
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));
        Subtask subtask1 = new Subtask(idEpic, "Subtask1", "descrip", Status.DONE,
                LocalDateTime.of(2025, 2, 20, 14, 30), Duration.ofMinutes(190));

        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask1);
        List<Subtask> subtasksByEpic = taskManager.getSubtasksByEpic(idEpic);

        assertEquals(2, subtasksByEpic.size(), "Значение неверное");
    }

    @Test
    void inEpicSubtasksIdShouldNotBeUnnecessarySubtaskIdAfterRemoveSubtaskId() {
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(epic.getId(), "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));
        Subtask subtask1 = new Subtask(epic.getId(), "Subtask1", "descrip", Status.DONE,
                LocalDateTime.of(2025, 2, 20, 14, 30), Duration.ofMinutes(190));

        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask1);
        taskManager.removeSubtaskId(subtask.getId());
        List<Integer> subtasksId = epic.getSubtaskIds();
        int idSubtask = subtask1.getId();

        assertEquals(1, subtasksId.size(), "Неверное количество");
        assertEquals(idSubtask, subtasksId.get(0), "Значения не равны");
    }

    @Test
    void getTaskIdShouldNotHaveAccessToChange() {
        taskManager.createTask(task);

        Task changeableTask = taskManager.getTaskId(task.getId());
        changeableTask.setStatus(Status.IN_PROGRESS);
        changeableTask.setDescription("Do nothing");

        Task task1 = taskManager.getTaskId(task.getId());
        assertNotEquals(changeableTask.getDescription(), task1.getDescription(), "Значения равны");
        assertNotEquals(changeableTask.getStatus(), task1.getStatus(), "Значения равны");
    }

    @Test
    void getPrioritizedTasks() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask(epic.getId(), "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185)));
        taskManager.createTask(task1);
        int sizeExpected = 3;

        assertEquals(taskManager.getPrioritizedTasks().size(), sizeExpected, "Списки не равны");
    }

    @Test
    void getHistory() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        int idSubtask = taskManager.createSubtask(new Subtask(epic.getId(), "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185)));
        taskManager.createTask(task1);
        int sizeExpected = 4;

        taskManager.getTaskId(task.getId());
        taskManager.getTaskId(task1.getId());
        taskManager.getEpicId(epic.getId());
        taskManager.getSubtaskId(idSubtask);

        assertEquals(taskManager.getHistory().size(), sizeExpected, "Значения не равны");
    }
}





