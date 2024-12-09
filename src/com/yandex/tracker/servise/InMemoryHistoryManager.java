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
    public int size;
    private Map<Integer, Node> mapForDelete;


    public InMemoryHistoryManager() {
        head = null;
        tail = null;
        size = 0;
        mapForDelete = new HashMap<>();
    }

    //добавление просмотренных тасков в конец истории просмотров
    Node linkLast(Task task) {
        if (tail == null) {
            Node node = new Node(task, null, null);
            head = node;
            tail = node;
            size++;
            return node;
        } else {
            Node oldTail = tail;
            Node newTail = new Node(task, oldTail, null);
            oldTail.setNext(newTail);
            tail = newTail;
            size++;
            return newTail;
        }
    }

    //перекладывание истории из связного списка в ArrayList
    List<Task> getTasks() {
        List<Task> browsingHistory = new ArrayList<>();
        Node  node = head;
        while (node != null){
            browsingHistory.add(node.getData());
            node = node.getNext();
        }
        return browsingHistory;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    //удаление ноды в зависимости от ее положения вв списке
    @Override
    public void removeNode(int id) {
        if (!mapForDelete.containsKey(id)) {
            return;
        }
        if (size == 1) {
            head = null;
            tail = null;
            mapForDelete.remove(id);
            size--;
        } else if (mapForDelete.get(id) == tail) {
            tail = mapForDelete.get(id).getPrev();
            tail.setNext(null);
            mapForDelete.remove(id);
            size--;
        } else if (mapForDelete.get(id) == head) {
            head = mapForDelete.get(id).getNext();
            head.setPrev(null);
            mapForDelete.remove(id);
            size--;
        } else {
            Node prevNode = mapForDelete.get(id).getPrev();
            Node nextNode = mapForDelete.get(id).getNext();
            prevNode.setNext(nextNode);
            nextNode.setPrev(prevNode);
            mapForDelete.remove(id);
            size--;
        }
    }

    //добавление нода в список и в мапу
    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (mapForDelete.containsKey(task.getId())) {
            removeNode(task.getId());
            mapForDelete.put(task.getId(), linkLast(task));
        } else {
            mapForDelete.put(task.getId(), linkLast(task));
        }
    }

}
