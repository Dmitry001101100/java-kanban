package manager;

import manager.History.HistoryManager;
import manager.History.InMemoryHistoryManager;
import manager.Task.InMemoryTaskManager;
import manager.Task.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
