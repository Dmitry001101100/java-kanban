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

    public Task(String title, String description, Integer id, Status status, LocalDateTime startTime, Duration duration) {
        this.name = title;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;

    }
    //------------------------------------ время -----------------------------------------------------------------------
    public LocalDateTime getStartTime() {
        return startTime;
    }


    public LocalDateTime getEndTime() { // расчет окончания работы задачи
        if(duration == null){
            return startTime;
        } else if (startTime != null) {
            return startTime.plus(duration);
        }else{
            return null;
        }
    }

    public String toFormat(LocalDateTime time){ // метод создан для обработки случаев если пользователь ввел значение null
        // если же все введено верно то метод выводит время и дату в нужном формате
        if(time == null){
            return "null";
        }
        return time.format(DATE_TIME_FORMATTER);
    }

    // -----------------------------------------------------------------------------------------------------------------
    public Task(String title, String description, Integer id, Status status) {
        this.status = status;
    }

    public TypeTask getType() {
        return TypeTask.TASK;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s", id, getType(), name, status, description,
                toFormat(startTime),
                toFormat(getEndTime()));
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

    public String getName() {
        return name;
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(id,
                task.id) && status == task.status && Objects.equals(startTime, task.startTime)
                && Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, startTime, duration);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
