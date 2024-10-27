package manager.Task;

import enumeration.*;
import manager.History.HistoryManager;
import manager.Managers;
import tasks.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    private static final Comparator<Task> taskComparator = Comparator.comparing(
            Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);
    //------------------------------------------------------------------------------------------------------------------
    protected final Map<Integer, Task> taskMap = new HashMap<>();
    protected final Map<Integer, Epic> epicMap = new HashMap<>();
    protected final Map<Integer, SubTask> subTaskMap = new HashMap<>();
    private Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);

    HistoryManager historyManager = Managers.getDefaultHistory();

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

        if (epicMap.get(id).getSubtaskIds().isEmpty()) { // если нет подзадач статус New
            epic1.setStatus(Status.NEW);
        }

        for (int i : epic1.getSubtaskIds()) {
            if (subTaskMap.get(i).getStatus() == Status.DONE) { //если статус подзадачи выполнен – то счетчик numDone увеличивается на 1
                numDone++;
            } else if (subTaskMap.get(i).getStatus() == Status.NEW) { //если статус подзадачи новый – то счетчик numNew увеличивается на 1
                numNew++;
            }
        }

        if ((epicMap.get(id).getSubtaskIds().isEmpty()) || (numNew == epic1.getSubtaskIds().size())) { // если список подзадач пуст или
            // все задачи имеют статус новый
            epic1.setStatus(Status.NEW); // статус эпика новый

        } else if (numDone == epic1.getSubtaskIds().size()) { // если все подзадачи имеют статусу выполнен

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

        taskMap.put(task.getId(), task);
        prioritizedTasks.add(task);
        System.out.println("Задача успешно сохранена!");

    }

    @Override
    public void saveEpic(Epic epic) { // сохранение и перезапись эпиков

        epicMap.put(epic.getId(), epic);
        SearchForTheStartTimeAndDuration(epic.getId());
        prioritizedTasks.add(epic);
        System.out.println("Эпик успешно сохранен!");

    }

    @Override
    public void saveSubTask(SubTask subTask) { // сохранение и перезапись подзадач

        int idEpic = subTask.getEpicId(); // записали id эпика к которому принадлежит подзадача
        int idSub = subTask.getId(); //записали id подзадачи


        Epic epic1 = epicMap.get(idEpic); // вызываем нужный элемент хеш таблицы

        if (epic1.getSubtaskIds() == null || (!epic1.getSubtaskIds().contains(idSub))) {
            epic1.addSubtaskIds(idSub); // записываем в список id подзадач новое значение
        }

        subTaskMap.put(subTask.getId(), subTask);
        updateEpicStatus(idEpic); //проверка и если требуется изменение статуса эпика
        SearchForTheStartTimeAndDuration(idEpic); // временные рамки эпика
        prioritizedTasks.add(subTask);
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
        historyManager.add(taskMap.get(numberId));
        return taskMap.get(numberId);
    }

    @Override
    public SubTask outIdSubTask(int numberId) { //вывод подзадачи по id
        historyManager.add(subTaskMap.get(numberId));
        return subTaskMap.get(numberId);
    }
    //

    @Override
    public Epic outIdEpic(int numberId) { //вывод эпика по id
        historyManager.add(epicMap.get(numberId));
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

        prioritizedTasks.removeIf(task -> task.getType() == TypeTask.TASK);

        taskMap.clear();
        System.out.println("Задачи полностью удалены.");
    }

    @Override
    public void clearSubTasksOfEpic(int epicId) { // удалить все подзадачи у одного эпика

        subTaskMap.values().removeIf(subTask -> subTask.getEpicId() == epicId); // удаляем все подзадачи относящиеся к этому эпику
        epicMap.get(epicId).getSubtaskIds().clear(); // очищаем список подзадач у эпика
        SearchForTheStartTimeAndDuration(epicId);   // временные рамки эпика
        updateEpicStatus(epicId);   // обновляем статус эпика
        System.out.println("Все подзадачи в рамках одного эпика удалены");
    }

    @Override
    public void clearEpics() { // удалить все эпики и подзадачи к ним относящиеся

        subTaskMap.clear();
        epicMap.clear();
        System.out.println("Эпики и подзадачи к ним относящиеся полностью удалены.");

    }

    @Override
    public void clearSubtasks() { // удаление всех подзадач

        for (Integer id : subTaskMap.keySet()) {

            SubTask subTask1 = subTaskMap.get(id); // делаем кейс с выбраными нами значениями

            int epicId = subTask1.getEpicId(); // забиваем данные для id эпика у которого из таблицы id подзадач будем удалять задачу
            int indexSub = epicMap.get(epicId).getSubtaskIds().indexOf(id); // ищем индекс нахождения id задачи которую хотим удалить

            epicMap.get(epicId).removeSubtaskIds(indexSub); // удаляем подзадачу из списка подзадач который находится в эпике
            SearchForTheStartTimeAndDuration(epicId); // изменяем начальное и конечное время если требуется
            updateEpicStatus(epicId); //проверка и если требуется изменение статуса эпика
        }

        subTaskMap.clear(); // очищаем таблицу подзадач
    }

    // -------------------------------------- 6 - Удаление по id -------------------------------------------------------
    @Override
    public void deleteTaskId(int numberId) { // удаление задачи по id

        taskMap.remove(numberId);
        historyManager.remove(numberId);
        System.out.println("Задача под номером " + numberId + " была удалена!");

    }

    @Override
    public void deleteSubTaskId(int numberId) { // удаление подзадачи по id

        int epicId = subTaskMap.get(numberId).getEpicId();
        int indexSub = epicMap.get(epicId).getSubtaskIds().indexOf(numberId); // ищем индекс нахождения
        // подзадачи в списке который хранится в эпике
        epicMap.get(epicId).getSubtaskIds().remove(indexSub); // удаляем подзадачу из списка подзадач который находится в эпике
        subTaskMap.remove(numberId);
        historyManager.remove(numberId);
        updateEpicStatus(epicId);
        SearchForTheStartTimeAndDuration(epicId);
        System.out.println("Подзадача под номером " + numberId + " была удалена!");

    }

    @Override
    public void deleteEpicId(int numberId) { // удаление эпика по id

        for (int i : epicMap.get(numberId).getSubtaskIds()) { // если в списке есть id подзадачи, то удаляем эту подзадачу
            subTaskMap.remove(i);
            historyManager.remove(i);

        }
        epicMap.remove(numberId);
        historyManager.remove(numberId);
        System.out.println("Эпик под номером " + numberId + " был удалён!");
    }

// ---------------------------------------------------------------------------------------------------------------------

    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) historyManager.getHistory();
    }

    // ----------------------------------- расчет начального и конечного времени эпика ----------------------------------

    public void SearchForTheStartTimeAndDuration(int epicId) {  // расчет времени старта, продолжительности и окончания эпика

        Epic epic = epicMap.get(epicId);

        LocalDateTime epicStartTime = epic.getStartTime();
        LocalDateTime epicEndTime = epic.getEndTime();
        Duration epicDuration = epic.getDuration();

        if (epic.getSubtaskIds() != null) {
            epicDuration = null;
            for (SubTask subTask : getSubTasksId(epicId)) {

                if (subTask.getEndTime() != null) {
                    if (epicEndTime == null || epicEndTime.isAfter(subTask.getEndTime())) {
                        System.out.println(" изменение конечного времени");
                        epicEndTime = subTask.getEndTime();
                    }
                }

                if (subTask.getStartTime() != null) {
                    if (epicStartTime == null || epicStartTime.isBefore(subTask.getStartTime())) {
                        System.out.println("изменение времени начала");
                        epicStartTime = subTask.getStartTime();
                    }
                }
                if (subTask.getDuration() != null) {
                   // System.out.println(subTask.getDuration());
                    if (epicDuration == null) {
                        epicDuration = subTask.getDuration();
                    } else {
                        epicDuration = epicDuration.plus(subTask.getDuration());
                        System.out.println("прибавление продолжительности " + epicDuration.toMinutes());
                    }
                }
            }
        } else {
            if (epicDuration == null) {
                epicEndTime = epicStartTime;
            } else if (epicStartTime != null) {
                epicEndTime = epicStartTime.plus(epicDuration);
            } else {
                epicEndTime = null;
            }
        }

        epic.setStartTime(epicStartTime);
        epic.setDuration(epicDuration);
        epic.setEndTime(epicEndTime);

    }


}
