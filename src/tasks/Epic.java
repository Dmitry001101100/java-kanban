package tasks;

import enumeration.Status;

import java.util.ArrayList;

public class Epic extends Task {

    public ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String title, String description, Integer id, enumeration.Status status) { // без списка
        super(title, description, id, status);
    }

    public Epic(String title, String description, Integer id, Status status, ArrayList<Integer> subtaskIds) { // со списком
        super(title, description, id, status);
        this.subtaskIds = subtaskIds;
    }


    public void addSubtaskIds(Integer id) { // ложим id subtaska в лист subtaskIds
        subtaskIds.add(id);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void removeSubtaskIds(Integer id) { // удаляем id subtaska из списка subtaskIds
        subtaskIds.remove(id);
    }


    @Override
    public String toString() {
        String result = "Epic{" +
                "name='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status;

        return result + '}';
    }


}