package manager.History;
import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();


    private static class Node<T> {

        private  T data;
        private Node<T> next;
        private Node<T> prev;

        private Node(T data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    private Node<Task> head;
    private Node<Task> tail;



    private Node<Task> linkLast(Task task) {
        if (head == null) {
            head = new Node<>(task);
            tail = head;
        } else {
            Node<Task> oldTail = tail;
            tail = new Node<>(task);
            tail.prev = oldTail;
            oldTail.next = tail;
        }

        return tail;
    }

    private List<Task> getTasks() {
        List<Task> tasksArr = new ArrayList<>();
        Node<Task> iterator = head;
        for (int i = 0; i < historyMap.size(); i++) {
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
        } else { //Это head
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else { //Это tail
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
    }


}
