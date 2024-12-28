package com.yandex.tracker.model;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> listOfSubtasks;

    public Epic(String description, String name) {
        super(description, name);
        listOfSubtasks = new ArrayList<>();
    }

    public Epic(int id, String name, String description, TaskStatus status, ArrayList<Integer> listOfSubtasks) {
        super(id, name, description, status);
        this.listOfSubtasks = listOfSubtasks;
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
    public String toStringToSave() {
        return String.format("%d,%s,%s,%s,%s,%s", getId(), TaskType.EPIC, getName(), getDescription(), getStatus(),
                listOfSubtasksToString());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName().length() + '\'' +
                ", description='" + getDescription().length() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", listOfSubtasks=" + listOfSubtasks +
                '}';
    }
}
