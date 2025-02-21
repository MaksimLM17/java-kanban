package ru.yandex.javacource.lemekhow.schedule.manager;

import ru.yandex.javacource.lemekhow.schedule.Exception.InvalidTimeException;
import ru.yandex.javacource.lemekhow.schedule.task.Epic;
import ru.yandex.javacource.lemekhow.schedule.task.Status;
import ru.yandex.javacource.lemekhow.schedule.task.Subtask;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected Integer counter = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public void fullUpdateEpic(int epicId) {
        updateEpicStatus(epicId);
        updateStartTimeEpic(epicId);
        updateDurationEpic(epicId);
        updateEndTimeEpic(epicId);
    }

    public void addTaskPriority(Task task) throws InvalidTimeException {
        if (validationTimeTasks(task)) {
            prioritizedTasks.add(task);
        } else {
            throw new InvalidTimeException("Задача с id= " + task.getId() + " пересекается с задачами из списка");
        }
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return new LinkedHashSet<>(prioritizedTasks);
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
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .toList();
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllTask() {
        tasks.keySet()
                .forEach(historyManager::remove);
        tasks.values()
                .forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public void removeAllEpic() {
        epics.values().forEach(epic -> {
            epic.getSubtaskIds()
                    .forEach(id -> {
                        historyManager.remove(id);
                        prioritizedTasks.remove(subtasks.get(id));
                    });
            epic.clearSubtaskIds();
            historyManager.remove(epic.getId());
                });
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtask() {
        subtasks.keySet()
                .forEach(historyManager::remove);
        subtasks.values()
                .forEach(prioritizedTasks::remove);
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.clearSubtaskIds();
            epic.setStatus(Status.NEW);
            epic.setStartTime(null);
            epic.setDuration(null);
        });
    }

    @Override
    public Task getTaskId(int taskId) {
        Task task = tasks.get(taskId);
        Task taskForHistory;

        if (task.getStartTime() != null) {
            taskForHistory = new Task(task.getName(), task.getDescription(), task.getId(), task.getStatus(),
                    task.getStartTime(), task.getDuration());
        } else {
            taskForHistory = new Task(task.getName(), task.getDescription(), task.getId(), task.getStatus());
        }

        historyManager.add(taskForHistory);
        return taskForHistory;
    }

    @Override
    public Epic getEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        Epic epicForHistory;

        if (epic.getStartTime() != null) {
            epicForHistory = new Epic(epic.getName(), epic.getDescription(), epic.getId(), epic.getStatus(),
                    epic.getStartTime(), epic.getDuration(), epic.getEndTime());
            epicForHistory.setSubtasksIds(epic.getSubtaskIds());
        } else {
            epicForHistory = new Epic(epic.getName(), epic.getDescription(), epic.getId(), epic.getStatus());
            epicForHistory.setSubtasksIds(epic.getSubtaskIds());
        }

        historyManager.add(epicForHistory);
        return epicForHistory;
    }

    @Override
    public Subtask getSubtaskId(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        Subtask subtaskForHistory;

        if (subtask.getStartTime() != null) {
            subtaskForHistory = new Subtask(subtask.getEpicId(), subtask.getName(),
                    subtask.getDescription(), subtask.getId(), subtask.getStatus(),
                    subtask.getStartTime(), subtask.getDuration());
        } else {
            subtaskForHistory = new Subtask(subtask.getEpicId(), subtask.getName(),
                    subtask.getDescription(), subtask.getId(), subtask.getStatus());
        }

        historyManager.add(subtaskForHistory);
        return subtaskForHistory;
    }


    @Override
    public int createTask(Task task) {
        Integer newId = generateId();
        task.setId(newId);
        tasks.put(task.getId(), task);
        try {
            if (task.getStartTime() != null) {
                addTaskPriority(task);
            }
        } catch (InvalidTimeException e) {
            System.err.println(e.getMessage());
        }
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
        try {
            if (subtask.getStartTime() != null) {
                addTaskPriority(subtask);
            }
        } catch (InvalidTimeException e) {
            System.err.println(e.getMessage());
        }
        epic.addSubtaskId(subtask.getId());
        fullUpdateEpic(epicId);
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
        try {
            if (upTask.getStartTime() != null) {
                prioritizedTasks.remove(savedTask);
                addTaskPriority(upTask);
            }
        } catch (InvalidTimeException e) {
            System.err.println(e.getMessage());
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
        try {
            if (upSubtask.getStartTime() != null) {
                prioritizedTasks.remove(savedSubtask);
                addTaskPriority(upSubtask);
            }
        } catch (InvalidTimeException e) {
            System.err.println(e.getMessage());
        }
        fullUpdateEpic(epicId);
    }

    @Override
    public Task removeTaskId(int taskId) {
        Task task = tasks.remove(taskId);
        historyManager.remove(taskId);
        prioritizedTasks.remove(task);
        return task;
    }

    @Override
    public Epic removeEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.getSubtaskIds().forEach(id -> {
                historyManager.remove(id);
                prioritizedTasks.remove(subtasks.get(id));
                subtasks.remove(id);
            });
            historyManager.remove(epicId);
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
        fullUpdateEpic(epic.getId());
        historyManager.remove(subtaskId);
        prioritizedTasks.remove(subtask);
        return subtask;
    }

    private Integer generateId() {
        return ++counter;
    }

    private void updateEpicStatus(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            List<Integer> subtaskIds = epic.getSubtaskIds();
            if (subtaskIds.isEmpty()) {
                epic.setStatus(Status.NEW);
                return;
            }

            int doneCount = (int) subtaskIds.stream()
                    .filter(id -> subtasks.get(id).getStatus().equals(Status.DONE))
                    .count();

            boolean hasInProgress = subtaskIds.stream()
                    .anyMatch(id -> subtasks.get(id).getStatus().equals(Status.IN_PROGRESS));

            if (subtaskIds.size() == doneCount) {
                epic.setStatus(Status.DONE);
            } else if (hasInProgress) {
                epic.setStatus(Status.IN_PROGRESS);
            } else {
                epic.setStatus(Status.NEW);
            }
        }
    }

    private void updateStartTimeEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            List<Integer> subtaskIds = epic.getSubtaskIds();
            if (subtaskIds.isEmpty()) {
                return;
            }

            LocalDateTime startTime = subtaskIds.stream()
                    .map(subtasks::get)
                    .filter(subtask -> subtask != null && subtask.getStartTime() != null)
                    .map(Subtask::getStartTime)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);

            epic.setStartTime(startTime);
        }

    }

    private void updateDurationEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            List<Integer> subtaskIds = epic.getSubtaskIds();
            if (subtaskIds.isEmpty()) {
                epic.setDuration(Duration.ZERO);
                return;
            }
            Duration durationEpic = subtaskIds.stream()
                    .map(subtasks::get)
                    .filter(subtask -> subtask != null && subtask.getDuration() != null)
                    .map(Subtask::getDuration)
                    .reduce(Duration.ZERO, Duration::plus);

            epic.setDuration(durationEpic);
        }
    }

    private void updateEndTimeEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            List<Integer> subtaskIds = epic.getSubtaskIds();
            if (subtaskIds.isEmpty()) {
                return;
            }

            LocalDateTime endTime = subtaskIds.stream()
                    .map(subtasks::get)
                    .filter(subtask -> subtask != null && subtask.getEndTime() != null)
                    .map(Subtask::getEndTime)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            epic.setEndTime(endTime);
        }
    }

    private boolean validationTimeTasks(Task task) {
        if (prioritizedTasks.isEmpty()) {
            return true;
        } else {
            return prioritizedTasks.stream()
                    .anyMatch(val -> (val.getStartTime().isBefore(task.getStartTime()) && val.getStartTime().isBefore(task.getEndTime())) ||
                            val.getStartTime().isAfter(task.getEndTime()) || val.getEndTime().isBefore(task.getStartTime()));
        }
    }
}