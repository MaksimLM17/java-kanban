package ru.yandex.javacource.lemekhow.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.lemekhow.schedule.task.Epic;
import ru.yandex.javacource.lemekhow.schedule.task.Status;
import ru.yandex.javacource.lemekhow.schedule.task.Subtask;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }


    @Test
    void addTask() {
        Task task = new Task("Task", "clean window", Status.NEW);

        final int taskId = taskManager.createTask(task);
        Task savedTask = taskManager.getTaskId(taskId);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");
        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertNotNull(task.getId(), "id == null");
        assertEquals(1, tasks.size(), "Неверное количество задач" );
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void removeAllTask() {
        Task task = new Task("Task", "clean window", Status.NEW);
        Task task1 = new Task("Task1", "clean", Status.NEW);

        taskManager.createTask(task);
        taskManager.createTask(task1);
        List<Task> tasks = taskManager.getTasks();

        assertEquals(2, tasks.size(), "Значения не равны");

        taskManager.removeAllTask();
        List<Task> tasksAfterDelete = taskManager.getTasks();

        assertTrue(tasksAfterDelete.isEmpty(), "Список не пуст");
    }

    @Test
    void updateTask() {
        Task task = new Task("Task", "clean window", Status.NEW);
        int id = taskManager.createTask(task);
        Task taskUpdate = new Task("Task1", "clean", id, Status.NEW);

        taskManager.updateTask(taskUpdate);
        Task taskAfterUpdate = taskManager.getTaskId(id);

        assertEquals(taskUpdate, taskAfterUpdate, "Объекты не равны");
    }

    @Test
    void removeTaskId() {
        Task task = new Task("Task", "clean window", Status.NEW);
        Task task1 = new Task("Task1", "clean", Status.NEW);

        taskManager.createTask(task);
        taskManager.createTask(task1);
        taskManager.removeTaskId(task1.getId());
        List<Task> tasks = taskManager.getTasks();

        assertEquals(1, tasks.size(), "Значения не равны");
    }

    @Test
    void addEpic() {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);

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
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        Epic epic1 = new Epic("Epic1", "Clean", Status.NEW);

        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);
        List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "epics = null");
        assertEquals(2, epics.size(), "Значения не равны");
    }

    @Test
    void removeAllEpic() {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        Epic epic1 = new Epic("Epic1", "Clean", Status.NEW);

        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);
        Subtask subtask = new Subtask(epic.getId(), "Subtask", "description", Status.NEW);
        Subtask subtask1 = new Subtask(epic.getId(), "Subtask1", "descrip", Status.NEW);

        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask1);
        List<Epic> epics = taskManager.getEpics();
        List<Integer> subtasksIds = epic.getSubtaskIds();

        assertNotNull(epics, "epics == null");
        assertEquals(2, subtasksIds.size(), "Значения не равны");

        taskManager.removeAllEpic();
        List<Epic> epicsAfterRemove = taskManager.getEpics();
        List<Integer> subtasksIdsAfterRemove = epic.getSubtaskIds();

        assertTrue(epicsAfterRemove.isEmpty(), "Список не пуст");
        assertTrue(subtasksIdsAfterRemove.isEmpty(), "Список не пуст");

    }

    @Test
    void getEpicId() {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        Epic epic1 = new Epic("Epic1", "Clean", Status.NEW);

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
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        int id = taskManager.createEpic(epic);
        Epic epicUpdate = new Epic("Epic1", "Clean", id, Status.NEW);
        Subtask subtask = new Subtask(id, "Subtask", "description", Status.NEW);
        Subtask subtask1 = new Subtask(id, "Subtask1", "descrip", Status.DONE);

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
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        int id = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(id, "Subtask", "description", Status.NEW);
        Subtask subtask1 = new Subtask(id, "Subtask1", "descrip", Status.DONE);

        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask1);
        List<Epic> epicsBeforeDelete = taskManager.getEpics();
        List<Subtask> subtasksBeforeDelete = taskManager.getSubtasks();

        assertFalse(epicsBeforeDelete.isEmpty(), "Список эпиков пуст");
        assertFalse(subtasksBeforeDelete.isEmpty(), "Список подзадач пуст");

        taskManager.removeEpicId(id);
        List<Epic> epicsAfterRemove = taskManager.getEpics();
        List<Subtask> subtaskAfterRemoveEpicId = taskManager.getSubtasks();

        assertTrue(epicsAfterRemove.isEmpty(), "Список эпиков не пуст");
        assertTrue(subtaskAfterRemoveEpicId.isEmpty(), "Список подзадач не пуст");
    }

    @Test
    void updateStatusByEpic() {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        int id = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(id, "Subtask", "description", Status.NEW);
        Subtask subtask1 = new Subtask(id, "Subtask1", "descrip", Status.DONE);

        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask1);

        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Статусы не равны");
    }

    @Test
    void addSubtask() {
        Epic epic = new Epic("Epic", "epicDescp", Status.NEW);
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "Subtask", "description", Status.NEW);

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
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "Subtask", "description", Status.NEW);

        taskManager.createSubtask(subtask);
        List<Subtask> subtasks = taskManager.getSubtasks();

        assertFalse(subtasks.isEmpty(), "Список пуст");
        assertEquals(1, subtasks.size(), "Значения не равны");
    }

    @Test
    void removeAllSubtasks() {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "Subtask", "description", Status.NEW);
        Subtask subtask1 = new Subtask(idEpic, "Subtask1", "descrip", Status.DONE);

        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask1);

        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Статусы не равны");

        taskManager.removeAllSubtask();
        List<Subtask> subtasksAfterRemove = taskManager.getSubtasks();
        List<Integer> subtasksIds = epic.getSubtaskIds();

        assertNotSame(epic.getStatus(), Status.IN_PROGRESS, "Статусы равны");
        assertTrue(subtasksAfterRemove.isEmpty(), "Список не пуст");
        assertTrue(subtasksIds.isEmpty(), "Список не пуст");
    }

    @Test
    void getSubtaskId(){
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        int idEpic =  taskManager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "Subtask", "description", Status.NEW);

        int idSubtask = taskManager.createSubtask(subtask);
        Subtask subtaskGetId = taskManager.getSubtaskId(idSubtask);

        assertNotNull(subtaskGetId, "subtaskGetId == null");
        assertEquals(subtaskGetId, subtask, "Объекты не равны");
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "Subtask", "description", Status.NEW);
        int idSubtask = taskManager.createSubtask(subtask);
        Subtask subtask1 = new Subtask(idEpic, "Subtask1", "descrip",
                idSubtask, Status.DONE);

        assertSame(epic.getStatus(), Status.NEW, "Статусы не равны");

        taskManager.updateSubtask(subtask1);
        List<Subtask> subtasks = taskManager.getSubtasks();
        Subtask subtaskAfterUpdateSubtask = subtasks.get(0);

        assertEquals(subtask1, subtaskAfterUpdateSubtask, "Объекты не равны");
        assertNotSame(epic.getStatus(), Status.NEW, "Статусы равны");
    }

    @Test
    void removeSubtaskId() {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "Subtask", "description", Status.DONE);

        int idSubtask = taskManager.createSubtask(subtask);

        assertSame(epic.getStatus(), Status.DONE, "Статусы не равны");

        taskManager.removeSubtaskId(idSubtask);
        List<Subtask> subtasks = taskManager.getSubtasks();

        assertTrue(subtasks.isEmpty(), "Список не пуст");
        assertNotSame(epic.getStatus(), Status.DONE, "Статусы равны");
    }

    @Test
    void getSubtaskIdByEpic() {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "Subtask", "description", Status.DONE);
        Subtask subtask1 = new Subtask(idEpic, "Subtask1", "descrip", Status.DONE);

        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask1);
        List<Subtask> subtasksByEpic = taskManager.getSubtasksByEpic(idEpic);

        assertEquals(2, subtasksByEpic.size(), "Значение неверное");
    }


}