package tasks;

import enumeration.Status;
import enumeration.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Integer id;
    private Status status;
    private LocalDateTime startTime;
    private Duration duration;


    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    public Task(String title, String description, Integer id, Status status,LocalDateTime startTime,Duration duration) {
        this.name = title;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;

    }


    public LocalDateTime getEndTime(){ //расчет окончания работы задачи
        if((startTime==null)||(duration == null)){
            return null;
        }
        return startTime.plus(duration);
    }



    public Task(Status status) {
        this.status = status;
    }

    public TypeTask getType() {
        return TypeTask.TASK;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s", id, getType(), name, status, description, startTime.format(DATE_TIME_FORMATTER), getEndTime().format(DATE_TIME_FORMATTER));
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
