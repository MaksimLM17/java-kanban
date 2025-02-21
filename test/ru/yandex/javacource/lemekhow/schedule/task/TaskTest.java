package ru.yandex.javacource.lemekhow.schedule.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.lemekhow.schedule.manager.Managers;
import ru.yandex.javacource.lemekhow.schedule.manager.TaskManager;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task task;

    @BeforeEach
    void beforeEach() {
        TaskManager taskManager = Managers.getDefault();
        task = new Task("Task", "Clean home", 1, Status.NEW,
                LocalDateTime.of(2025, 2, 21, 6, 20), Duration.ofMinutes(15));
    }

    @Test
    void getID() {
        Integer id = task.getId();
        Integer expected = 1;
        assertEquals(expected, id, "Значения не совпадают");
    }

    @Test
    void getNameTask() {
        String nameTask = task.getName();
        String expected = "Task";
        assertEquals(expected, nameTask, "Имена не совпадают");
    }

    @Test
    void getDescription() {
        String description = task.getDescription();
        String expected = "Clean home";
        assertEquals(expected, description, "Описание не совпадают");
    }

    @Test
    void getStatus() {
        Status status = task.getStatus();
        Status expected = Status.NEW;
        assertEquals(expected, status, "Статусы не совпадают");
    }

    @Test
    void getStartTime() {
        LocalDateTime startTimeReceive = task.getStartTime();
        LocalDateTime startTime = LocalDateTime.of(2025, 2, 21, 6, 20);

        assertEquals(startTime, startTimeReceive, "Время начала не совпадает");

    }

    @Test
    void getDuration() {
        Duration durationReceive = task.getDuration();
        Duration duration = Duration.ofMinutes(15);

        assertEquals(duration, durationReceive, "Продолжительность не совпадает");
    }

    @Test
    void getEndTime() {
        LocalDateTime endTimeReceive = task.getEndTime();
        LocalDateTime endTime = LocalDateTime.of(2025, 2, 21, 6, 20).plusMinutes(15);

        assertEquals(endTime, endTimeReceive, "Время окончания не совпадает");
    }

    @Test
    void setId() {
        Integer setupId = 2;
        Integer expected = 2;
        task.setId(setupId);
        Integer taskId = task.getId();

        assertEquals(expected, taskId, "Значения не совпадают");
    }

    @Test
    void setName() {
        String setupName = "Cleaning";
        task.setName(setupName);
        String taskName = task.getName();

        assertEquals(setupName, taskName, "Имена не совпадают");
    }

    @Test
    void setDescription() {
        String setupDescription = "Clean window";
        task.setDescription(setupDescription);
        String taskDescription = task.getDescription();

        assertEquals(setupDescription, taskDescription, "Описания не совпадают");
    }

    @Test
    void setStatus() {
        Status setupStatus = Status.IN_PROGRESS;
        task.setStatus(setupStatus);
        Status taskStatus = task.getStatus();

        assertEquals(setupStatus, taskStatus, "Статусы не совпадают");
    }

    @Test
    void setStartTime() {
        LocalDateTime setupStartTime = LocalDateTime.of(2025, 2, 20, 8, 20);
        task.setStartTime(setupStartTime);
        LocalDateTime startTimeTask = task.getStartTime();

        assertEquals(startTimeTask, setupStartTime, "Измененное время начала не совпадает");
    }

    @Test
    void setDuration() {
        Duration setupDuration = Duration.ofMinutes(25);
        task.setDuration(setupDuration);
        Duration durationTask = task.getDuration();

        assertEquals(durationTask, setupDuration, "Продолжительность после изменения не совпадает");
    }

    @Test
    void equals() {
        Task task1 = new Task("task1", "have a dinner",1, Status.IN_PROGRESS );
        boolean actually = task.equals(task1);
        assertTrue(actually, "Объекты не равны");
    }

    @Test
    void testHashCode() {
        Task task1 = new Task("task1", "have a dinner",1, Status.IN_PROGRESS );
        int hashCode = task.hashCode();
        int hashCode1 = task1.hashCode();

        assertEquals(hashCode, hashCode1, "Хеш-коды не равны");
    }

    @Test
    void testToString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String expected = "Task{" +
                "name='" + task.getName() + '\'' +
                ", description='" + task.getDescription() + '\'' +
                ", id=" + task.getId() +
                ", status=" + task.getStatus() +
                ", startTime=" + task.getStartTime().format(formatter) +
                ", duration=" + task.getDuration().toHours() + "ч" + task.getDuration().toMinutesPart() + "мин" +
                ", endTime=" + task.getEndTime().format(formatter) +
                '}';
        String taskToString = task.toString();

        assertEquals(expected, taskToString, "toString не равен");
    }
}