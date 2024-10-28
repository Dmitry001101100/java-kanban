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
        Epic epic = epicMap.get(id);
        List<Integer> subtaskList = epicMap.get(id).getSubtaskIds();
        if (subtaskList.isEmpty()) epic.setStatus(Status.NEW);

        int newStatus = 0;
        int doneStatus = 0;

        for (Integer subtaskId : subtaskList) {
            Status status = subTaskMap.get(subtaskId).getStatus();
            if (status.equals(Status.NEW)) newStatus++;
            if (status.equals(Status.DONE)) doneStatus++;
        }

        if (newStatus == subtaskList.size()) epic.setStatus(Status.NEW);
        else if (doneStatus == subtaskList.size()) epic.setStatus(Status.DONE);
        else epic.setStatus(Status.IN_PROGRESS);

    }

    // -------------------------------------- prioritizedTasks ---------------------------------------------------------

    public boolean isValid(Task task) {
        for (Task existingTask : taskMap.values()) {
            if (existingTask.getId() != task.getId() &&
                    (task.getStartTime().isBefore(existingTask.getEndTime()) &&
                            task.getEndTime().isAfter(existingTask.getStartTime()))) {
                return false;
            }
        }
        return true;
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public void setPrioritizedTasks(Task task) {
        this.prioritizedTasks.add(task);
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
        searchForTheStartTimeAndDuration(epic.getId());
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
        searchForTheStartTimeAndDuration(idEpic); // временные рамки эпика
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
        return epicMap.get(epicId).getSubtaskIds().stream()
                .map(subTaskMap::get)
                .collect(Collectors.toCollection(ArrayList::new));
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
        prioritizedTasks.clear();
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
        searchForTheStartTimeAndDuration(epicId);   // временные рамки эпика
        updateEpicStatus(epicId);   // обновляем статус эпика
        System.out.println("Все подзадачи в рамках одного эпика удалены");
    }

    @Override
    public void clearEpics() { // удалить все эпики и подзадачи к ним относящиеся

        subTaskMap.clear();
        epicMap.clear();
        prioritizedTasks.removeIf(task -> task.getType() == TypeTask.EPIC);
        prioritizedTasks.removeIf(task -> task.getType() == TypeTask.SUBTASK);
        System.out.println("Эпики и подзадачи к ним относящиеся полностью удалены.");

    }

    @Override
    public void clearSubtasks() { // удаление всех подзадач

        epicMap.values().stream()
                .filter(epic -> !epic.getSubtaskIds().isEmpty())
                .peek(Epic::clearSubtaskIds)
                .peek(epic -> updateEpicStatus(epic.getId()))
                .peek(epic -> searchForTheStartTimeAndDuration(epic.getId()))
                        .collect(Collectors.toList());



        /*
        for (Integer id : subTaskMap.keySet()) {
            SubTask subTask1 = subTaskMap.get(id);
            int epicId = subTask1.getEpicId();
            int indexSub = epicMap.get(epicId).getSubtaskIds().indexOf(id);
            epicMap.get(epicId).removeSubtaskIds(indexSub);
            searchForTheStartTimeAndDuration(epicId);
            updateEpicStatus(epicId);
        }*/
        subTaskMap.clear();
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

        subTaskMap.values().stream()
                .filter(subTask -> subTask.getId().equals(numberId)) // проверяем есть ли подзадача с этим id
                .peek(subTask -> historyManager.remove(subTask.getId())) // удаляем из истории
                .peek(subTask -> epicMap.get(subTask.getEpicId()).removeSubtaskIds(subTask.getId())) // удаляем подзадачу из списка подзадач эпика
                .peek(subTask -> updateEpicStatus(subTask.getEpicId())) // обновляем статус эпика после удаления подзадачи
                .peek(subTask -> searchForTheStartTimeAndDuration(subTask.getEpicId()))
                .collect(Collectors.toList());
        //обновляем временные рамки эпика



/*
        int epicId = subTaskMap.get(numberId).getEpicId();
        int indexSub = epicMap.get(epicId).getSubtaskIds().indexOf(numberId); // ищем индекс нахождения
        // подзадачи в списке который хранится в эпике
        epicMap.get(epicId).getSubtaskIds().remove(indexSub); // удаляем подзадачу из списка подзадач который находится в эпике
        subTaskMap.remove(numberId);
        historyManager.remove(numberId);
        updateEpicStatus(epicId);
        searchForTheStartTimeAndDuration(epicId);
        System.out.println("Подзадача под номером " + numberId + " была удалена!");

 */

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

    public void searchForTheStartTimeAndDuration(int epicId) {
        Epic epic = epicMap.get(epicId);

        LocalDateTime epicStartTime = null;
        LocalDateTime epicEndTime = null;
        Duration epicDuration = null;

        if (!epic.getSubtaskIds().isEmpty()) {

            for (int i : epic.getSubtaskIds()) {
                //System.out.println(i);
                SubTask subTask = subTaskMap.get(i);
                if (subTask.getEndTime() != null) {
                    if (epicEndTime == null || epicEndTime.isAfter(subTask.getEndTime())) {
                        //   System.out.println("изменение конечного времени");
                        epicEndTime = subTask.getEndTime();
                    }
                }

                if (subTask.getStartTime() != null) {
                    if (epicStartTime == null || epicStartTime.isBefore(subTask.getStartTime())) {
                        //  System.out.println("изменение времени начала");
                        epicStartTime = subTask.getStartTime();
                    }
                }

                if (subTask.getDuration() != null) {
                    if (epicDuration == null) {
                        epicDuration = subTask.getDuration();
                        // System.out.println("присвоение продолжительности "+subTask.getDuration());
                    } else {
                        epicDuration = epicDuration.plus(subTask.getDuration());
                        // System.out.println("прибавление продолжительности " + epicDuration.toMinutes());
                    }
                }
            }
        }

        epic.setStartTime(epicStartTime);
        epic.setDuration(epicDuration);
        epic.setEndTime(epicEndTime);
    }

}
