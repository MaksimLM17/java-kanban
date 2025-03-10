package ru.yandex.javacource.lemekhow.schedule.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.lemekhow.schedule.Server.HttpTaskServer;
import ru.yandex.javacource.lemekhow.schedule.manager.Managers;
import ru.yandex.javacource.lemekhow.schedule.manager.TaskManager;
import ru.yandex.javacource.lemekhow.schedule.task.Epic;
import ru.yandex.javacource.lemekhow.schedule.task.Status;
import ru.yandex.javacource.lemekhow.schedule.task.Subtask;
import ru.yandex.javacource.lemekhow.schedule.task.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private TaskManager manager = Managers.getDefault();
    private HttpTaskServer server = new HttpTaskServer(manager);
    private Gson gson = Managers.getDefaultGson();
    private final HttpClient client = HttpClient.newHttpClient();

    HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    void setup() {
        manager.removeAllTask();
        manager.removeAllEpic();
        manager.removeAllSubtask();
        server.startServer();
    }

    @AfterEach
    void shutDown() {
        server.stopServer(1);
    }

    @Test
    void addTask() throws IOException, InterruptedException {
        Task task = new Task("name", "fff", Status.NEW,
                LocalDateTime.of(2014,8, 15,14, 20),
                Duration.ofMinutes(356));
        String jsonTask = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .header("Content-type","application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не совпадает");

        List<Task> managerFromServer = manager.getTasks();

        assertNotNull(managerFromServer, "Задачи не созданы");
        assertEquals(managerFromServer.size(), 1, "Некорректное количество задач");
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        Task task = new Task("name", "fff", Status.NEW,
                LocalDateTime.of(2014,8, 15,14, 20),
                Duration.ofMinutes(356));
        Task task1 = new Task("Имя", "sss",1, Status.DONE,
                LocalDateTime.of(2014,8, 15,14, 20),
                Duration.ofMinutes(35));
        manager.createTask(task);

        String jsonUpTask = gson.toJson(task1);
        URI urlUp = URI.create("http://localhost:8080/tasks/1");
        HttpRequest requestUpdate = HttpRequest.newBuilder()
                .uri(urlUp)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUpTask))
                .header("Content-type","application/json")
                .build();
        HttpResponse<String> responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseUpdate.statusCode(), "Код ответа не совпадает");

        List<Task> managerFromServer = manager.getTasks();

        assertNotNull(managerFromServer, "Задачи отсутствуют");
        assertEquals(managerFromServer.size(), 1, "Некорректное количество задач");
        assertEquals(managerFromServer.get(0).getName(), "Имя", "Имена не совпадают.");
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        Task task = new Task("name", "fff", Status.NEW,
                LocalDateTime.of(2014,8, 15,14, 20),
                Duration.ofMinutes(356));
        Task task1 = new Task("Имя", "sss",1, Status.DONE,
                LocalDateTime.of(2025,8, 15,14, 20),
                Duration.ofMinutes(35));
        manager.createTask(task);
        manager.createTask(task1);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> tasks = gson.fromJson(response.body(), taskType);

        assertEquals(200, response.statusCode(), "Код не совпадает");
        assertEquals(2, tasks.size(), "Некорректное количество задач");

    }

    @Test void getTaskId() throws IOException, InterruptedException {
        Task task = new Task("name", "fff", Status.NEW,
                LocalDateTime.of(2014,8, 15,14, 20),
                Duration.ofMinutes(356));
        Task task1 = new Task("Имя", "sss",1, Status.DONE,
                LocalDateTime.of(2025,8, 15,14, 20),
                Duration.ofMinutes(35));
        manager.createTask(task);
        manager.createTask(task1);

        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskFromResponse = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode(), "Код не совпадает");
        assertEquals("Имя", taskFromResponse.getName(), "Имена не совпадают");
    }

    @Test
    void removeTaskId() throws IOException, InterruptedException {
        Task task = new Task("name", "fff", Status.NEW,
                LocalDateTime.of(2014,8, 15,14, 20),
                Duration.ofMinutes(356));
        Task task1 = new Task("Имя", "sss",1, Status.DONE,
                LocalDateTime.of(2025,8, 15,14, 20),
                Duration.ofMinutes(35));
        manager.createTask(task);
        manager.createTask(task1);

        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код не совпадает");

        List<Task> tasks = manager.getTasks();

        assertEquals(1, tasks.size(),"Некорректное количество задач");
    }

    @Test
    void addEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW,
                LocalDateTime.of(2025, 2, 21, 9, 53), Duration.ofMinutes(50),
                LocalDateTime.of(2025,1,1,1,0));
        String jsonEpic = gson.toJson(epic);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .header("Content-type","application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не совпадает");

        List<Epic> managerFromServer = manager.getEpics();

        assertNotNull(managerFromServer, "Эпики не созданы");
        assertEquals(managerFromServer.size(), 1, "Некорректное количество эпиков");
    }

    @Test
    void updateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW,
                LocalDateTime.of(2025, 2, 21, 9, 53), Duration.ofMinutes(50),
                LocalDateTime.of(2025,1,1,1,0));
        int id = manager.createEpic(epic);
        Epic epic1 = new Epic("newEpic", "Cleaning", id, Status.NEW,
                LocalDateTime.of(2025, 2, 21, 9, 53), Duration.ofMinutes(80),
                LocalDateTime.of(2025,1,1,1,0));
        String jsonEpic = gson.toJson(epic1);
        URI url = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .header("Content-type","application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не совпадает");
        assertEquals("newEpic", manager.getEpicId(id).getName(),"Имена не совпадают");
    }

    @Test
    void getEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW,
                LocalDateTime.of(2025, 2, 21, 9, 53), Duration.ofMinutes(50),
                LocalDateTime.of(2025,1,1,1,0));
        Epic epic1 = new Epic("newEpic", "Go to the park", Status.NEW,
                LocalDateTime.of(2025, 2, 21, 9, 53), Duration.ofMinutes(80),
                LocalDateTime.of(2025,1,1,1,0));
        manager.createEpic(epic);
        manager.createEpic(epic1);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type epicType = new TypeToken<ArrayList<Epic>>() {}.getType();
        List<Epic> epicsFromResponse = gson.fromJson(response.body(), epicType);

        assertEquals(200, response.statusCode(),"Код ответа не совпадает");
        assertEquals(2, epicsFromResponse.size(),"Некорректное количество эпиков");
    }

    @Test
    void getEpicId() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW,
                LocalDateTime.of(2025, 2, 21, 9, 53), Duration.ofMinutes(50),
                LocalDateTime.of(2025,1,1,1,0));
        Epic epic1 = new Epic("newEpic", "Go to the park", Status.NEW,
                LocalDateTime.of(2025, 2, 21, 9, 53), Duration.ofMinutes(80),
                LocalDateTime.of(2025,1,1,1,0));
        int id = manager.createEpic(epic);
        manager.createEpic(epic1);

        URI url = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicFromResponse = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode(),"Код ответа не совпадает");
        assertEquals("Cleaning", epicFromResponse.getDescription(), "Описание эпика не совпадает");
    }

    @Test
    void removeEpicId() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW,
                LocalDateTime.of(2025, 2, 21, 9, 53), Duration.ofMinutes(50),
                LocalDateTime.of(2025,1,1,1,0));
        Epic epic1 = new Epic("newEpic", "Go to the park", Status.NEW,
                LocalDateTime.of(2025, 2, 21, 9, 53), Duration.ofMinutes(80),
                LocalDateTime.of(2025,1,1,1,0));
        int id = manager.createEpic(epic);
        manager.createEpic(epic1);
        URI url = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Код ответа не совпадает");

        int actualSize = manager.getEpics().size();

        assertEquals(1, actualSize, "Некорректное количество эпиков, после удаления");
    }

    @Test
    void getSubtasksEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW,
                LocalDateTime.of(2025, 2, 21, 9, 53), Duration.ofMinutes(50),
                LocalDateTime.of(2025,1,1,1,0));
        int idEpic = manager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));
        Subtask subtask1 = new Subtask(idEpic, "Subtask1", "descrip", Status.DONE,
                LocalDateTime.of(2025, 2, 20, 14, 30), Duration.ofMinutes(190));
        manager.createSubtask(subtask);
        manager.createSubtask(subtask1);

        URI url = URI.create(String.format("http://localhost:8080/epics/%d/subtasks", idEpic));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type subtaskType = new TypeToken<ArrayList<Subtask>>() {}.getType();
        ArrayList<Subtask> subtasksByEpic = gson.fromJson(response.body(), subtaskType);

        assertEquals(200, response.statusCode(),"Код ответа не совпадает");
        assertEquals(2, subtasksByEpic.size(),"Некорректное количество подзадач");
    }

    @Test
    void createSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        int idEpic = manager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));
        String jsonSubtask = gson.toJson(subtask);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask))
                .header("Content-type","application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(),"Код ответа не совпадает");
        assertNotNull(manager.getEpicId(idEpic).getEndTime(),"Время окончания эпика не вернулось");
        assertNotNull(manager.getSubtasks(), "Список подзадач пуст");
    }

    @Test
    void updateSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        int idEpic = manager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));
        int id = manager.createSubtask(subtask);
        Subtask subtask1 = new Subtask(idEpic, "Subtask1", "descrip", id, Status.DONE,
                LocalDateTime.of(2025, 2, 20, 14, 30), Duration.ofMinutes(190));
        String jsonSubtask = gson.toJson(subtask1);
        URI url = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask))
                .header("Content-type","application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(),"Код ответа не совпадает");
        assertEquals("Subtask1", manager.getSubtaskId(id).getName(), "Имена не совпадают");

    }

    @Test
    void getSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        int idEpic = manager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));
        Subtask subtask1 = new Subtask(idEpic, "Subtask1", "descrip", Status.DONE,
                LocalDateTime.of(2025, 2, 20, 14, 30), Duration.ofMinutes(190));
        manager.createSubtask(subtask);
        manager.createSubtask(subtask1);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type subtaskType = new TypeToken<ArrayList<Subtask>>() {}.getType();
        ArrayList<Subtask> subtasks = gson.fromJson(response.body(), subtaskType);

        assertEquals(200, response.statusCode(),"Код ответа не совпадает");
        assertEquals(2, subtasks.size(),"Некорректное количество подзадач");
    }

    @Test
    void getSubtaskId() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        int idEpic = manager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));
        Subtask subtask1 = new Subtask(idEpic, "Subtask1", "descrip", Status.DONE,
                LocalDateTime.of(2025, 2, 20, 14, 30), Duration.ofMinutes(190));
        int id = manager.createSubtask(subtask);
        manager.createSubtask(subtask1);
        URI url = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtaskFromResponse = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode(),"Код ответа не совпадает");
        assertEquals("subtask", subtaskFromResponse.getName(), "Имена не совпадают");
    }

    @Test
    void removeSubtaskId() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        int idEpic = manager.createEpic(epic);
        Subtask subtask = new Subtask(idEpic, "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));
        Subtask subtask1 = new Subtask(idEpic, "Subtask1", "descrip", Status.DONE,
                LocalDateTime.of(2025, 2, 20, 14, 30), Duration.ofMinutes(190));
        int id = manager.createSubtask(subtask);
        manager.createSubtask(subtask1);
        URI url = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Код ответа не совпадает");
        assertEquals(1, manager.getSubtasks().size(), "Некорректное количество подзадач");
    }

    @Test
    void taskIntersectionTest() throws IOException, InterruptedException {
        Task task = new Task("name", "fff", Status.NEW,
                LocalDateTime.of(2025,3, 9,14, 20),
                Duration.ofMinutes(356));
        manager.createTask(task);
        Task task1 = new Task("Имя", "sss",1, Status.DONE,
                LocalDateTime.of(2025,3, 9,14, 30),
                Duration.ofMinutes(35));
        String taskJson = gson.toJson(task1);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Код ответа не совпадает");
        assertEquals(1, manager.getTasks().size(),"Некорректное количество задач");
    }

    @Test
    void getIsEmptyList() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Код ответа не совпадает");
    }

    @Test
    void getNonExistTask() throws IOException, InterruptedException {
        Task task = new Task("name", "fff", Status.NEW,
                LocalDateTime.of(2025,3, 9,14, 20),
                Duration.ofMinutes(356));
        manager.createTask(task);
        URI url = URI.create("http://localhost:8080/tasks/3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(500, response.statusCode(), "Код ответа не совпадает");
    }

    @Test
    void incorrectId() throws IOException, InterruptedException {
        Task task = new Task("name", "fff", Status.NEW,
                LocalDateTime.of(2025,3, 9,14, 20),
                Duration.ofMinutes(356));
        manager.createTask(task);
        URI url = URI.create("http://localhost:8080/tasks/s1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Код ответа не совпадает");
    }

    @Test
    void incorrectCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Cleaning", Status.NEW);
        int idEpic = manager.createEpic(epic);
        Subtask subtask = new Subtask(4, "subtask", "sub",3, Status.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 18, 10, 30), Duration.ofMinutes(185));
        String jsonSubtask = gson.toJson(subtask);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask))
                .header("Content-type","application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(),"Код ответа не совпадает");
    }

    @Test
    void incorrectMethod() throws IOException, InterruptedException {
        Task task = new Task("name", "fff", Status.NEW,
                LocalDateTime.of(2025,3, 9,14, 20),
                Duration.ofMinutes(356));
        String taskJson = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode(), "Код ответа не совпадает");
    }
}
