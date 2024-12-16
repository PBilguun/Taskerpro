package com.example.taskerpro;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements TaskStatsManager.TaskUpdateListener {

    private TextView totalTasksView;
    private TextView completedTasksView;
    private TextView pendingTasksView;
    private RecyclerView recentTasksRecycler;
    private TaskAdapter recentTasksAdapter;
    private PieChart priorityPieChart;
    private BarChart completionBarChart;
    private List<TodoTask> allTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeViews();
        setupCharts();
        setupRecentTasksRecycler();


        TaskStatsManager.getInstance().addListener(this);


        onTasksUpdated(TaskStatsManager.getInstance().getTasks());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TaskStatsManager.getInstance().removeListener(this);
    }

    @Override
    public void onTasksUpdated(List<TodoTask> tasks) {
        this.allTasks = new ArrayList<>(tasks);
        updateDashboard();
    }

    private void initializeViews() {
        totalTasksView = findViewById(R.id.total_tasks);
        completedTasksView = findViewById(R.id.completed_tasks);
        pendingTasksView = findViewById(R.id.pending_tasks);
        priorityPieChart = findViewById(R.id.priority_pie_chart);
        completionBarChart = findViewById(R.id.completion_bar_chart);
        recentTasksRecycler = findViewById(R.id.recent_tasks_recycler);
    }

    private void setupCharts() {
        // Setup Pie Chart
        priorityPieChart.setDrawHoleEnabled(true);
        priorityPieChart.setHoleColor(Color.WHITE);
        priorityPieChart.setTransparentCircleRadius(61f);
        priorityPieChart.setDescription(null);

        // Setup Bar Chart
        completionBarChart.setDescription(null);
        completionBarChart.setDrawGridBackground(false);
        completionBarChart.setDrawBarShadow(false);
    }

    private void updateCharts() {
        updatePriorityChart();
        updateCompletionTrendChart();
    }

    private void updatePriorityChart() {
        int highPriority = 0, mediumPriority = 0, lowPriority = 0;

        for (TodoTask task : allTasks) {
            switch (task.getPriority()) {
                case HIGH:
                    highPriority++;
                    break;
                case MEDIUM:
                    mediumPriority++;
                    break;
                case LOW:
                    lowPriority++;
                    break;
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        if (highPriority > 0) {
            entries.add(new PieEntry(highPriority, "High"));
        }
        if (mediumPriority > 0) {
            entries.add(new PieEntry(mediumPriority, "Medium"));
        }
        if (lowPriority > 0) {
            entries.add(new PieEntry(lowPriority, "Low"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Priority Distribution");
        dataSet.setColors(Color.RED, Color.YELLOW, Color.GREEN);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        priorityPieChart.setData(data);
        priorityPieChart.invalidate();
    }

    private void updateCompletionTrendChart() {

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 4)); // Monday
        entries.add(new BarEntry(1, 6)); // Tuesday
        entries.add(new BarEntry(2, 3)); // Wednesday
        entries.add(new BarEntry(3, 5)); // Thursday
        entries.add(new BarEntry(4, 7)); // Friday
        entries.add(new BarEntry(5, 2)); // Saturday
        entries.add(new BarEntry(6, 4)); // Sunday

        BarDataSet dataSet = new BarDataSet(entries, "Tasks Completed");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        completionBarChart.setData(data);
        completionBarChart.invalidate();
    }

    private void setupRecentTasksRecycler() {
        recentTasksAdapter = new TaskAdapter();
        recentTasksRecycler.setLayoutManager(new LinearLayoutManager(this));
        recentTasksRecycler.setAdapter(recentTasksAdapter);
    }

    private void updateDashboard() {
        if (allTasks == null) {
            return;
        }

        int totalTasks = allTasks.size();
        int completedTasks = 0;
        int highPriority = 0;
        int mediumPriority = 0;
        int lowPriority = 0;

        for (TodoTask task : allTasks) {
            if (task.isCompleted()) {
                completedTasks++;
            }

            switch (task.getPriority()) {
                case HIGH:
                    highPriority++;
                    break;
                case MEDIUM:
                    mediumPriority++;
                    break;
                case LOW:
                    lowPriority++;
                    break;
            }
        }


        totalTasksView.setText(String.valueOf(totalTasks));
        completedTasksView.setText(String.valueOf(completedTasks));
        pendingTasksView.setText(String.valueOf(totalTasks - completedTasks));


        List<TodoTask> recentTasks = new ArrayList<>();
        int recentTasksCount = Math.min(allTasks.size(), 5);
        for (int i = 0; i < recentTasksCount; i++) {
            recentTasks.add(allTasks.get(i));
        }
        recentTasksAdapter.setTasks(recentTasks);


        updateCharts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboard();
        updateCharts();
    }
}
