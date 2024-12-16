package com.example.taskerpro;

public class TodoTask {

    private String title;
    private String description;
    private boolean completed;
    private long timestamp;
    private Priority priority;

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    public TodoTask(String title, String description, long timestamp) {
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.completed = false;
        this.priority = Priority.MEDIUM; // Default priority
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Priority getPriority() {
        return priority;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TodoTask task = (TodoTask) o;
        return timestamp == task.timestamp
                && title.equals(task.title)
                && description.equals(task.description);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
}
