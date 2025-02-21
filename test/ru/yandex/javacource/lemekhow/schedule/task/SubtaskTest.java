package ru.yandex.javacource.lemekhow.schedule.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private Subtask subtask;

    @BeforeEach
    void beforeEach() {
        subtask = new Subtask(2, "subtask", "sub", Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));
    }

    @Test
    void getEpicId() {
        Integer expected = 2;
        Integer actually = subtask.getEpicId();
        assertEquals(expected, actually, "Эпик айди не равны");
    }

    @Test
    void setEpicId() {
        Integer setupEpicId = 4;
        subtask.setEpicId(setupEpicId);
        Integer subtaskEpicId = subtask.getEpicId();

        assertEquals(setupEpicId, subtaskEpicId, "Эпик айди не равны после сеттера");
    }

    @Test
    void testToString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String expected = "Subtask{" +
                "epicId=" + subtask.getEpicId() +
                ", name='" + subtask.getName() + '\'' +
                ", description='" + subtask.getDescription() + '\'' +
                ", id=" + subtask.getId() +
                ", status=" + subtask.getStatus() +
                ", startTime=" + subtask.getStartTime().format(formatter) +
                ", duration=" + subtask.getDuration().toHours() +"ч" + subtask.getDuration().toMinutesPart() + "мин" +
                ", endTime=" + subtask.getEndTime().format(formatter) +
                '}';
        String subtaskToString = subtask.toString();

        assertEquals(expected, subtaskToString, "toString не корректен");
    }
}