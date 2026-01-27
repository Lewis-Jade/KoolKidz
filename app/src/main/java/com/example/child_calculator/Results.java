package com.example.child_calculator;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Results extends AppCompatActivity {

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
        int score = intent.getIntExtra("SCORE", 0);
        int total = intent.getIntExtra("TOTAL", 10);
        ArrayList<String> questions = intent.getStringArrayListExtra("QUESTIONS");
        ArrayList<String> correctAnswers = intent.getStringArrayListExtra("ANSWERS");
        ArrayList<String> userAnswers = intent.getStringArrayListExtra("USER_ANSWERS");

        TextView passText = findViewById(R.id.pass);
        TextView scoreValue = findViewById(R.id.tv_score_value);
        ProgressBar progressBar = findViewById(R.id.result_progress);
        LinearLayout questionContainer = findViewById(R.id.question_list_container);

        float percent = (total > 0) ? (score * 100f) / total : 0;
        String gradeMessage;
        if (percent >= 90) gradeMessage = "Excellent!";
        else if (percent >= 70) gradeMessage = "Great Job!";
        else if (percent >= 50) gradeMessage = "Well Done!";
        else gradeMessage = "Keep Practicing!";

        passText.setText(gradeMessage);
        scoreValue.setText(score + " / " + total);

        // Animate progress bar
        progressBar.setMax(total * 100);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, score * 100);
        animation.setDuration(1500);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        Typeface kidFont = ResourcesCompat.getFont(this, R.font.fuzzybubbles_bold);

        // Populate question list
        if (questions != null && correctAnswers != null && userAnswers != null) {
            for (int i = 0; i < questions.size(); i++) {
                TextView tv = new TextView(this);
                String userAns = userAnswers.size() > i ? userAnswers.get(i) : "?";
                String correctAns = correctAnswers.get(i);
                
                String displayText = (i + 1) + ". " + questions.get(i) + " = " + correctAns;
                if (!userAns.equals(correctAns)) {
                    displayText += " (You: " + userAns + ")";
                    tv.setTextColor(Color.parseColor("#E57373")); // Soft red
                } else {
                    tv.setTextColor(Color.parseColor("#81C784")); // Soft green
                }

                tv.setText(displayText);
                tv.setTextSize(20f);
                tv.setPadding(0, 12, 0, 12);
                if (kidFont != null) tv.setTypeface(kidFont);
                questionContainer.addView(tv);
            }
        }

        Button back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            Intent playIntent = new Intent(Results.this, Play.class);
            // Optional: pass the same mode if you want to keep the difficulty
            startActivity(playIntent);
            finish();
        });

        findViewById(R.id.done).setOnClickListener(v -> finish());
    }
}
