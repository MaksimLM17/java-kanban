package ru.yandex.javacource.lemekhow.schedule.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.lemekhow.schedule.task.Epic;
import ru.yandex.javacource.lemekhow.schedule.task.Status;
import ru.yandex.javacource.lemekhow.schedule.task.Subtask;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.io.File;
import java.io.IOException;

public class FileBackedTaskManagerTest {
    private TaskManager fileManager;
    private Task task;
    private Task task1;
    private Epic epic;
    private Epic epic1;
    private Subtask subtask;
    private Subtask subtask1;
    private File tempFile;

    @BeforeEach
    public void beforeEach() {
        try {
            tempFile = File.createTempFile("data", "csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        task = new Task("Task", "clean window", 1, Status.NEW);
        task1 = new Task("TaskName", "test", 4, Status.IN_PROGRESS);
        epic = new Epic("Epic", "Going", 2, Status.NEW);
        epic1 = new Epic("EpicTest", "Pass the test", 5, Status.NEW);
        subtask = new Subtask(2, "Subtask", "go to the park", 3, Status.IN_PROGRESS);
        subtask1 = new Subtask(2, "SubtaskTest", "go to the street", 6, Status.DONE);
        fileManager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void loadFromFileTest() {
        fileManager.createTask(task);
        fileManager.createTask(task1);
        fileManager.createEpic(epic);
        fileManager.createEpic(epic1);
        fileManager.createSubtask(subtask);
        fileManager.createSubtask(subtask1);
        TaskManager loadManager =  FileBackedTaskManager.loadFromFile(tempFile);

        String actually = fileManager.getTasks() + " " +  fileManager.getEpics() +  " " + fileManager.getSubtasks();
        String expected = loadManager.getTasks() + " " + loadManager.getEpics() + " " + loadManager.getSubtasks();

        Assertions.assertEquals(actually, expected, "Строки не совпадают");
    }

    @Test
    void createTaskTest() {
        fileManager.createTask(task);
        fileManager.createEpic(epic1);
        fileManager.createSubtask(subtask);

        TaskManager loadManager =  FileBackedTaskManager.loadFromFile(tempFile);
        String actual = fileManager.getTaskId(task.getId()).toString();
        String expected = loadManager.getTaskId(task.getId()).toString();

        Assertions.assertEquals(actual, expected, "Задачи не совпадают");
    }

    @Test
    void updateTaskTest() {
        fileManager.createTask(task);
        Task testTask = fileManager.getTaskId(task.getId());
        testTask.setName("New name");
        fileManager.updateTask(testTask);

        TaskManager loadManager =  FileBackedTaskManager.loadFromFile(tempFile);
        String actual = fileManager.getTaskId(task.getId()).toString();
        String expected = loadManager.getTaskId(task.getId()).toString();

        Assertions.assertEquals(actual, expected, "Задачи не совпадают, после обновления");
    }

    @Test
    void removeTaskIdTest() {
        fileManager.createTask(task);
        fileManager.createTask(task1);
        fileManager.removeTaskId(task.getId());

        TaskManager loadManager =  FileBackedTaskManager.loadFromFile(tempFile);
        String actual = fileManager.getTasks().toString();
        String expected = loadManager.getTasks().toString();

        Assertions.assertEquals(actual, expected, "Задачи не совпадают после удаления");
    }

    @Test
    void removeAllTaskTest() {
        fileManager.createTask(task);
        fileManager.createTask(task1);
        fileManager.removeAllTask();

        TaskManager loadManager =  FileBackedTaskManager.loadFromFile(tempFile);
        String actual = fileManager.getTasks().toString();
        String expected = loadManager.getTasks().toString();

        Assertions.assertEquals(actual, expected, "Задачи не совпадают после удаления всех задач");
    }

    @Test
    void createEpicTest() {
        fileManager.createTask(task);
        fileManager.createEpic(epic1);
        fileManager.createSubtask(subtask);

        TaskManager loadManager =  FileBackedTaskManager.loadFromFile(tempFile);
        String actual = fileManager.getEpicId(epic1.getId()).toString();
        String expected = loadManager.getEpicId(epic1.getId()).toString();

        Assertions.assertEquals(actual, expected, "Эпики не совпадают");
    }

    @Test
    void updateEpicTest() {
        fileManager.createEpic(epic);
        Epic testEpic = fileManager.getEpicId(epic.getId());
        testEpic.setName("New name");
        fileManager.updateEpic(testEpic);

        TaskManager loadManager =  FileBackedTaskManager.loadFromFile(tempFile);
        String actual = fileManager.getEpicId(epic.getId()).toString();
        String expected = loadManager.getEpicId(epic.getId()).toString();

        Assertions.assertEquals(actual, expected, "Эпики не совпадают, после обновления");
    }

    @Test
    void removeEpicIdTest() {
        fileManager.createEpic(epic);
        fileManager.createEpic(epic1);
        fileManager.removeEpicId(epic1.getId());

        TaskManager loadManager =  FileBackedTaskManager.loadFromFile(tempFile);
        String actual = fileManager.getEpics().toString();
        String expected = loadManager.getEpics().toString();

        Assertions.assertEquals(actual, expected, "Эпики не совпадают после удаления");
    }

    @Test
    void removeAllEpicTest() {
        fileManager.createEpic(epic);
        fileManager.createEpic(epic1);
        fileManager.removeAllEpic();

        TaskManager loadManager =  FileBackedTaskManager.loadFromFile(tempFile);
        String actual = fileManager.getEpics().toString();
        String expected = loadManager.getEpics().toString();

        Assertions.assertEquals(actual, expected, "Эпики не совпадают после удаления");
    }

    @Test
    void createSubtaskTest() {
        fileManager.createTask(task);
        fileManager.createEpic(epic1);
        fileManager.createSubtask(subtask);

        TaskManager loadManager =  FileBackedTaskManager.loadFromFile(tempFile);
        String actual = fileManager.getSubtaskId(subtask.getId()).toString();
        String expected = loadManager.getSubtaskId(subtask.getId()).toString();

        Assertions.assertEquals(actual, expected, "Подзадачи не совпадают");
    }

    @Test
    void updateSubtaskTest() {
        fileManager.createTask(task);
        fileManager.createEpic(epic);
        fileManager.createSubtask(subtask);
        Subtask testSubtask = fileManager.getSubtaskId(subtask.getId());
        testSubtask.setName("New name");
        fileManager.updateSubtask(testSubtask);

        TaskManager loadManager =  FileBackedTaskManager.loadFromFile(tempFile);
        String actual = fileManager.getSubtaskId(subtask.getId()).toString();
        String expected = loadManager.getSubtaskId(subtask.getId()).toString();

        Assertions.assertEquals(actual, expected, "Подзадачи не совпадают, после обновления");
    }

    @Test
    void removeSubtaskIdTest() {
        fileManager.createTask(task);
        fileManager.createEpic(epic);
        fileManager.createSubtask(subtask);
        fileManager.createSubtask(subtask1);
        fileManager.removeSubtaskId(subtask.getId());

        TaskManager loadManager =  FileBackedTaskManager.loadFromFile(tempFile);
        String actual = fileManager.getSubtasks().toString();
        String expected = loadManager.getSubtasks().toString();

        Assertions.assertEquals(actual, expected, "Подзадачи не совпадают после удаления");
    }

    @Test
    void removeAllSubtaskTest() {
        fileManager.createTask(task);
        fileManager.createEpic(epic);
        fileManager.createSubtask(subtask);
        fileManager.createSubtask(subtask1);
        fileManager.removeAllSubtask();

        TaskManager loadManager =  FileBackedTaskManager.loadFromFile(tempFile);
        String actual = fileManager.getSubtasks().toString();
        String expected = loadManager.getSubtasks().toString();

        Assertions.assertEquals(actual, expected, "Подзадачи не совпадают после удаления");
    }

}
