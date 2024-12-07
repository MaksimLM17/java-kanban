package Manager;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds;
    public Epic(String nameTask, String description,Integer id, Status status){
        super(nameTask,description,id,status);
        subtaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }
    public void removeSubtaskIds(Integer subtaskId){
        subtaskIds.remove(subtaskId);
    }
    public void clearSubtaskIds(){
        subtaskIds.clear();
    }

}
