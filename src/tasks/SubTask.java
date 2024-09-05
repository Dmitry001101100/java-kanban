package tasks;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
