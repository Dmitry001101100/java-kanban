package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {
    int getIdUp();

    boolean keySearch(int numberId);

    boolean keySearchTask(int numberId);

    boolean keySearchEpic(int numberId);

    boolean keySearchSubTask(int numberId);


    boolean isEmptyTaskMap();

    boolean isEmptyEpicMap();

    boolean isEmptySubTaskMap();

    ArrayList<Integer> keySetTaskMap();

    ArrayList<Integer> keySetEpicMap();

    ArrayList<Integer> keySetSubTaskMap();

    void saveTask(Task savetheTask);

    void saveEpic(Epic savetheEpic);

    void saveSubTask(SubTask saveSubTask);



    ArrayList<Epic> getEpics();

    ArrayList<Task> getTasks();

    ArrayList<SubTask> getSubTasksId(int epicId);

    ArrayList<SubTask> getSubTasks();


    Task outIdTask(int numberId);

    SubTask outIdSubTask(int numberId);

    Epic outIdEpic(int numberId);


    void clearContent();


    void clearTasks();

    void clearSubTasksOfEpic(int epicId);

    void clearEpics();

    void clearSubtasks();

    void deleteTaskId(int numberId);

    void deleteSubTaskId(int numberId);

    void deleteEpicId(int numberId);

    Task outIdTaskHis(int numberId);

    SubTask outIdSubTaskHis(int numberId);

    Epic outIdEpicHis(int numberId);

    ArrayList<Task> inPutOutPutHistory();
}
