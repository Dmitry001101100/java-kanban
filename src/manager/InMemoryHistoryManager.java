package manager;

import tasks.Task;

import java.util.ArrayList;


public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history = new ArrayList<>(10);
    //  private static final int maxCount = 10;// максимальная длинна списка

    @Override
    public void addTaskHis(Task task) {// запись просмотренной задачи по id


        if (history.contains(task)) {
            history.remove(task);

        }

        history.add(task);


    }

    @Override
    public ArrayList<Task> getHistory() {

        return history;
    }

    @Override
    public void remove(int id) {// добавили удаление по id

        history.remove((id - 1));
    }


}
