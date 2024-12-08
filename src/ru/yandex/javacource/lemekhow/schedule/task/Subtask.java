package ru.yandex.javacource.lemekhow.schedule.task;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String nameTask, String description, Integer id, Status status) {
        super(nameTask, description, id, status);
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId='" + epicId + '\'' +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                '}';
    }
}
