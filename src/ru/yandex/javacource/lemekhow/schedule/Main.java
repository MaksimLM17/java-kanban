package ru.yandex.javacource.lemekhow.schedule;

import ru.yandex.javacource.lemekhow.schedule.manager.TaskManager;
import ru.yandex.javacource.lemekhow.schedule.task.Epic;
import ru.yandex.javacource.lemekhow.schedule.task.Status;
import ru.yandex.javacource.lemekhow.schedule.task.Subtask;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();
        Task task = new Task("Hello", "Get up",null, Status.NEW);
        Epic epic = new Epic("Убраться дома", "Выполнить несколько подзадач", null, Status.NEW);
        Subtask subtask = new Subtask("Пропылесосить", "Ковер", null, Status.IN_PROGRESS);
        Subtask subtask1 = new Subtask("Выкинуть мусор", "Собрать и выкинуть мусор", null, Status.NEW);
        subtask.setEpicId(2);
        subtask1.setEpicId(2);
        System.out.println();
        Task createdTask = taskManager.createTask(task);
        Integer id = createdTask.getId();
        taskManager.createEpic(epic);
        System.out.println();
        taskManager.createSubtask(subtask);
        System.out.println();
        taskManager.createSubtask(subtask1);
        System.out.println();
        Subtask subtask2 = new Subtask("Пропылесосить", "Ковер", 3, Status.DONE);
        subtask2.setEpicId(2);
        taskManager.updateSubtask(subtask2);
        System.out.println();
        Subtask subtask3 = new Subtask("Выкинуть мусор", "Собрать и выкинуть мусор", 4, Status.NEW);
        subtask3.setEpicId(2);
        taskManager.updateSubtask(subtask3);
        System.out.println(taskManager.getSubtaskId(4));
        System.out.println(taskManager.getEpicId(2));
        //taskManager.removeAllEpic();
        //taskManager.removeSubtaskId(3);
        System.out.println();


    }

}
