package ru.yandex.javacource.lemekhow.schedule;

import ru.yandex.javacource.lemekhow.schedule.manager.Managers;
import ru.yandex.javacource.lemekhow.schedule.manager.TaskManager;

import ru.yandex.javacource.lemekhow.schedule.task.Epic;
import ru.yandex.javacource.lemekhow.schedule.task.Status;
import ru.yandex.javacource.lemekhow.schedule.task.Subtask;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Hello", "Get up", Status.NEW);
        Epic epic = new Epic("Убраться дома", "Выполнить несколько подзадач",  Status.NEW);
        Subtask subtask = new Subtask(2, "Пропылесосить", "Ковер", Status.IN_PROGRESS);
        Subtask subtask1 = new Subtask(2, "Выкинуть мусор", "Собрать и выкинуть мусор",  Status.NEW);
        System.out.println();
        taskManager.createTask(task);
        taskManager.getTaskId(task.getId());
        Task task1 = new Task("Hello1", "Get up1", Status.NEW);
        Epic epic1 = new Epic("Epic", "Epic to home", Status.NEW);
        taskManager.createEpic(epic1);
        taskManager.createTask(task1);

        taskManager.createEpic(epic);
        System.out.println();
        taskManager.createSubtask(subtask);
        System.out.println();
        taskManager.createSubtask(subtask1);
        System.out.println();
        Subtask subtask2 = new Subtask(2,"Пропылесосить", "Ковер",  Status.DONE);
        taskManager.createSubtask(subtask2);
        System.out.println();
        Subtask subtask3 = new Subtask(2,"Выкинуть мусор", "Собрать и выкинуть мусор",  Status.NEW);

        taskManager.createSubtask(subtask3);
        taskManager.getSubtaskId(subtask2.getId());
        taskManager.getTaskId(task.getId());
        taskManager.getEpicId(epic1.getId());
        taskManager.getTaskId(task1.getId());
        taskManager.getSubtaskId(subtask2.getId());
        taskManager.getEpicId(epic.getId());
        taskManager.getSubtaskId(subtask.getId());
        taskManager.getEpicId(epic1.getId());
        taskManager.getTaskId(task1.getId());
        //taskManager.removeEpicId(epic1.getId());
        taskManager.removeAllEpic();
        System.out.println(epic1.getSubtaskIds());


        List<Task> historyTasks = taskManager.getHistory();

        System.out.println(historyTasks);
        int index = 0;
        for (Task t : historyTasks) {
            index++;
            System.out.println(index + ". "  + t + ". ");
        }

    }

}
