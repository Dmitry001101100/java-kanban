package tasks;

import enumeration.Status;
import enumeration.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description, Integer id, enumeration.Status status, LocalDateTime startTime, Duration duration) { // без списка
        super(title, description, id, status, startTime, duration);
    }


    public void clearSubtaskIds() {
        subtaskIds.clear();
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
    public TypeTask getType() {
        return TypeTask.EPIC;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s",
                getId(),
                getType(),
                getName(),
                getStatus(),
                getDescription(),
                toFormat(getStartTime()),
                toFormat(endTime));
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}