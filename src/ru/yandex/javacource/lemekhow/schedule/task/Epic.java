package ru.yandex.javacource.lemekhow.schedule.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic(String nameTask, String description, Integer id, Status status) {
        super(nameTask, description, id, status);
        subtaskIds = new ArrayList<>();
    }

    public Epic(String nameTask, String description, Status status) {
        super(nameTask, description, status);
        subtaskIds = new ArrayList<>();
    }

    public Epic(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        subtaskIds = new ArrayList<>();
    }

    public Epic(String name, String description,Integer id, Status status,
                LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        super(name, description,id, status, startTime, duration);
        this.endTime = endTime;
        subtaskIds = new ArrayList<>();
    }

    public Epic(String name, String description, Status status,
                LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        super(name, description, status, startTime, duration);
        this.endTime = endTime;
        subtaskIds = new ArrayList<>();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtasksIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", subtasksIds=" + subtaskIds +
                ", startTime=" + super.getStartTime().format(formatter) +
                ", duration=" + super.getDuration().toHours() + "ч" + super.getDuration().toMinutesPart() + "мин" +
                ", endTime=" + endTime.format(formatter) +
                '}';
    }
}
