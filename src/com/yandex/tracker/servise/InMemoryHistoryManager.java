package com.yandex.tracker.servise;

import com.yandex.tracker.model.Node;
import com.yandex.tracker.model.Task;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;
    private final Map<Integer, Node> mapForDelete;


    public InMemoryHistoryManager() {
        mapForDelete = new HashMap<>();
    }

    //добавление просмотренных тасков в конец истории просмотров
    Node linkLast(Task task) {
        Node node = new Node(task, tail, null);
        if (head == null) {
            head = node;
        } else {
            tail.setNext(node);
        }
        tail = node;
        return node;
    }

    //перекладывание истории из связного списка в ArrayList
    List<Task> getTasks() {
        List<Task> browsingHistory = new ArrayList<>();
        Node  node = head;
        while (node != null) {
            browsingHistory.add(node.getData());
            node = node.getNext();
        }
        return browsingHistory;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (!mapForDelete.containsKey(id)) {
            return;
        }
        removeNode(mapForDelete.get(id));
        mapForDelete.remove(id);
    }

    //удаление ноды в зависимости от ее положения вв списке
    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        if (tail == node && head == node) {
            head = null;
            tail = null;
        } else if (node == tail) {
            tail = node.getPrev();
            tail.setNext(null);
        } else if (node == head) {
            head = node.getNext();
            head.setPrev(null);
        } else {
            Node prevNode = node.getPrev();
            Node nextNode = node.getNext();
            prevNode.setNext(nextNode);
            nextNode.setPrev(prevNode);
        }
    }

    //добавление нода в список и в мапу
    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        removeNode(mapForDelete.remove(task.getId()));
        mapForDelete.put(task.getId(), linkLast(task));
    }
}
