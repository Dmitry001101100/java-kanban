package manager;

import enumeration.Status;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    //------------------------------------------------------------------------------------------------------------------
    private final Map<Integer, Task> task = new HashMap<>();
    private final Map<Integer, Epic> epic = new HashMap<>();
    private final Map<Integer, SubTask> subTask = new HashMap<>();

    HistoryManager managerHis = Managers.getDefaultHistory();

    //-------------------------------Вспомогательные--------------------------------------------------------------------
    int idUp = 0;

    @Override
    public int getIdUp() {// герерирует id
        while (true) {
            idUp++;
            if (keySearch(idUp)) {// если следующий id из генерации уже использовался
                idUp = idUp;//то заново;
            } else {// если не использовался то выводит значение

                break;
            }
        }
        return idUp;
    }

    @Override
    public boolean keySearch(int numberId) {// проверка ключа в задачах,эпиках и подзадачах
        for (Integer key : task.keySet()) {
            if (key == numberId) {
                return true;
            }
        }
        for (Integer key : epic.keySet()) {
            if (key == numberId) {
                return true;
            }
        }
        for (Integer key : subTask.keySet()) {
            if (key == numberId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keySearchTask(int numberId) {//проверка присутствия id в task
        for (Integer key : task.keySet()) {
            if (key == numberId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keySearchEpic(int numberId) {//проверка присутствия id epic
        for (Integer key : epic.keySet()) {
            if (key == numberId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keySearchSubTask(int numberId) {//проверка присутствия id subTask
        for (Integer key : subTask.keySet()) {
            if (key == numberId) {
                return true;
            }
        }
        return false;
    }

    public void epicStatus(int id) {// определение статуса эпика

        Epic epic1 = epic.get(id);

        int numNew = 0;
        int numDone = 0;

        if (epic.get(id).subtaskIds.isEmpty()) {// если нет подзадач статус New
            epic1.setStatus(Status.NEW);
        }

        for (int i : epic1.subtaskIds) {
            if (subTask.get(i).getStatus() == Status.DONE) {//если статус подзадачи выполнен – то счетчик numDone увеличивается на 1
                numDone++;
            } else if (subTask.get(i).getStatus() == Status.NEW) {//если статус подзадачи новый – то счетчик numNew увеличивается на 1
                numNew++;
            }
        }

        if ((epic.get(id).subtaskIds.isEmpty()) || (numNew == epic1.subtaskIds.size())) {// если список подзадач пуст или
            // все задачи имеют статус новый
            epic1.setStatus(Status.NEW); // статус эпика новый

        } else if (numDone == epic1.subtaskIds.size()) {// если все подзадачи имеют статсу выполнен

            epic1.setStatus(Status.DONE);//статус эпика выполнен
        } else {
            epic1.setStatus(Status.IN_PROGRESS);
        }
    }

    //------------------------------------ проверка не пуст ли список --------------------------------------------------
    @Override
    public boolean isEmptyTask() {
        return task.isEmpty();
    }

    @Override
    public boolean isEmptyEpic() {
        return epic.isEmpty();
    }

    @Override
    public boolean isEmptySubTask() {
        return subTask.isEmpty();
    }

    //---------------------------- keySeach ----------------------------------------------------------------------------
    @Override
    public ArrayList<Integer> keySetTask() {
        return new ArrayList<>(task.keySet());
    }

    @Override
    public ArrayList<Integer> keySetEpic() {
        return new ArrayList<>(epic.keySet());
    }

    @Override
    public ArrayList<Integer> keySetSubTask() {
        return new ArrayList<>(subTask.keySet());
    }


    //---------------------------------- 1 - Сохранение ----------------------------------------------------------------
    @Override
    public void saveTask(Task savetheTask) {// сохранение и перезапись задач

        task.put(savetheTask.id, savetheTask);
        System.out.println("Задача успешно сохранена!");

    }

    @Override
    public void saveEpic(Epic savetheEpic) {// сохранение и перезапись эпиков

        epic.put(savetheEpic.id, savetheEpic);
        System.out.println("Эпик успешно сохранен!");

    }

    @Override
    public void saveSubTask(SubTask saveSubTask) {// сохранение и перезапись подзадач

        int idEpic = saveSubTask.epicId;// записали id эпика к которому принадлежит подзадача
        int idSub = saveSubTask.id;//записали id подзадачи

        Epic epic1 = epic.get(idEpic);// вызываем нужный элемент хеш таблицы

        if (!epic1.subtaskIds.contains(idSub)) {
            epic1.addSubtaskIds(idSub);// записываем в список id подзадач новое значение
        }

        subTask.put(saveSubTask.id, saveSubTask);
        epicStatus(idEpic);//проверка и если требуется изменение статуса эпика
        System.out.println("Подзадача успешно сохранена!");

    }
//------------------------------------------- 2 - Вывод полный ---------------------------------------------------------

    @Override
    public ArrayList<Epic> getEpic() { // кладем значение из мапы эпиков в лист и возвращаем его
        return new ArrayList<>(epic.values());
    }

    @Override
    public ArrayList<Task> getTasks() { // кладем значение из мап тасков в лист и возвращаем его
        return new ArrayList<>(task.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasksId(int epicId) { // кладем значение из мап подзадач в лист и возвращаем его
        ArrayList<SubTask> subTasksIdArrayList = new ArrayList<>();

        for (int i : epic.get(epicId).getSubtaskIds()) {// если в списке есть id подзадачи – то берем на вывод эту подзадачу
            subTasksIdArrayList.add(subTask.get(i));

        }

        return subTasksIdArrayList;
    }

    @Override
    public ArrayList<SubTask> getSubTask() { // кладем значение из мап тасков в лист и возвращаем его
        return new ArrayList<>(subTask.values());
    }

    //-------------------------------------- 3 - Вывод по id -----------------------------------------------------------
    @Override
    public Task outIdTask(int numberId) {//вывод задачи по id
        return task.get(numberId);
    }

    @Override
    public SubTask outIdSubTask(int numberId) {//вывод подзадачи по id
        return subTask.get(numberId);
    }

    @Override
    public Epic outIdEpic(int numberId) {//вывод эпика по id
        return epic.get(numberId);
    }

    //--------------------------------------------- 5 - Полное удаление ------------------------------------------------
    @Override
    public void deleteContent() {// удалить всё
        deleteTask();
        deleteEpic();
        idUp = 0;// обнуляем переменную для id
        System.out.println("Все содержимое полностью очищено.");
    }

    @Override
    public void deleteTask() {// удалить все задачи
        task.clear();
        System.out.println("Задачи полностью удалены.");
    }

    @Override
    public void deleteSubTaskOfEpic(int epicId) {// удалить все подзадачи у одного эпика

        for (int i : epic.get(epicId).getSubtaskIds()) {// если в списке есть id подзадачи – то удаляем эту подзадачу
            subTask.remove(i);

        }
        epic.get(epicId).subtaskIds.clear();
        epicStatus(epicId);
    }

    @Override
    public void deleteEpic() {// удалить все эпики и подзадачи к ним относящиеся

        for (Integer name : epic.keySet()) {// перебираем значение для вывода

            for (int i : epic.get(name).subtaskIds) {// если в списке есть id подзадачи – то удаляем эту подзадачу

                subTask.remove(i);

            }
        }
        epic.clear();
        System.out.println("Эпики и подзадачи к ним относящиеся полностью удалены.");

    }

    @Override
    public void deleteSubtask() {

        for (Integer name : subTask.keySet()) {

            SubTask subTask1 = subTask.get(name);// делаем кейс с выбраными нами значениями

            int epicId = subTask1.getEpicId();// забиваем данные для id эпика у которого из таблицы id подзадач будем удалять задачу
            int indexSub = epic.get(epicId).subtaskIds.indexOf(name);// ищем индекс нахождения id задачи которую хотим удалить

            epic.get(epicId).subtaskIds.remove(indexSub);// удаляем подзадачу из списка подзадач который находится в эпике

        }

        subTask.clear();// очищаем таблицу подзадач
    }

    // -------------------------------------- 6 - Удаление по id -------------------------------------------------------
    @Override
    public void deleteTaskId(int numberId) {// удаление задачи по id

        task.remove(numberId);
        System.out.println("Задача под номером " + numberId + " была удалена!");

    }

    @Override
    public void deleteSubTaskId(int numberId) { // удаление подзадачи по id

        int epicId = subTask.get(numberId).epicId;
        int indexSub = epic.get(epicId).subtaskIds.indexOf(numberId);// ищем индекс нахождения
        // подзадачи в списке который хранится в эпике
        epic.get(epicId).subtaskIds.remove(indexSub);// удаляем подзадачу из списка подзадач который находится в эпике
        subTask.remove(numberId);
        epicStatus(epicId);
        System.out.println("Подзадача под номером " + numberId + " была удалена!");

    }

    @Override
    public void deleteEpicId(int numberId) { // удаление эпика по id

        for (int i : epic.get(numberId).subtaskIds) {// если в списке есть id подзадачи, то удаляем эту подзадачу
            subTask.remove(i);

        }
        epic.remove(numberId);
        System.out.println("Эпик под номером " + numberId + " был удалён!");
    }

    //------------------------------------------------------------------------------------------------------------------


    @Override
    public Task outIdTaskHis(int numberId) {//вывод задачи по id
        managerHis.setHistory(task.get(numberId));
        return task.get(numberId);
    }

    @Override
    public SubTask outIdSubTaskHis(int numberId) {//вывод подзадачи по id
        managerHis.setHistory(subTask.get(numberId));
        return subTask.get(numberId);
    }

    @Override
    public Epic outIdEpicHis(int numberId) {//вывод эпика по id
        managerHis.setHistory(epic.get(numberId));
        return epic.get(numberId);
    }

    @Override
    public ArrayList<Task> inPutOutPutHistory() {
        return managerHis.getHistory();
    }


}
