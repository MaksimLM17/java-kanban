package Manager;

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
}
