package manager.Task;

import manager.Managers;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class InMemoryTaskManagerTest extends AbstractTaskManagerTest {

    @Test
    void saveTask() {
        savesTask(taskManager);
    }

    @Test
    void taskToTask() {
        taskToTask(taskManager);
    }

    @Test
    void epicInstancesAreEqualWhenTheirIdsEqual() {
        epicInstancesAreEqualWhenTheirIdsEqual(taskManager);
    }

    @Test
    void checkingTheSetAndGeneratedId() {
        checkingTheSetAndGeneratedId(taskManager);
    }

    @Test
    void checkingForImmutabilityByFields() {
        checkingForImmutabilityByFields(taskManager);
    }

    @Test
    public void shouldReturnInMemoryTaskManagerByDefault1() {
        shouldReturnInMemoryTaskManagerByDefault();
    }
    @Test
    void shouldReturnInMemoryHistoryManagerByDefault1(){
        shouldReturnInMemoryHistoryManagerByDefault();
    }
    @Test
    void removeTask(){
        removeTask(taskManager);
    }
    @Test
    void changeContentTask1(){
        changeContentTask(taskManager);
    }

    @Test
    void deleteTask1(){
        deleteTask(taskManager);
    }
    @Test
    void deleteEpic(){
        deleteEpic(taskManager);
    }

    @Test
    void removeEpic1(){
        removeEpic(taskManager);
    }

    @Test
    void removeSubtask1(){
        removeSubtask(taskManager);
    }

    @Test
    void clerSubTaskofEpic1(){
        clerSubTaskofEpic(taskManager);
    }

    @Test
    void clearTasks1(){
        clearTasks(taskManager);
    }

    @Test
    void getHistory1(){
        getHistory(taskManager);
    }

    @Test
    void removeTaskHis1(){
        removeTaskHis(taskManager);
    }
    @Test
    void getUniqueHistory1(){
        getUniqueHistory(taskManager);
    }

    @Test
    void historyIsEmpty1(){
        historyIsEmpty(taskManager);
    }


}
