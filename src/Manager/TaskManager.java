package Manager;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private Integer counter = 0;
    HashMap<Integer, Task> taskMap = new HashMap<>();
    HashMap<Integer, Epic> epicMap = new HashMap<>();
    HashMap<Integer, Subtask> subtaskMap = new HashMap<>();


    private Integer generateId() {
        return counter++;
    }
    private Status checkStatusEpic(int epicId) {
        if (epicMap.containsKey(epicId)) {
            Epic epic = epicMap.get(epicId);
            ArrayList<Integer> subtaskId = epic.getSubtaskIds();
            ArrayList<Integer> value = new ArrayList<>();
            for (Integer id : subtaskId) {
                Subtask subtask = subtaskMap.get(id);
                if (subtask.getStatus().equals(Status.DONE)) {
                    value.add(id);
                } else if (subtask.getStatus().equals(Status.IN_PROGRESS)) {
                    return Status.IN_PROGRESS;
                }
            }
            if (subtaskId.size() == value.size()) {
                return Status.DONE;
            } else if (!value.isEmpty()) {
                return Status.IN_PROGRESS;
            } else {
                return Status.NEW;
            }
        }
        return null;
    }




    public ArrayList<Task> listTask() {
        ArrayList<Task> listTask = new ArrayList<>();
        for (Task task : taskMap.values()) {
            listTask.add(task);
        }
        return listTask;
    }

    public ArrayList<Epic> listEpic() {
        ArrayList<Epic> listEpic = new ArrayList<>();
        for (Epic epic : epicMap.values()) {
            listEpic.add(epic);
        }
        return listEpic;
    }

    public ArrayList<Subtask> listSubtaskByEpic(int epicId) {
        Epic epic = epicMap.get(epicId);
        ArrayList<Subtask> subtaskByEpic = new ArrayList<>();
        for (Integer idSubtaskByEpic : epic.getSubtaskIds()) {
            subtaskByEpic.add(subtaskMap.get(idSubtaskByEpic));
        }
        return subtaskByEpic;
    }

    public ArrayList<Subtask> listSubtask() {
        ArrayList<Subtask> listSubtask = new ArrayList<>();
        for (Subtask subtask : subtaskMap.values()) {
            listSubtask.add(subtask);
        }
        return listSubtask;
    }



    public HashMap<Integer, Task> removeAllTask() {
        taskMap.clear();
        return taskMap;
    }

    public HashMap<Integer, Epic> removeAllEpic() {
        epicMap.clear();
        subtaskMap.clear();
        return epicMap;

    }

    public HashMap<Integer, Subtask> removeAllSubtask() {
        subtaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.clearSubtaskIds();
            epic.setStatus(Status.NEW);
        }
        return subtaskMap;

    }



    public Task getTaskId(int taskId) {
        return taskMap.get(taskId);
    }

    public Epic getEpicId(int epicId) {
        return epicMap.get(epicId);
    }

    public Subtask getSubtaskId(int subtaskId) {
        return subtaskMap.get(subtaskId);
    }



    public Task createTask(Task task) {
        Integer newId = generateId();
        task.setId(newId);
        Task createdTask = new Task(task.getNameTask(), task.getDescription(), task.getId(), task.getStatus());
        taskMap.put(createdTask.getId(), createdTask);
        task.setId(newId);
        return task;
    }

    public Epic createEpic(Epic epic) {
        Integer newId = generateId();
        epic.setId(newId);
        Epic createdEpic = new Epic(epic.getNameTask(), epic.getDescription(), epic.getId(), epic.getStatus());
        epicMap.put(createdEpic.getId(), createdEpic);
        epic.setId(newId);
        return epic;

    }

    public Subtask createSubtask(Subtask subtask) {
        if(epicMap.containsKey(subtask.getEpicId())) {
            Integer newId = generateId();
            subtask.setId(newId);
            Subtask createSubtask = new Subtask(subtask.getNameTask(), subtask.getDescription(), subtask.getId(), subtask.getStatus());
            createSubtask.setEpicId(subtask.getEpicId());
            subtaskMap.put(createSubtask.getId(), createSubtask);
            Epic epic = epicMap.get(createSubtask.getEpicId());
            ArrayList<Integer> saveSubtaskId = epic.getSubtaskIds();
            saveSubtaskId.add(createSubtask.getId());
            epic.setSubtaskIds(saveSubtaskId);
            epicMap.put(epic.getId(), epic);
            subtask.setId(newId);
            return subtask;
        }
        return null;

    }



    public Task updateTask(Task upTask) {
        if (taskMap.containsKey(upTask.getId())) {
            Task existingTask = taskMap.get(upTask.getId());
            existingTask.setNameTask(upTask.getNameTask());
            existingTask.setDescription(upTask.getDescription());
            existingTask.setStatus(upTask.getStatus());
            taskMap.put(existingTask.getId(), existingTask);
            return taskMap.get(existingTask.getId());
        }
        return null;
    }

    public Epic updateEpic(Epic upEpic) {
        if (epicMap.containsKey(upEpic.getId())) {
            Epic existingEpic = epicMap.get(upEpic.getId());
            existingEpic.setNameTask(upEpic.getNameTask());
            existingEpic.setDescription(upEpic.getDescription());
            existingEpic.setSubtaskIds(upEpic.getSubtaskIds());
            existingEpic.setStatus(checkStatusEpic(existingEpic.getId()));
            epicMap.put(existingEpic.getId(), existingEpic);
            return epicMap.get(existingEpic.getId());
        }
        return null;
    }

    public Subtask updateSubtask(Subtask upSubtask) {
        if (subtaskMap.containsKey(upSubtask.getId())) {
            Subtask existingSubtask = subtaskMap.get(upSubtask.getId());
            existingSubtask.setNameTask(upSubtask.getNameTask());
            existingSubtask.setDescription(upSubtask.getDescription());
            existingSubtask.setStatus(upSubtask.getStatus());
            Epic epic = epicMap.get(existingSubtask.getEpicId());
            epic.setStatus(checkStatusEpic(epic.getId()));
            subtaskMap.put(existingSubtask.getId(), existingSubtask);
            return subtaskMap.get(existingSubtask.getId());
        }
        return null;
    }



    public Task removeTaskId(int taskId) {
        Task task = taskMap.remove(taskId);
        return task;
    }

    public Epic removeEpicId(int epicId) {
        Epic epic = epicMap.get(epicId);
        if (epic != null) {
            for (Integer id : epic.getSubtaskIds()) {
                subtaskMap.remove(id);
            }
            epic = epicMap.remove(epicId);
            return epic;
        }
        return null;
    }

    public Subtask removeSubtaskId(int subtaskId) {
        Subtask subtask = subtaskMap.get(subtaskId);
        if (subtask != null) {
            Epic epic = epicMap.get(subtask.getEpicId());
            subtaskMap.remove(subtask);
            epic.removeSubtaskIds(subtask.getId());
            epic.setStatus(checkStatusEpic(subtask.getEpicId()));
            epicMap.put(epic.getId(), epic);
            return subtaskMap.remove(subtaskId);
        }
        return null;
    }
}