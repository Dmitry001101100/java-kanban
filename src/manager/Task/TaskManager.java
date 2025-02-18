package manager.Task;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {


    int getIdUp();

    boolean containsKeyTask(int id);
    boolean containsKeySubTask(int id);
    boolean containsKeyEpic(int id);

    void createTask(Task savetheTask);
    void updateTask(Task newTask);

    void createEpic(Epic savetheEpic);

    void createSubTask(SubTask saveSubTask);


    ArrayList<Epic> getEpics();

    ArrayList<Task> getTasks();

    ArrayList<SubTask> getSubTasksByEpicId(int epicId);

    ArrayList<SubTask> getSubTasks();


    Task getTaskById(int numberId);

    SubTask getSubTaskById(int numberId);

    Epic getEpicById(int numberId);


    void clearContent();


    void clearTasks();

    void clearSubTasksOfEpic(int epicId);

    void clearEpics();

    void clearSubtasks();

    void deleteTaskId(int numberId);

    void deleteSubTaskId(int numberId);

    void deleteEpicId(int numberId);

    ArrayList<Task> getHistory();
}
