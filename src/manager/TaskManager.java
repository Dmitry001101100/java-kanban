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


    boolean isEmptyTask();

    boolean isEmptyEpic();

    boolean isEmptySubTask();

    ArrayList<Integer> keySetTask();

    ArrayList<Integer> keySetEpic();

    ArrayList<Integer> keySetSubTask();

    void saveTask(Task savetheTask);

    void saveEpic(Epic savetheEpic);

    void saveSubTask(SubTask saveSubTask);



    ArrayList<Epic> getEpic();

    ArrayList<Task> getTasks();

    ArrayList<SubTask> getSubTasksId(int epicId);

    ArrayList<SubTask> getSubTask();


    Task outIdTask(int numberId);

    SubTask outIdSubTask(int numberId);

    Epic outIdEpic(int numberId);


    void deleteContent();


    void deleteTasks();

    void deleteSubTaskOfEpic(int epicId);

    void deleteEpics();

    void deleteSubtasks();

    void deleteTaskId(int numberId);

    void deleteSubTaskId(int numberId);

    void deleteEpicId(int numberId);

    Task outIdTaskHis(int numberId);

    SubTask outIdSubTaskHis(int numberId);

    Epic outIdEpicHis(int numberId);

    ArrayList<Task> inPutOutPutHistory();
}
