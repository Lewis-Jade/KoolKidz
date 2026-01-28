package com.example.child_calculator;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ToastHelper {

    public static void showCustomToast(Activity activity, String message, int iconRes) {
        if (activity == null || activity.isFinishing()) return;

        new Handler(Looper.getMainLooper()).post(() -> {
            // Find the root view of the activity
            ViewGroup rootView = activity.findViewById(android.R.id.content);
            if (rootView == null) return;

            LayoutInflater inflater = LayoutInflater.from(activity);
            View toastView = inflater.inflate(R.layout.custom_toast, rootView, false);

            ImageView icon = toastView.findViewById(R.id.toast_icon);
            if (icon != null) icon.setImageResource(iconRes);

            TextView text = toastView.findViewById(R.id.toast_text);
            if (text != null) text.setText(message);

            // Ensure high Z-index
            toastView.setTranslationZ(1000f);
            
            // Positioning at bottom center
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            params.bottomMargin = 200; 
            toastView.setLayoutParams(params);

            // Add to view hierarchy
            rootView.addView(toastView);

            // Fade in animation
            AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
            fadeIn.setDuration(400);
            fadeIn.setFillAfter(true);
            toastView.startAnimation(fadeIn);

            // Remove after delay
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
                fadeOut.setDuration(400);
                fadeOut.setFillAfter(true);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        rootView.removeView(toastView);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                toastView.startAnimation(fadeOut);
            }, 3000);
        });
    }
}
