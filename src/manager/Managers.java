package manager;

import manager.History.HistoryManager;
import manager.History.InMemoryHistoryManager;
import manager.Task.FileBackedTaskManager;
import manager.Task.InMemoryTaskManager;
import manager.Task.TaskManager;

import java.io.File;


public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultFileBackedTaskManager(File file){
        return new FileBackedTaskManager(file);
    }
}
