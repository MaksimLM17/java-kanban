package ru.yandex.javacource.lemekhow.schedule.manager;

import ru.yandex.javacource.lemekhow.schedule.Exception.InvalidTimeException;
import ru.yandex.javacource.lemekhow.schedule.Exception.ManagerInitializationException;
import ru.yandex.javacource.lemekhow.schedule.Exception.ManagerSaveException;
import ru.yandex.javacource.lemekhow.schedule.Exception.NotFoundException;
import ru.yandex.javacource.lemekhow.schedule.task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File data;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String HEADER = "id,type,name,status,description,startTime,duration,epicId";

    public FileBackedTaskManager(File file) {
        this.data = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            lines.removeFirst();
            int [] generatorId = {0};
            lines.stream()
                    .map(FileBackedTaskManager::readFromString)
                    .peek(task -> {
                        int id = task.getId();
                        if (id > generatorId[0]) {
                            generatorId[0] = id;
                        }
                    })
                    .forEach(taskManager::addAnyTask);

            taskManager.subtasks.values().forEach(subtask -> {
                Epic epic = taskManager.epics.get(subtask.getEpicId());
                epic.addSubtaskId(subtask.getId());
                taskManager.fullUpdateEpic(epic.getId());
            });
            taskManager.counter = generatorId[0];
        } catch (IOException e) {
            throw new ManagerInitializationException("Can't read form file: " + file.getName(), e);
        }
        return taskManager;
    }

    public String toStringInFile(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + ","
                + task.getStatus() + "," + task.getDescription() + ","
                 + task.getStartTime().format(formatter) + "," + task.getDuration().toMinutes() + ","
                 + (task.getType().equals(TaskType.SUBTASK) ? ((Subtask) task).getEpicId() : "");
    }

    public String toStringForEpic(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + ","
                + task.getStatus() + "," + task.getDescription();
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
    public void updateEpic(Epic epic)  {
        try {
            super.updateTask(epic);
        } catch (NotFoundException ignored){

        }

        save();
    }

    @Override
    public void updateSubtask(Subtask subtask)  {
        try {
            super.updateTask(subtask);
        } catch (NotFoundException ignored) {

        }
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
        try {
            getTasks().forEach(task ->
                    dataInFile.add(toStringInFile(task)));

            getEpics().forEach(epic ->
                    dataInFile.add(toStringForEpic(epic)));

            getSubtasks().forEach(subtask ->
                    dataInFile.add(toStringInFile(subtask)));
        } catch (NotFoundException ignored) {

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

        if (split[1].equals("EPIC")) {
            return new Epic(split[2], split[4], id, status);
        }

        long minutes = Long.parseLong(split[6]);
        Duration duration = Duration.ofMinutes(minutes);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(split[5], formatter);


        if (split[1].equals("SUBTASK")) {
            Integer epicId = Integer.parseInt(split[7]);
            return new Subtask(epicId, split[2], split[4], id, status, startTime, duration);
        } else {
            return new Task(split[2], split[4], id, status, startTime, duration);
        }
    }

    protected void addAnyTask(Task task) {
        final int id = task.getId();
        switch (task.getType()) {
            case TASK:
                tasks.put(id, task);
                try {
                    if (task.getStartTime() != null) {
                        addTaskPriority(task);
                    }
                } catch (InvalidTimeException e) {
                    System.err.println(e.getMessage());
                }
                break;
            case SUBTASK:
                subtasks.put(id, (Subtask) task);
                try {
                    if (task.getStartTime() != null) {
                        addTaskPriority(task);
                    }
                } catch (InvalidTimeException e) {
                    System.err.println(e.getMessage());
                }
                break;
            case EPIC:
                epics.put(id, (Epic) task);
                break;
        }
    }
}

