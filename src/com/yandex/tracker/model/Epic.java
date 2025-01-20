package com.yandex.tracker.model;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> listOfSubtasks;
    private LocalDateTime endTime;

    public Epic(String description, String name) {
        super(description, name);
        listOfSubtasks = new ArrayList<>();
        endTime = DEFAULT_TIME;
    }

    //конструктор для эпиков без указания времени
    public Epic(int id, String name, String description, TaskStatus status,
                ArrayList<Integer> listOfSubtasks) {
        super(id, name, description, status);
        this.listOfSubtasks = listOfSubtasks;
        endTime = DEFAULT_TIME;
    }

    public Epic(int id, String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration,
                ArrayList<Integer> listOfSubtasks, LocalDateTime endTime) {
        super(id, name, description, status, startTime, duration);
        this.listOfSubtasks = listOfSubtasks;
        this.endTime = endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void removeListOfSubtasks() {
        listOfSubtasks.clear();
    }

    public void addSubtaskToList(int subtaskId) {
        listOfSubtasks.add(subtaskId);
    }

    public void removeSubtaskFromList(Integer subtaskId) {
        listOfSubtasks.remove(subtaskId);
    }

    public ArrayList<Integer> getListOfSubtasks() {
        return listOfSubtasks;
    }

    private String listOfSubtasksToString() {
        if (listOfSubtasks.isEmpty()) {
            return "-1";
        }
        StringBuilder sb = new StringBuilder();
        for (Integer id : listOfSubtasks) {
            sb.append(id);
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toStringToSave() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return String.format("%d,%s,%s,%s,%s,%s,%d,%s,%s", getId(), TaskType.EPIC, getName(), getDescription(),
                getStatus(), getStartTime().format(formatter), getDuration().toMinutes(), listOfSubtasksToString(),
                endTime.format(formatter));
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName().length() + '\'' +
                ", description='" + getDescription().length() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                ", duration=" + getDuration() +
                ", listOfSubtasks=" + listOfSubtasks +
                '}';
    }
}
