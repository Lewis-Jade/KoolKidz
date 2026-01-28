package com.example.child_calculator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Random;

public class Results extends AppCompatActivity {

    private String playerName;
    private int score;
    private int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_results);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        score = intent.getIntExtra("SCORE", 0);
        total = intent.getIntExtra("TOTAL", 10);
        playerName = intent.getStringExtra("PLAYER_NAME");
        ArrayList<String> questions = intent.getStringArrayListExtra("QUESTIONS");
        ArrayList<String> correctAnswers = intent.getStringArrayListExtra("ANSWERS");
        ArrayList<String> userAnswers = intent.getStringArrayListExtra("USER_ANSWERS");

        savePlayerData();

        TextView passText = findViewById(R.id.pass);
        TextView scoreValue = findViewById(R.id.tv_score_value);
        ProgressBar progressBar = findViewById(R.id.result_progress);
        LinearLayout questionContainer = findViewById(R.id.question_list_container);

        float percent = (total > 0) ? (score * 100f) / total : 0;
        String gradeMessage;
        if (percent >= 90) {
            gradeMessage = "Excellent, " + playerName + "!";
            findViewById(R.id.main).post(this::startBalloonAnimation);
        } else if (percent >= 70) {
            gradeMessage = "Great Job, " + playerName + "!";
        } else if (percent >= 50) {
            gradeMessage = "Well Done, " + playerName + "!";
        } else {
            gradeMessage = "Keep Practicing, " + playerName + "!";
        }

        passText.setText(gradeMessage);
        scoreValue.setText(score + " / " + total);

        progressBar.setMax(total * 100);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, score * 100);
        animation.setDuration(1500);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        Typeface kidFont = ResourcesCompat.getFont(this, R.font.fuzzybubbles_bold);

        if (questions != null && correctAnswers != null && userAnswers != null) {
            for (int i = 0; i < questions.size(); i++) {
                TextView tv = new TextView(this);
                String userAns = userAnswers.size() > i ? userAnswers.get(i) : "?";
                String correctAns = correctAnswers.get(i);
                
                String displayText = (i + 1) + ". " + questions.get(i) + " = " + correctAns;
                if (!userAns.equals(correctAns)) {
                    displayText += " (You: " + userAns + ")";
                    tv.setTextColor(Color.parseColor("#E57373"));
                } else {
                    tv.setTextColor(Color.parseColor("#81C784"));
                }

                tv.setText(displayText);
                tv.setTextSize(20f);
                tv.setPadding(0, 12, 0, 12);
                if (kidFont != null) tv.setTypeface(kidFont);
                questionContainer.addView(tv);
            }
        }

        findViewById(R.id.back).setOnClickListener(v -> {
            Intent playIntent = new Intent(Results.this, Play.class);
            playIntent.putExtra("PLAYER_NAME", playerName);
            startActivity(playIntent);
            finish();
        });

        findViewById(R.id.done).setOnClickListener(v -> finish());
    }

    private void savePlayerData() {
        SharedPreferences sharedPref = getSharedPreferences("KidzCalculatorPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String key = "history_" + System.currentTimeMillis();
        String result = playerName + " | Score: " + score + "/" + total;
        editor.putString(key, result);
        editor.apply();
    }

    private void startBalloonAnimation() {
        final ConstraintLayout root = findViewById(R.id.main);
        final Random random = new Random();
        int width = root.getWidth();
        int height = root.getHeight();
        
        if (width <= 0) width = 1000;
        if (height <= 0) height = 2000;

        for (int i = 0; i < 20; i++) {
            final ImageView balloon = new ImageView(this);
            balloon.setImageResource(R.drawable.balloon);
            
            // Set translationZ to bring to front
            balloon.setTranslationZ(100f);
            
            // Random colors for balloons
            float[] hsv = {random.nextInt(360), 0.6f, 0.9f};
            balloon.setColorFilter(Color.HSVToColor(hsv));
            
            int size = 120 + random.nextInt(80);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(size, size);
            balloon.setLayoutParams(params);
            
            root.addView(balloon);
            
            balloon.setX(random.nextInt(width - size));
            balloon.setY(height + size);
            
            long duration = 3000 + random.nextInt(3000);
            long delay = random.nextInt(2000);
            
            ObjectAnimator rise = ObjectAnimator.ofFloat(balloon, "translationY", height, -size - 200);
            rise.setDuration(duration);
            rise.setStartDelay(delay);
            
            // Add a little side-to-side drift
            ObjectAnimator drift = ObjectAnimator.ofFloat(balloon, "translationX", 
                balloon.getX(), balloon.getX() + (random.nextInt(300) - 150));
            drift.setDuration(duration);
            drift.setStartDelay(delay);

            rise.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    root.removeView(balloon);
                }
            });
            
            rise.start();
            drift.start();
        }
    }
}
