package manager;
import tasks.Task;

import java.util.ArrayList;

public interface HistoryManager {
    // ----------------------------------- история просмотра последних 10 задач по id -------------------------------------

    void setHistory(Task task);

    default ArrayList<Task> getHistory(){
        return new ArrayList<>();
    }



}
