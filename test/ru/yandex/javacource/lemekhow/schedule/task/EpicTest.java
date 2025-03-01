package ru.yandex.javacource.lemekhow.schedule.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.lemekhow.schedule.manager.InMemoryTaskManager;
import ru.yandex.javacource.lemekhow.schedule.manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void beforeEach() {
        epic = new Epic("Epic", "Going", 1, Status.NEW);
        subtask = new Subtask(1, "subtask",  "sub", 2, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
    }

    @Test
    void getSubtaskIds() {
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(subtask.getId());
        List<Integer> subtaskIds = epic.getSubtaskIds();

        assertEquals(expected, subtaskIds, "Списки не равны");
    }

    @Test
    void addSubtaskId() {
        epic.addSubtaskId(subtask.getId());
        List<Integer> subtaskIds = epic.getSubtaskIds();

        assertTrue(subtaskIds.contains(subtask.getId()), "Id не найдено");
    }

    @Test
    void removeSubtaskIds() {
        epic.removeSubtaskIds(subtask.getId());
        List<Integer> subtaskIds = epic.getSubtaskIds();

        assertTrue(subtaskIds.isEmpty(), "Список не пуст");
    }

    @Test
    void clearSubtaskIds() {
        Subtask sub = new Subtask(1, "Subtask", "go to the park", 3, Status.IN_PROGRESS);

        epic.addSubtaskId(subtask.getId());
        epic.addSubtaskId(sub.getId());
        epic.clearSubtaskIds();
        List<Integer> subtaskIds = epic.getSubtaskIds();

        assertTrue(subtaskIds.isEmpty(), "Список не пуст");
    }

    @Test
    void setEndTime() {
        LocalDateTime setupEndTime = LocalDateTime.of(2025, 2, 21, 8, 56);
        epic.setEndTime(setupEndTime);
        LocalDateTime endTimeEpic = epic.getEndTime();

        assertEquals(endTimeEpic, setupEndTime, "Время окончания эпика после изменения не совпадает");
    }

    @Test
    void testToString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String expected = "Epic{" +
                "name='" + epic.getName() + '\'' +
                ", description='" + epic.getDescription() + '\'' +
                ", id=" + epic.getId() +
                ", status=" + epic.getStatus() +
                ", subtasksIds=" + epic.getSubtaskIds() +
                ", startTime=" + epic.getStartTime().format(formatter) +
                ", duration=" + epic.getDuration().toHours() +"ч" + epic.getDuration().toMinutesPart() + "мин" +
                ", endTime=" + epic.getEndTime().format(formatter) +
                '}';
        String epicToString = epic.toString();

        assertEquals(expected, epicToString, "toString не равен");
    }
}