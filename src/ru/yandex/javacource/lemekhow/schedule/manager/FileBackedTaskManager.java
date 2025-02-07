package ru.yandex.javacource.lemekhow.schedule.manager;

import ru.yandex.javacource.lemekhow.schedule.Exception.ManagerInitializationException;
import ru.yandex.javacource.lemekhow.schedule.Exception.ManagerSaveException;
import ru.yandex.javacource.lemekhow.schedule.task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File data;

    public FileBackedTaskManager( File file) {
        this.data = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = (FileBackedTaskManager) Managers.getDefaultFileManager(file);
        try (Reader fileReader = new FileReader(file)) {
            BufferedReader br = new BufferedReader(fileReader);
            List<String> stringsFromFile = new ArrayList<>();

            while (br.ready()) {
                stringsFromFile.add(br.readLine());
            }

            stringsFromFile.removeFirst();
            for (String fromFile : stringsFromFile) {
                fileBackedTaskManager.readFromString(fromFile);
            }

            fileBackedTaskManager.counter = fileBackedTaskManager.newCounter(stringsFromFile);
        } catch (IOException e) {
            throw new ManagerInitializationException("Ошибка чтения файла! " + e.getMessage());
        }
        return fileBackedTaskManager;
    }

    @Override
    public int createTask(Task task) {
        int taskId = super.createTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Ошибка сохранения задачи в файл: " + getTaskId(taskId));
        }
        return taskId;
    }

    @Override
    public int createEpic(Epic epic) {
        int epicId = super.createEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Ошибка сохранения эпика в файл: " + getEpicId(epicId));
        }
        return epicId;
    }

    @Override
    public Integer createSubtask(Subtask subtask)  {
        Integer subtaskId = super.createSubtask(subtask);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Ошибка сохранения подзадачи в файл: " + getSubtaskId(subtaskId));
        }
        return subtaskId;
    }

    @Override
    public Task removeTaskId(int taskId) {
        Task task = super.removeTaskId(taskId);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл при удалении задачи: " + task);
        }
        return task;
    }

    @Override
    public Epic removeEpicId(int epicId) {
        Epic epic = super.removeEpicId(epicId);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл при удалении эпика: " + epic);
        }
        return epic;
    }

    @Override
    public Subtask removeSubtaskId(int subtaskId) {
        Subtask subtask = super.removeSubtaskId(subtaskId);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл при удалении эпика: " + subtask);
        }
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Ошибка сохранения данных после обновления задачи: " + task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateTask(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Ошибка сохранения данных после обновления эпика: " + epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateTask(subtask);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Ошибка сохранения данных после обновления подзадачи: " + subtask);
        }
    }

    @Override
    public void removeAllTask() {
        super.removeAllTask();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл после удаления всех задач!");
        }
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл после удаления всех эпиков!");
        }
    }

    @Override
    public void removeAllSubtask() {
        super.removeAllSubtask();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл после удаления всех подзадач!");
        }
    }

    private void save() {
        List<String> dataInFile = new ArrayList<>();
        final String content = "id,type,name,status,description,epic";
        dataInFile.add(content);
        for (Task task : getTasks()) {
            dataInFile.add(task.toStringForFile());
        }
        for (Epic epic : getEpics()) {
            dataInFile.add(epic.toStringForFile());
        }
        for (Subtask subtask : getSubtasks()) {
            dataInFile.add(subtask.toStringForFile());
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
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Произошла ошибка сохранения в файл" + e.getMessage());
        }
    }

    private  Task readFromString(String taskInFile) {
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
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(epicId);
            epic.addSubtaskId(id);
            epics.put(epicId, epic);
            return subtask;
        }

        if (split[1].equals("TASK")) {
            Task task = new Task(split[2], split[4], id, status);
            tasks.put(task.getId(), task);
            return task;
        } else {
            Epic epic = new Epic(split[2], split[4], id, status);
            epics.put(epic.getId(), epic);
            return epic;
        }
    }

    private int newCounter(List<String> strings) {
        int newCounter = 0;
        for (String str : strings) {
            String result = str.substring(0,1);
            int number = Integer.parseInt(result);
            if (number > newCounter) {
                newCounter = number;
            }
        }
        return newCounter;
    }
}

