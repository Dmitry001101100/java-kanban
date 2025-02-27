import server.HttpTaskServer;
import manager.Managers;
import manager.Task.TaskManager;

import java.io.File;
import java.io.IOException;

class Main {
    public static void main(String[] arf) throws IOException {
        // перенос запуска сервера в метод маин
        final int PORT = 8080;
        final File file = new File("taskToList.csv"); // используется для проверки
        final TaskManager taskManager = Managers.getDefaultFileBackedTaskManager(file);

        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.startServer(PORT, taskManager); // запускаем сервер
    }
}