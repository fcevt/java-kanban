package com.yandex.tracker.model;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class Task implements Comparable<Task> {
    public static final LocalDateTime DEFAULT_TIME = LocalDateTime.MAX;
    public static final Duration DEFAULT_DURATION = Duration.ZERO;
    private String name;
    private String description;
    private int id;
    private TaskStatus status;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String description, String name) {
        this.status = TaskStatus.NEW;
        this.id = 0;
        this.description = description;
        this.name = name;
        this.duration = DEFAULT_DURATION;
        this.startTime = DEFAULT_TIME;
    }

    //конструктор для задач без указания времени
    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = DEFAULT_DURATION;
        this.startTime = DEFAULT_TIME;
    }

    public Task(int id, String name, String description, TaskStatus status, LocalDateTime startTime,
                Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String toStringToSave() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return String.format("%d,%s,%s,%s,%s,%s,%d", id, TaskType.TASK, name, description, status,
                startTime.format(formatter), duration.toMinutes());
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(Task task) {
        if (this.startTime.isBefore(task.startTime)) {
            return -1;
        } else if (this.startTime.isEqual(task.startTime)) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name.length() + '\'' +
                ", description='" + description.length() + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime +
                '}';
    }

}
