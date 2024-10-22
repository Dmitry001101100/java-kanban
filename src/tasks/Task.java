package tasks;

import enumeration.Status;
import enumeration.TypeTask;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Integer id;
    private Status status;

    public Task(String title, String description, Integer id, Status status) {
        this.name = title;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(Status status) {
        this.status = status;
    }

    public TypeTask getType() {
        return TypeTask.TASK;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s", id, getType(), name, status, description);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {

        return status;

    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(id, task.id) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    public String getName() {
        return name;
    }
}
