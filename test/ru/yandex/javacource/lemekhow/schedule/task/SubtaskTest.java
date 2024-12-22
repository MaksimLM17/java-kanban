package ru.yandex.javacource.lemekhow.schedule.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private Subtask subtask;

    @BeforeEach
    void beforeEach() {
        subtask = new Subtask(1, "Subtask", "go to the park", 2, Status.IN_PROGRESS);
    }

    @Test
    void getEpicId() {
        Integer expected = 1;
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
        String expected = "Subtask{" + "epicId=" + subtask.getEpicId() +  ", name='" + subtask.getName() + '\''
                + ", description='" + subtask.getDescription() + '\''
                + ", id=" + subtask.getId() + ", status=" + subtask.getStatus() + "}";
        String subtaskToString = subtask.toString();

        assertEquals(expected, subtaskToString, "toString не корректен");
    }
}