package ru.yandex.javacource.lemekhow.schedule.Exception;

public class ManagerInitializationException extends RuntimeException {
    public ManagerInitializationException(String message, Exception e) {
        super(message);
    }
}
