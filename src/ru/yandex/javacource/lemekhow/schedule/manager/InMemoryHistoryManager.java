package ru.yandex.javacource.lemekhow.schedule.manager;

import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> historyTask = new HashMap<>();
    private Node first;
    private Node last;
    
    private static class Node {
        public Task data;
        public Node next;
        public Node prev;

        public Node(Node prev, Task data, Node next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
        
        public Node() {
            
        }

    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        addTaskMap(task);
    }

    @Override
    public void remove(int id){
        if(historyTask.containsKey(id) ){
            Node node = historyTask.get(id);
            removeNode(node);
            historyTask.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void addTaskMap(Task task) {
        Node node = new Node(null, task, null);
        if(first == null) {
            first = node;
            historyTask.put(task.getId(), node);
        } else if(last == null) {
            last = node;
            node.prev = first;
            Node firstNode = first;
            firstNode.next = node;
            historyTask.put(task.getId(), node);
        } else {
            node.prev = last;
            Node lastNode = last;
            lastNode.next = node;
            last = node;
            historyTask.put(task.getId(), node);
        }
    }

    private void removeNode(Node node) {
        Node nextNode = node.next;
        Node prevNode = node.prev;
        if (prevNode == null) {
            first = nextNode;
        } else {
            prevNode.next = nextNode;
            node.prev = null;
        }

        if (nextNode == null) {
            last = prevNode;
        } else {
            nextNode.prev = prevNode;
            node.next = null;
        }
        node.data = null;
    }

    private List<Task> getTasks() {
        ArrayList<Task> history = new ArrayList<>();
        Node currentNode = first;
        while(currentNode != null) {
            history.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return history;
    }
}
