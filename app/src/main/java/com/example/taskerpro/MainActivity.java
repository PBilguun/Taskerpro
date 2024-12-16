package com.example.taskerpro;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "TaskerProPrefs";
    private static final String THEME_KEY = "isDarkMode";

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddTask;
    private TaskAdapter taskAdapter;
    private List<TodoTask> taskList;
    private TaskStatsManager taskStatsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskStatsManager = TaskStatsManager.getInstance();
        taskList = new ArrayList<>();
        initializeViews();
        setupFabAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            taskStatsManager.clear();
        }
    }

    private void initializeViews() {
        try {
            recyclerView = findViewById(R.id.recycler_tasks);
            fabAddTask = findViewById(R.id.fab_add_task);

            // Setup RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            taskAdapter = new TaskAdapter();
            recyclerView.setAdapter(taskAdapter);
            recyclerView.setItemAnimator(new FadeScaleItemAnimator());

            // Setup FAB
            fabAddTask.setOnClickListener(v -> showAddTaskDialog());

            // Run layout animation
            runLayoutAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAddTaskDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add New Task");

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(40, 20, 40, 10);

            final EditText titleInput = new EditText(this);
            titleInput.setHint("Task Title");
            layout.addView(titleInput);

            final EditText descriptionInput = new EditText(this);
            descriptionInput.setHint("Task Description");
            layout.addView(descriptionInput);

            builder.setView(layout);

            builder.setPositiveButton("Add", (dialog, which) -> {
                String title = titleInput.getText().toString().trim();
                String description = descriptionInput.getText().toString().trim();

                if (!title.isEmpty()) {
                    TodoTask newTask = new TodoTask(title, description, System.currentTimeMillis());
                    taskList.add(newTask);
                    taskAdapter.addTask(newTask);
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runLayoutAnimation() {
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(
                this, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }

    private void setupFabAnimation() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabAddTask.hide();
                } else {
                    fabAddTask.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView;
        try {
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        } catch (ClassCastException e) {
            e.printStackTrace();
            return true;
        }

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchTasks(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchTasks(newText);
                    return true;
                }
            });
        }

        return true;
    }

    private void searchTasks(String query) {
        List<TodoTask> filteredList = new ArrayList<>();
        for (TodoTask task : taskList) {
            if (task.getTitle().toLowerCase().contains(query.toLowerCase())
                    || task.getDescription().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(task);
            }
        }
        taskAdapter.setTasks(filteredList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskAdapter.setTasks(taskList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_dashboard) {
            openDashboard();
            return true;
        } else if (id == R.id.action_theme) {
            toggleTheme();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(THEME_KEY, false);
        isDarkMode = !isDarkMode;

        prefs.edit().putBoolean(THEME_KEY, isDarkMode).apply();

        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(THEME_KEY, false);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    private void openDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }
}
