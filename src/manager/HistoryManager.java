package manager;
import tasks.Task;

import java.util.ArrayList;

public interface HistoryManager {
    // ----------------------------------- история просмотра последних 10 задач по id -------------------------------------


    void addTaskHis(Task task);

    default ArrayList<Task> getHistory(){
        return new ArrayList<>();
    }

    void remove(int id);



}
