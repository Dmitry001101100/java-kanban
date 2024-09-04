package manager;
import tasks.Task;

import java.util.ArrayList;


public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history = new ArrayList<>(10);
    private static final int maxCount = 10;// максимальная длинна списка // и перенес переменную из метода в класс

    @Override
    public void addTaskHis(Task task) {// запись просмотренной задачи по id    // изменил название


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

    @Override
    public void remove(int id) {// добавили удаление по id
        history.remove(id);
    }


}
