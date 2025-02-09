package ru.yandex.javacource.lemekhow.schedule.task;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(Integer epicId, String nameTask, String description, Status status) {
        super(nameTask, description, status);
        this.epicId = epicId;
    }

    public Subtask(Integer epicId, String nameTask, String description, Integer id, Status status) {
        super(nameTask,description, id, status);
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
        return "Subtask{" +
                "epicId=" + epicId +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                '}';
    }
}
