package ru.yandex.javacource.lemekhow.schedule.manager;

import ru.yandex.javacource.lemekhow.schedule.task.Epic;
import ru.yandex.javacource.lemekhow.schedule.task.Status;
import ru.yandex.javacource.lemekhow.schedule.task.Subtask;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private Integer counter = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }


    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> subtaskByEpic = new ArrayList<>();
        for (Integer idSubtaskByEpic : epic.getSubtaskIds()) {
            subtaskByEpic.add(subtasks.get(idSubtaskByEpic));
        }
        return subtaskByEpic;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }



    @Override
    public void removeAllTask() {
        for (Integer id : epics.keySet()){
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpic() {
        for (Epic epic : epics.values()){
            epic.clearSubtaskIds();
        }
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();

    }

    @Override
    public void removeAllSubtask() {
        List<Integer> subtaskInRemove = new ArrayList<>(subtasks.keySet());
        for (Integer id : subtaskInRemove) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            epic.setStatus(Status.NEW);
        }
    }



    @Override
    public Task getTaskId(int taskId) {
        Task task = tasks.get(taskId);
        Task taskForHistory = new Task(task.getName(), task.getDescription(), task.getId(), task.getStatus());
        historyManager.add(taskForHistory);
        return taskForHistory;
    }

    @Override
    public Epic getEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        Epic epicForHistory = new Epic(epic.getName(), epic.getDescription(), epic.getId(), epic.getStatus());
        epicForHistory.setSubtasksIds(epic.getSubtaskIds());
        historyManager.add(epicForHistory);
        return epicForHistory;
    }

    @Override
    public Subtask getSubtaskId(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);

        Subtask subtaskForHistory = new Subtask(subtask.getEpicId(), subtask.getName(),
                subtask.getDescription(), subtask.getId(), subtask.getStatus());

        historyManager.add(subtaskForHistory);
        return subtaskForHistory;
    }



    @Override
    public int createTask(Task task) {
        Integer newId = generateId();
        task.setId(newId);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        Integer newId = generateId();
        epic.setId(newId);
        epics.put(epic.getId(), epic);
        return epic.getId();

    }

    @Override
    public Integer createSubtask(Subtask subtask) {
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
        return subtask.getId();
    }


    @Override
    public void updateTask(Task upTask) {
        int id = upTask.getId();
        Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, upTask);
    }

    @Override
    public void updateEpic(Epic upEpic) {
        final Epic savedEpic = epics.get(upEpic.getId());
        if (savedEpic == null) {
            return;
        }
        upEpic.setSubtasksIds(savedEpic.getSubtaskIds());
        upEpic.setStatus(savedEpic.getStatus());
        epics.put(upEpic.getId(), upEpic);
    }

    @Override
    public void updateSubtask(Subtask upSubtask) {
        Integer id = upSubtask.getId();
        Integer epicId = upSubtask.getEpicId();
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

    @Override
    public Task removeTaskId(int taskId) {
        Task task = tasks.remove(taskId);
        historyManager.remove(taskId);
        return task;
    }

    @Override
    public Epic removeEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer id : epic.getSubtaskIds()) {
                subtasks.remove(id);
            }
            historyManager.remove(epicId);
            for(Integer id : epic.getSubtaskIds()){
                historyManager.remove(id);
            }
            epic = epics.remove(epicId);

        }
        return epic;
    }

    @Override
    public Subtask removeSubtaskId(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask == null) {
            return null;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtaskIds(subtaskId);
        updateEpicStatus(epic.getId());
        historyManager.remove(subtaskId);
        return subtask;
    }

    private Integer generateId() {
        return ++counter;
    }

    private void updateEpicStatus(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            List<Integer> subtaskId = epic.getSubtaskIds();
            if(subtaskId.isEmpty()) {
                epic.setStatus(Status.NEW);
                return;
            }
            List<Integer> value = new ArrayList<>();
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