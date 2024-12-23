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
        Subtask subtask = new Subtask(1,"Пропылесосить", "Ковер", Status.IN_PROGRESS);
        Subtask subtask1 = new Subtask(1,"Выкинуть мусор", "Собрать и выкинуть мусор",  Status.NEW);
        subtask.setEpicId(2);
        subtask1.setEpicId(2);
        System.out.println();
        taskManager.createTask(task);
        taskManager.getTaskId(task.getId());

        taskManager.createEpic(epic);
        System.out.println();
        taskManager.createSubtask(subtask);
        System.out.println();
        taskManager.createSubtask(subtask1);
        System.out.println();
        Subtask subtask2 = new Subtask(1,"Пропылесосить", "Ковер",  Status.DONE);
        subtask2.setEpicId(2);
        taskManager.createSubtask(subtask2);
        taskManager.updateSubtask(subtask2);
        System.out.println();
        Subtask subtask3 = new Subtask(1,"Выкинуть мусор", "Собрать и выкинуть мусор",  Status.NEW);
        subtask3.setEpicId(2);
        taskManager.updateSubtask(subtask3);
        taskManager.getSubtaskId(subtask2.getId());
        /*System.out.println(taskManager.getSubtaskId(4));
        System.out.println(taskManager.getSubtaskId(3));*/
        //taskManager.removeAllEpic();
        //taskManager.removeSubtaskId(3);
        List<Task> historyTasks = taskManager.getHistory();
        int index = 0;
        for(Task t : historyTasks) {
            index++;
            System.out.println(index + ". "  + t + ". ");
        }

    }

}
