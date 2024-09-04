package tasks;

public class SubTask extends Task {

    public int epicId;

    public SubTask(int epicSubTask, String title, String description, Integer id, enumeration.Status status) {
        super(title, description, id, status);
        this.epicId = epicSubTask;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "Title='" + title + '\'' +
                ", Description='" + description + '\'' +
                ", id=" + id +
                ", Status=" + status +
                '}';
    }

    public int getEpicId() {
        return epicId;
    }
}
