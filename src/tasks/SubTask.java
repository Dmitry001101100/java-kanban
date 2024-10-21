package tasks;

import enumeration.TypeTask;

import java.util.Objects;

public class SubTask extends Task {

    public int epicId;

    public SubTask(int epicSubTask, String title, String description, Integer id, enumeration.Status status) {
        super(title, description, id, status);
        this.epicId = epicSubTask;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.SUBTASK;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s", id, getType(), name, status, description,epicId);
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
