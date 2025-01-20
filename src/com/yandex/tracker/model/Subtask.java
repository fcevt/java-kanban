package com.yandex.tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description) {
        super(name, description);
        epicId = 0;
    }

    public Subtask(int id, String name, String description, TaskStatus status, LocalDateTime startTime,
                   Duration duration, int epicId) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toStringToSave() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return String.format("%d,%s,%s,%s,%s,%s,%d,%d", getId(), TaskType.SUBTASK, getName(), getDescription(),
                getStatus(), getStartTime().format(formatter), getDuration().toMinutes(), getEpicId());
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", name='" + getName().length() + '\'' +
                ", description='" + getDescription().length() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                '}';
    }
}
