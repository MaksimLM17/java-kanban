package ru.yandex.javacource.lemekhow.schedule.manager;

import ru.yandex.javacource.lemekhow.schedule.Exception.ManagerInitializationException;
import ru.yandex.javacource.lemekhow.schedule.Exception.ManagerSaveException;
import ru.yandex.javacource.lemekhow.schedule.task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File data;
    private static final String HEADER = "id,type,name,status,description,epic";

    public FileBackedTaskManager(File file) {
        this.data = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            lines.removeFirst();
            int generatorId = 0;
            for (String line : lines) {
                final Task task = readFromString(line);
                final int id = task.getId();
                if (id > generatorId) {
                    generatorId = id;
                }
                taskManager.addAnyTask(task);
            }
            for (Map.Entry<Integer, Subtask> subtaskEntry : taskManager.subtasks.entrySet()) {
                final Subtask subtask = subtaskEntry.getValue();
                final Epic epic = taskManager.epics.get(subtask.getEpicId());
                epic.addSubtaskId(subtask.getId());
            }
            taskManager.counter = generatorId;
        } catch (IOException e) {
            throw new ManagerInitializationException("Can't read form file: " + file.getName(), e);
        }
        return taskManager;
    }

    public String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + ","
                + task.getStatus() + "," + task.getDescription() + ","
                + (task.getType().equals(TaskType.SUBTASK) ? ((Subtask) task).getEpicId() : "");
    }

    @Override
    public int createTask(Task task) {
        int taskId = super.createTask(task);
        save();
        return taskId;
    }

    @Override
    public int createEpic(Epic epic) {
        int epicId = super.createEpic(epic);
        save();
        return epicId;
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        Integer subtaskId = super.createSubtask(subtask);
        save();
        return subtaskId;
    }

    @Override
    public Task removeTaskId(int taskId) {
        Task task = super.removeTaskId(taskId);
        save();
        return task;
    }

    @Override
    public Epic removeEpicId(int epicId) {
        Epic epic = super.removeEpicId(epicId);
        save();
        return epic;
    }

    @Override
    public Subtask removeSubtaskId(int subtaskId) {
        Subtask subtask = super.removeSubtaskId(subtaskId);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateTask(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateTask(subtask);
        save();
    }

    @Override
    public void removeAllTask() {
        super.removeAllTask();
        save();
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }

    @Override
    public void removeAllSubtask() {
        super.removeAllSubtask();
        save();
    }

    private void save() {
        List<String> dataInFile = new ArrayList<>();
        dataInFile.add(HEADER);
        for (Task task : getTasks()) {
            dataInFile.add(toString(task));
        }
        for (Epic epic : getEpics()) {
            dataInFile.add(toString(epic));
        }
        for (Subtask subtask : getSubtasks()) {
            dataInFile.add(toString(subtask));
        }
        try {
            saveToFile(dataInFile);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл! " + e.getMessage());
        }
    }

    private void saveToFile(List<String> arrayData) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(data, StandardCharsets.UTF_8, false))) {
            for (String dataTask : arrayData) {
                writer.write(dataTask);
                writer.newLine();
            }
        }
    }

    private static Task readFromString(String taskInFile) {
        String[] split = taskInFile.split(",");
        Integer id = Integer.parseInt(split[0]);
        Status status;

        if (split[3].equals("NEW")) {
            status = Status.NEW;
        } else if (split[3].equals("DONE")) {
            status = Status.DONE;
        } else {
            status = Status.IN_PROGRESS;
        }

        if (split.length == 6) {
            Integer epicId = Integer.parseInt(split[5]);
            Subtask subtask = new Subtask(epicId, split[2], split[4], id, status);
            return subtask;
        }

        if (split[1].equals("TASK")) {
            Task task = new Task(split[2], split[4], id, status);
            return task;
        } else {
            Epic epic = new Epic(split[2], split[4], id, status);
            return epic;
        }
    }

    protected void addAnyTask(Task task) {
        final int id = task.getId();
        switch (task.getType()) {
            case TASK:
                tasks.put(id, task);
                break;
            case SUBTASK:
                subtasks.put(id, (Subtask) task);
                break;
            case EPIC:
                epics.put(id, (Epic) task);
                break;
        }
    }
}

