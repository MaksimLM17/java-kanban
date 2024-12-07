package Manager;

import java.util.HashMap;
import java.util.Objects;

public class Task {
    private String nameTask;
    private String description;
    private Integer id;
    private Status status;
    public Task(String nameTask, String description, Integer id, Status status) {
        this.nameTask = nameTask;
        this.description = description;
        this.id = id;
        this.status = status;

    }

    public String getNameTask() {
        return nameTask;
    }

    public String getDescription() {
        return description;
    }
    public Integer getId() {
        return id;
    }

    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "nameTask='" + nameTask + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}

