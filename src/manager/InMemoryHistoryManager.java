package manager;
import tasks.Task;

import java.util.ArrayList;


public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history = new ArrayList<>(10);

    @Override
    public void setHistory(Task task) {// запись просмотренной задачи по id
        int maxCount = 10;// максимальная длинна списка

        if (task != null ){
            if (history.size() < maxCount) {//если длинна списка меньше максимальной длинны, то идет запись
                this.history.add(task);
            } else {// если равна максимальной длине, то удаляется первый элемент списка и идет запись
                this.history.removeFirst();
                this.history.add(task);
            }
        }
    }

    @Override
    public ArrayList<Task> getHistory() {

        return history;
    }


}
