package com.xquipster.taskApp.api;

import com.xquipster.taskApp.TaskManager;
import org.junit.jupiter.api.*;

class TaskTest {

    @Test
    void testString() {
        Task task = new Task("1", false);
        System.out.println(task);
        Task task1 = Task.parse(task.toString());
        System.out.println(task1);
        Assertions.assertEquals(task, task1);
    }
    @Test
    void testFileReading(){
        TaskManager manager = new TaskManager();
    }
}