package manager.History;

import tasks.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();
    private Node<Task> head = null;
    private Node<Task> tail = null;

    private static class Node<T> {
        private T data;
        private Node<T> next;
        private Node<T> prev;

        private Node(T data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    private Node<Task> linkLast(Task task) {
        Node<Task> newNode = new Node<>(task);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        return tail;
    }

    private List<Task> getTasks() {
        List<Task> tasksArr = new ArrayList<>();
        Node<Task> iterator = head;
        while (iterator != null) {
            tasksArr.add(iterator.data);
            iterator = iterator.next;
        }
        return tasksArr;
    }

    private void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        historyMap.put(task.getId(), linkLast(task));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            removeNode(historyMap.get(id));
            historyMap.remove(id);
        }
    }

    @Override
    public void clear() {
        historyMap.clear();
        head = null;
        tail = null;
    }
}