package ru.yandex.javacource.lemekhow.schedule.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(Integer epicId, String nameTask, String description, Integer id, Status status) {
        super(nameTask,description, id, status);
        this.epicId = epicId;
    }

    public Subtask(Integer epicId, String name, String description, Integer id,
                   Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, id, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(Integer epicId, String name, String description,
                   Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Subtask{" +
                "epicId=" + epicId +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", startTime=" + super.getStartTime().format(formatter) +
                ", duration=" + super.getDuration().toHours() + "ч" + super.getDuration().toMinutesPart() + "мин" +
                ", endTime=" + super.getEndTime().format(formatter) +
                '}';
    }
}
