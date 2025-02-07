package ru.yandex.javacource.lemekhow.schedule.task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds;

    public Epic(String nameTask, String description, Status status) {
        super(nameTask, description, status);
        subtaskIds = new ArrayList<>();
    }

    public Epic(String nameTask, String description, Integer id, Status status) {
        super(nameTask, description, id, status);
        subtaskIds = new ArrayList<>();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public List<Integer> setSubtasksIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
        return subtaskIds;
    }

    public void removeSubtaskIds(Integer subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }


    public String toStringForFile() {
        return super.getId() + "," + TaskName.EPIC + "," + super.getName() + "," + super.getStatus() +
                "," + super.getDescription();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", subtasksIds=" + subtaskIds +
                '}';
    }
}
