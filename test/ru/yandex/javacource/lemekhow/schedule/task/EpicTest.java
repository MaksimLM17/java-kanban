package ru.yandex.javacource.lemekhow.schedule.task;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.lemekhow.schedule.manager.Managers;
import ru.yandex.javacource.lemekhow.schedule.manager.TaskManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void beforeEach() {
        epic = new Epic("Epic", "Going", 1, Status.NEW);
        subtask = new Subtask(1, "Subtask", "go to the park", 2, Status.IN_PROGRESS);
    }

    @Test
    void getSubtaskIds() {
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(subtask.getId());
        epic.addSubtaskId(subtask.getId());
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();

        assertEquals(expected, subtaskIds, "Списки не равны");
    }

    @Test
    void addSubtaskId() {
        epic.addSubtaskId(subtask.getId());
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();

        assertTrue(subtaskIds.contains(subtask.getId()), "Id не найдено");
    }

    @Test
    void removeSubtaskIds() {
        epic.addSubtaskId(subtask.getId());
        epic.removeSubtaskIds(subtask.getId());
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();

        assertTrue(subtaskIds.isEmpty(), "Список не пуст");
    }

    @Test
    void clearSubtaskIds() {
        Subtask sub = new Subtask(1, "Subtask", "go to the park", 3, Status.IN_PROGRESS);

        epic.addSubtaskId(subtask.getId());
        epic.addSubtaskId(sub.getId());
        epic.clearSubtaskIds();
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();

        assertTrue(subtaskIds.isEmpty(), "Список не пуст");
    }

    @Test
    void testToString() {
        String expected = "Epic{" + "name='" + epic.getName() + '\'' + ", description='" +
                epic.getDescription() + '\'' + ", id=" + epic.getId() + ", status=" + epic.getStatus() +
                ", subtasksIds=" +  epic.getSubtaskIds() + "}";
        String epicToString = epic.toString();

        assertEquals(expected, epicToString, "toString не равен");
    }



}