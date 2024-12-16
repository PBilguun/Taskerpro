package com.example.taskerpro;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class FadeScaleItemAnimator extends DefaultItemAnimator {

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        View view = holder.itemView;

        // Set initial values
        view.setAlpha(0f);
        view.setScaleX(0.8f);
        view.setScaleY(0.8f);

        // Create animators
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(view, View.SCALE_X, 0.8f, 1f),
                ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.8f, 1f)
        );

        animatorSet.setDuration(300);
        animatorSet.start();

        return true;
    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        View view = holder.itemView;

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f),
                ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0.8f),
                ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0.8f)
        );

        animatorSet.setDuration(300);
        animatorSet.start();

        return true;
    }
}
