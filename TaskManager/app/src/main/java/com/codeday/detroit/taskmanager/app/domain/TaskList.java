package com.codeday.detroit.taskmanager.app.domain;

import java.util.UUID;

/**
 * Created by timothymiko on 5/24/14.
 */
public class TaskList {

    public String identifier;
    public String name;
    public int numberOfTasks;
    public int numberOfCompletedTasks;
    public boolean isComplete;

    public TaskList() {

    }

    public TaskList(String name, int numberOfTasks, int numberOfCompletedTasks, boolean isComplete) {
        this.identifier = UUID.randomUUID().toString();
        this.name = name;
        this.numberOfTasks = numberOfTasks;
        this.numberOfCompletedTasks = numberOfCompletedTasks;
        this.isComplete = isComplete;
    }

    public TaskList(String identifier, String name, int numberOfTasks, int numberOfCompletedTasks, boolean isComplete) {
        this.identifier = identifier;
        this.name = name;
        this.numberOfTasks = numberOfTasks;
        this.numberOfCompletedTasks = numberOfCompletedTasks;
        this.isComplete = isComplete;
    }
}
