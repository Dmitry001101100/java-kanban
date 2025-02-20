package manager;

import HttpTaskServer.adapters.DurationAdapter;
import HttpTaskServer.adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.History.HistoryManager;
import manager.History.InMemoryHistoryManager;
import manager.Task.FileBackedTaskManager;
import manager.Task.InMemoryTaskManager;
import manager.Task.TaskManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;


public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(file);
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        //  gsonBuilder.serializeNulls();
        return gsonBuilder.create();
    }

}
