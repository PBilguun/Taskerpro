package com.example.taskerpro;

import android.graphics.Color;

public enum TaskPriority {
    LOW(Color.parseColor("#4CAF50")), // Green
    MEDIUM(Color.parseColor("#FFC107")), // Yellow
    HIGH(Color.parseColor("#F44336"));    // Red

    private final int color;

    TaskPriority(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
