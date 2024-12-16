package com.example.taskerpro;

import java.util.ArrayList;
import java.util.List;

public class TaskStatsManager {

    private static volatile TaskStatsManager instance;
    private final List<TodoTask> tasks;
    private final List<TaskUpdateListener> listeners;
    private boolean isUpdating = false;

    private TaskStatsManager() {
        tasks = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public static TaskStatsManager getInstance() {
        if (instance == null) {
            synchronized (TaskStatsManager.class) {
                if (instance == null) {
                    instance = new TaskStatsManager();
                }
            }
        }
        return instance;
    }

    public void addTask(TodoTask task) {
        if (task != null && !tasks.contains(task)) {
            tasks.add(task);
            notifyListeners();
        }
    }

    public void removeTask(TodoTask task) {
        if (task != null && tasks.remove(task)) {
            notifyListeners();
        }
    }

    public void updateTask(TodoTask task) {
        if (task != null && tasks.contains(task)) {
            notifyListeners();
        }
    }

    public List<TodoTask> getTasks() {
        return new ArrayList<>(tasks);
    }

    public void addListener(TaskUpdateListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(TaskUpdateListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    private void notifyListeners() {
        if (isUpdating) {
            return;
        }
        isUpdating = true;
        try {
            List<TodoTask> tasksCopy = new ArrayList<>(tasks);
            for (TaskUpdateListener listener : new ArrayList<>(listeners)) {
                if (listener != null) {
                    listener.onTasksUpdated(tasksCopy);
                }
            }
        } finally {
            isUpdating = false;
        }
    }

    public interface TaskUpdateListener {

        void onTasksUpdated(List<TodoTask> tasks);
    }

    public void clear() {
        tasks.clear();
        listeners.clear();
    }
}
