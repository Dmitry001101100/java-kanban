package manager;

import enumeration.Status;
import tasks.*;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    //------------------------------------------------------------------------------------------------------------------
    private final Map<Integer, Task> taskMap = new HashMap<>();
    private final Map<Integer, Epic> epicMap = new HashMap<>();
    private final Map<Integer, SubTask> subTaskMap = new HashMap<>();

    HistoryManager managerHis = Managers.getDefaultHistory();

    //-------------------------------Вспомогательные--------------------------------------------------------------------
    int idUp = 0;

    @Override
    public int getIdUp() { // герерирует id
        idUp++;
        return idUp;
    }

    public void updateEpicStatus(int id) { // определение статуса эпика

        Epic epic1 = epicMap.get(id);

        int numNew = 0;
        int numDone = 0;

        if (epicMap.get(id).subtaskIds.isEmpty()) { // если нет подзадач статус New
            epic1.setStatus(Status.NEW);
        }

        for (int i : epic1.subtaskIds) {
            if (subTaskMap.get(i).getStatus() == Status.DONE) { //если статус подзадачи выполнен – то счетчик numDone увеличивается на 1
                numDone++;
            } else if (subTaskMap.get(i).getStatus() == Status.NEW) { //если статус подзадачи новый – то счетчик numNew увеличивается на 1
                numNew++;
            }
        }

        if ((epicMap.get(id).subtaskIds.isEmpty()) || (numNew == epic1.subtaskIds.size())) { // если список подзадач пуст или
            // все задачи имеют статус новый
            epic1.setStatus(Status.NEW); // статус эпика новый

        } else if (numDone == epic1.subtaskIds.size()) { // если все подзадачи имеют статсу выполнен

            epic1.setStatus(Status.DONE); //статус эпика выполнен
        } else {
            epic1.setStatus(Status.IN_PROGRESS);
        }
    }


    //---------------------------- keySeach ----------------------------------------------------------------------------

    @Override
    public ArrayList<Integer> keySetTaskMap() {
        return new ArrayList<>(taskMap.keySet());
    }

    @Override
    public ArrayList<Integer> keySetEpicMap() {
        return new ArrayList<>(epicMap.keySet());
    }

    @Override
    public ArrayList<Integer> keySetSubTaskMap() {
        return new ArrayList<>(subTaskMap.keySet());
    }


    //---------------------------------- 1 - Сохранение ----------------------------------------------------------------

    @Override
    public void saveTask(Task task) { // сохранение и перезапись задач

        taskMap.put(task.id, task);
        System.out.println("Задача успешно сохранена!");

    }

    @Override
    public void saveEpic(Epic epic) { // сохранение и перезапись эпиков

        epicMap.put(epic.id, epic);
        System.out.println("Эпик успешно сохранен!");

    }

    @Override
    public void saveSubTask(SubTask subTask) { // сохранение и перезапись подзадач

        int idEpic = subTask.epicId; // записали id эпика к которому принадлежит подзадача
        int idSub = subTask.id; //записали id подзадачи

        Epic epic1 = epicMap.get(idEpic); // вызываем нужный элемент хеш таблицы

        if (!epic1.subtaskIds.contains(idSub)) {
            epic1.addSubtaskIds(idSub); // записываем в список id подзадач новое значение
        }

        subTaskMap.put(subTask.id, subTask);
        updateEpicStatus(idEpic); //проверка и если требуется изменение статуса эпика
        System.out.println("Подзадача успешно сохранена!");

    }
//------------------------------------------- 2 - Вывод полный ---------------------------------------------------------

    @Override
    public ArrayList<Epic> getEpics() { // кладем значение из мапы эпиков в лист и возвращаем его
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public ArrayList<Task> getTasks() { // кладем значение из мап тасков в лист и возвращаем его
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasksId(int epicId) { // кладем значение из мап подзадач в лист и возвращаем его
        ArrayList<SubTask> subTasksIdArrayList = new ArrayList<>();

        for (int i : epicMap.get(epicId).getSubtaskIds()) { // если в списке есть id подзадачи – то берем на вывод эту подзадачу
            subTasksIdArrayList.add(subTaskMap.get(i));

        }

        return subTasksIdArrayList;
    }

    @Override
    public ArrayList<SubTask> getSubTasks() { // кладем значение из мап тасков в лист и возвращаем его
        return new ArrayList<>(subTaskMap.values());
    }

    //-------------------------------------- 3 - Вывод по id -----------------------------------------------------------
    @Override
    public Task outIdTask(int numberId) { //вывод задачи по id
        managerHis.add(taskMap.get(numberId));
        return taskMap.get(numberId);
    }

    @Override
    public SubTask outIdSubTask(int numberId) { //вывод подзадачи по id
        managerHis.add(subTaskMap.get(numberId));
        return subTaskMap.get(numberId);
    }

    @Override
    public Epic outIdEpic(int numberId) { //вывод эпика по id
        managerHis.add(epicMap.get(numberId));
        return epicMap.get(numberId);
    }

    //--------------------------------------------- 5 - Полное удаление ------------------------------------------------
    @Override
    public void clearContent() { // удалить всё
        clearTasks();
        clearEpics();
        idUp = 0;// обнуляем переменную для id
        System.out.println("Все содержимое полностью очищено.");
    }

    @Override
    public void clearTasks() { // удалить все задачи
        taskMap.clear();
        System.out.println("Задачи полностью удалены.");
    }

    @Override
    public void clearSubTasksOfEpic(int epicId) { // удалить все подзадачи у одного эпика

        for (int i : epicMap.get(epicId).getSubtaskIds()) { // если в списке есть id подзадачи – то удаляем эту подзадачу
            subTaskMap.remove(i);

        }
        epicMap.get(epicId).subtaskIds.clear();
        updateEpicStatus(epicId);
    }

    @Override
    public void clearEpics() { // удалить все эпики и подзадачи к ним относящиеся

        subTaskMap.clear();
        epicMap.clear();
        System.out.println("Эпики и подзадачи к ним относящиеся полностью удалены.");

    }

    @Override
    public void clearSubtasks() {

        for (Integer id : subTaskMap.keySet()) {

            SubTask subTask1 = subTaskMap.get(id); // делаем кейс с выбраными нами значениями

            int epicId = subTask1.getEpicId(); // забиваем данные для id эпика у которого из таблицы id подзадач будем удалять задачу
            int indexSub = epicMap.get(epicId).subtaskIds.indexOf(id); // ищем индекс нахождения id задачи которую хотим удалить

            epicMap.get(epicId).removeSubtaskIds(indexSub); // удаляем подзадачу из списка подзадач который находится в эпике
            updateEpicStatus(epicId); //проверка и если требуется изменение статуса эпика
        }

        subTaskMap.clear(); // очищаем таблицу подзадач
    }

    // -------------------------------------- 6 - Удаление по id -------------------------------------------------------
    @Override
    public void deleteTaskId(int numberId) { // удаление задачи по id

        taskMap.remove(numberId);
        managerHis.remove(numberId);
        System.out.println("Задача под номером " + numberId + " была удалена!");

    }

    @Override
    public void deleteSubTaskId(int numberId) { // удаление подзадачи по id

        int epicId = subTaskMap.get(numberId).epicId;
        int indexSub = epicMap.get(epicId).subtaskIds.indexOf(numberId); // ищем индекс нахождения
        // подзадачи в списке который хранится в эпике
        epicMap.get(epicId).subtaskIds.remove(indexSub); // удаляем подзадачу из списка подзадач который находится в эпике
        subTaskMap.remove(numberId);
        managerHis.remove(numberId);
        updateEpicStatus(epicId);
        System.out.println("Подзадача под номером " + numberId + " была удалена!");

    }

    @Override
    public void deleteEpicId(int numberId) { // удаление эпика по id

        for (int i : epicMap.get(numberId).subtaskIds) { // если в списке есть id подзадачи, то удаляем эту подзадачу
            subTaskMap.remove(i);
            managerHis.remove(i);

        }
        epicMap.remove(numberId);
        managerHis.remove(numberId);
        System.out.println("Эпик под номером " + numberId + " был удалён!");
    }

    //------------------------------------------------------------------------------------------------------------------
    @Override
    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) managerHis.getHistory();
    }


}
