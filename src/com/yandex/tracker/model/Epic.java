package com.yandex.tracker.model;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> listOfSubtasks;

    public Epic(String description, String name) {
        super(description, name);
        listOfSubtasks = new ArrayList<>();
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

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName().length() + '\'' +
                ", description='" + getDescription().length() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", listOfSubtsks=" + listOfSubtasks +
                '}';
    }
}
