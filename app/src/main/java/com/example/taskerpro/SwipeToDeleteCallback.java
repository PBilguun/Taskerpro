package com.example.taskerpro;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private final TaskAdapter taskAdapter;
    private final ColorDrawable deleteBackground;
    private final ColorDrawable completeBackground;
    private final Drawable deleteIcon;
    private final Drawable completeIcon;
    private final int iconMargin;
    private final Paint textPaint;
    private final String deleteLabel = "Delete";
    private final String completeLabel = "Complete";
    private final Context context;

    public SwipeToDeleteCallback(Context context, TaskAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.context = context;
        this.taskAdapter = adapter;

        deleteBackground = new ColorDrawable(ContextCompat.getColor(context, R.color.delete_color));
        completeBackground = new ColorDrawable(ContextCompat.getColor(context, R.color.complete_color));

        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        completeIcon = ContextCompat.getDrawable(context, R.drawable.ic_check);

        iconMargin = context.getResources().getDimensionPixelSize(R.dimen.icon_margin);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.swipe_label_size));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();

        if (direction == ItemTouchHelper.LEFT) {
            taskAdapter.deleteItem(position);
        } else if (direction == ItemTouchHelper.RIGHT) {
            taskAdapter.updateTaskCompletion(position);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder,
            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getBottom() - itemView.getTop();

        if (dX > 0) { // Swiping to the right (complete)
            completeBackground.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX), itemView.getBottom());
            completeBackground.draw(c);

            // Draw icon
            if (completeIcon != null) {
                int iconTop = itemView.getTop() + (itemHeight - completeIcon.getIntrinsicHeight()) / 2;
                int iconLeft = itemView.getLeft() + iconMargin;
                int iconRight = iconLeft + completeIcon.getIntrinsicWidth();
                int iconBottom = iconTop + completeIcon.getIntrinsicHeight();
                completeIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                completeIcon.draw(c);

                // Draw label
                float textX = iconRight + iconMargin * 2;
                float textY = itemView.getTop() + itemHeight / 2f + textPaint.getTextSize() / 3;
                c.drawText(completeLabel, textX, textY, textPaint);
            }
        } else if (dX < 0) { // Swiping to the left (delete)
            deleteBackground.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(),
                    itemView.getRight(), itemView.getBottom());
            deleteBackground.draw(c);

            // Draw icon
            if (deleteIcon != null) {
                int iconTop = itemView.getTop() + (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
                int iconRight = itemView.getRight() - iconMargin;
                int iconLeft = iconRight - deleteIcon.getIntrinsicWidth();
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                deleteIcon.draw(c);

                // Draw label
                float textX = iconLeft - iconMargin * 2;
                float textY = itemView.getTop() + itemHeight / 2f + textPaint.getTextSize() / 3;
                c.drawText(deleteLabel, textX, textY, textPaint);
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
