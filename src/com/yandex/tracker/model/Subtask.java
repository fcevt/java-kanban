package com.yandex.tracker.model;

public class Subtask extends Task {
    private int epicId;


    public Subtask(String description, String name) {
        super(description, name);
        this.epicId = 0;
    }

    public Subtask(int id, String name, String description, TaskStatus status, int epicId) {
        super(id, name, description, status);
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
        return String.format("%d,%s,%s,%s,%s,%s", getId(), TaskType.SUBTASK, getName(), getDescription(), getStatus(),
                getEpicId());
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", name='" + getName().length() + '\'' +
                ", description='" + getDescription().length() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                '}';
    }
}
