package com.example.taskerpro;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final List<TodoTask> todoTasks;
    private final List<TodoTask> allTasks; // For search functionality
    private Context context;
    private final TaskStatsManager statsManager;

    public TaskAdapter() {
        this.todoTasks = new ArrayList<>();
        this.allTasks = new ArrayList<>();
        this.statsManager = TaskStatsManager.getInstance();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TodoTask task = todoTasks.get(position);

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.titleTextView.setText(task.getTitle());
        holder.descriptionTextView.setText(task.getDescription());
        holder.checkBox.setChecked(task.isCompleted());

        updateStrikeThrough(holder.titleTextView, task.isCompleted());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            updateStrikeThrough(holder.titleTextView, isChecked);
            statsManager.updateTask(task);

            // Play completion animation
            if (isChecked) {
                Animation checkAnim = AnimationUtils.loadAnimation(context, R.anim.check_animation);
                holder.checkBox.startAnimation(checkAnim);
            }
        });

        // Add click animation
        holder.itemView.setOnClickListener(v -> {
            v.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() ->
                            v.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(100)
                                    .start())
                    .start();
        });
    }

    private void updateStrikeThrough(TextView textView, boolean isChecked) {
        if (isChecked) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return todoTasks.size();
    }

    public void setTasks(List<TodoTask> tasks) {
        todoTasks.clear();
        allTasks.clear();
        if (tasks != null) {
            todoTasks.addAll(tasks);
            allTasks.addAll(tasks);
            for (TodoTask task : tasks) {
                statsManager.addTask(task);
            }
        }
        notifyDataSetChanged();
    }

    public void addTask(TodoTask task) {
        todoTasks.add(task);
        allTasks.add(task);
        statsManager.addTask(task);
        notifyItemInserted(todoTasks.size() - 1);
    }

    public void deleteItem(int position) {
        if (position >= 0 && position < todoTasks.size()) {
            TodoTask taskToDelete = todoTasks.get(position);

            new AlertDialog.Builder(context)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        todoTasks.remove(position);
                        allTasks.remove(taskToDelete);
                        notifyItemRemoved(position);
                        statsManager.removeTask(taskToDelete);
                        Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        notifyItemChanged(position);
                    })
                    .show();
        }
    }

    public void updateTaskCompletion(int position) {
        if (position >= 0 && position < todoTasks.size()) {
            TodoTask task = todoTasks.get(position);
            task.setCompleted(!task.isCompleted());
            notifyItemChanged(position);
            statsManager.updateTask(task);

            String status = task.isCompleted() ? "completed" : "uncompleted";
            Toast.makeText(context, "Task marked as " + status, Toast.LENGTH_SHORT).show();
        }
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        final CheckBox checkBox;
        final TextView titleTextView;
        final TextView descriptionTextView;

        TaskViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_task);
            titleTextView = itemView.findViewById(R.id.text_task_title);
            descriptionTextView = itemView.findViewById(R.id.text_task_description);
        }
    }
}
