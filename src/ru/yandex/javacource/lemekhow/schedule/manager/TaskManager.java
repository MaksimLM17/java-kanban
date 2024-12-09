package ru.yandex.javacource.lemekhow.schedule.manager;

import ru.yandex.javacource.lemekhow.schedule.task.Epic;
import ru.yandex.javacource.lemekhow.schedule.task.Status;
import ru.yandex.javacource.lemekhow.schedule.task.Subtask;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private Integer counter = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();


    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subtaskByEpic = new ArrayList<>();
        for (Integer idSubtaskByEpic : epic.getSubtaskIds()) {
            subtaskByEpic.add(subtasks.get(idSubtaskByEpic));
        }
        return subtaskByEpic;
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }



    public void removeAllTask() {
        tasks.clear();
    }

    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    public void removeAllSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            epic.setStatus(Status.NEW);
        }
    }



    public Task getTaskId(int taskId) {
        return tasks.get(taskId);
    }

    public Epic getEpicId(int epicId) {
        return epics.get(epicId);
    }

    public Subtask getSubtaskId(int subtaskId) {
        return subtasks.get(subtaskId);
    }



    public Task createTask(Task task) {
        Integer newId = generateId();
        task.setId(newId);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        Integer newId = generateId();
        epic.setId(newId);
        epics.put(epic.getId(), epic);
        return epic;

    }

    public Subtask createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        Integer newId = generateId();
        subtask.setId(newId);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic.getId());
        epics.put(epic.getId(), epic);
        return subtask;
    }


    public void updateTask(Task upTask) {
        int id = upTask.getId();
        Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, upTask);
    }

    public void updateEpic(Epic upEpic) {
        Epic savedEpic = epics.get(upEpic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(upEpic.getName());
        savedEpic.setDescription(upEpic.getDescription());
    }

    public void updateSubtask(Subtask upSubtask) {
        int id = upSubtask.getId();
        int epicId = upSubtask.getEpicId();
        Subtask savedSubtask = subtasks.get(id);
        if (savedSubtask == null) {
            return;
        }
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        subtasks.put(id, upSubtask);
        updateEpicStatus(epicId);
    }

    public Task removeTaskId(int taskId) {
        Task task = tasks.remove(taskId);
        return task;
    }

    public Epic removeEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer id : epic.getSubtaskIds()) {
                subtasks.remove(id);
            }
            epic = epics.remove(epicId);
        }
        return epic;
    }

    public Subtask removeSubtaskId(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask == null) {
            return null;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtaskIds(subtaskId);
        updateEpicStatus(epic.getId());
        return subtask;
    }


    private Integer generateId() {
        return ++counter;
    }

    private void updateEpicStatus(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            ArrayList<Integer> subtaskId = epic.getSubtaskIds();
            ArrayList<Integer> value = new ArrayList<>();
            for (Integer id : subtaskId) {
                Subtask subtask = subtasks.get(id);
                if (subtask.getStatus().equals(Status.DONE)) {
                    value.add(id);
                } else if (subtask.getStatus().equals(Status.IN_PROGRESS)) {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }

            if (subtaskId.size() == value.size()) {
                epic.setStatus(Status.DONE);
            } else if (!value.isEmpty()) {
                epic.setStatus(Status.IN_PROGRESS);
            } else {
                epic.setStatus(Status.NEW);
            }
        }
    }
}