package manager.History;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {
    // ----------------------------------- история просмотра последних 10 задач по id -------------------------------------


    void add(Task task);

    default List<Task> getHistory() {
        return new ArrayList<>();
    }

    void remove(int id);


}
