package manager;

import exception.ManagerSaveException;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager  {

    private final File file;


    public FileBackedTaskManager( File file) {
        super();
        this.file = file;
    }

    public void saveTas(Task task){
        saveTask(task);
        save();
    }
//----------------------------------------------------------------------------------------------------------------------



    // метод для сохранения всех видов задач в файл;

    public void save(){
        sortList(getTasks());
        /*
        try (Writer writer = new FileWriter(file);){

            writer.write("id,type,name,status,description,epic\n");
            for (Integer key : taskMap.keySet()) {
                writer.write(taskMap.get(key).toString() + "\n");
            }
            for (Integer key : epicMap.keySet()) {
                writer.write(epicMap.get(key).toString() + "\n");
            }
            for (Integer key : subTaskMap.keySet()) {
                writer.write(subTaskMap.get(key).toString() + "\n");
            }
        } catch (IOException exp) {
            throw new ManagerSaveException("Произошла ошибка записи в файл", exp);
        }*/
    }

    public ArrayList<String> sortList(ArrayList<Task> list){
        ArrayList<String> sortFile = new ArrayList<>();

        for(Task text : list){
            System.out.println(text);
            sortFile.add(text.toString());
        }


        return sortFile;
    }


}
