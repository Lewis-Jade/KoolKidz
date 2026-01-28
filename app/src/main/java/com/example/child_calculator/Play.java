package com.example.child_calculator;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Random;

public class Play extends AppCompatActivity {

    TextView tvCalculation, tvDifficultyDisplay, tvQuestionCounter;
    EditText input;
    View displayCard;

    int questionCount = 0;
    int totalQuestions = 10;
    int score = 0;

    String currentQuestion;
    double currentAnswer;
    String playerName;
    String mode;

    ArrayList<String> questionsList = new ArrayList<>();
    ArrayList<String> answersList = new ArrayList<>();
    ArrayList<String> userAnswersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        playerName = getIntent().getStringExtra("PLAYER_NAME");
        mode = getIntent().getStringExtra("MODE");
        if (playerName == null) playerName = "Player";
        if (mode == null) mode = "EASY";

        tvCalculation = findViewById(R.id.tvcalculation);
        tvDifficultyDisplay = findViewById(R.id.tv_difficulty_display);
        tvQuestionCounter = findViewById(R.id.tv_question_counter);
        input = findViewById(R.id.input);
        displayCard = findViewById(R.id.display_card);

        // Set difficulty text and color
        tvDifficultyDisplay.setText(mode);
        switch (mode) {
            case "EASY":
                tvDifficultyDisplay.setTextColor(Color.parseColor("#FF81C784")); // Soft Green
                break;
            case "MEDIUM":
                tvDifficultyDisplay.setTextColor(Color.parseColor("#FFFFB74D")); // Accent Orange
                break;
            case "HARD":
                tvDifficultyDisplay.setTextColor(Color.parseColor("#FFE57373")); // Soft Red
                break;
        }

        setNumberClick(R.id.zero, "0");
        setNumberClick(R.id.one, "1");
        setNumberClick(R.id.two, "2");
        setNumberClick(R.id.three, "3");
        setNumberClick(R.id.four, "4");
        setNumberClick(R.id.five, "5");
        setNumberClick(R.id.six, "6");
        setNumberClick(R.id.seven, "7");
        setNumberClick(R.id.eight, "8");
        setNumberClick(R.id.nine, "9");
        setNumberClick(R.id.point, ".");

        findViewById(R.id.clear_btn).setOnClickListener(v -> input.setText(""));
        findViewById(R.id.submit).setOnClickListener(v -> checkAnswer());
        findViewById(R.id.back_btn).setOnClickListener(v -> finish());

        nextQuestion();
    }

    private void setNumberClick(int id, String value) {
        View btn = findViewById(id);
        if (btn != null) {
            btn.setOnClickListener(v -> input.append(value));
        }
    }

    private void playSound(int resId) {
        MediaPlayer mp = MediaPlayer.create(this, resId);
        if (mp != null) {
            mp.setOnCompletionListener(MediaPlayer::release);
            mp.start();
        }
    }

    private void checkAnswer() {
        String userText = input.getText().toString();
        if (userText.isEmpty()) {
            ToastHelper.showCustomToast(this, "Enter an answer!", R.drawable.fun_3d_cartoon_teenage_boy);
            return;
        }

        double userAnswer;
        try {
            userAnswer = Double.parseDouble(userText);
        } catch (NumberFormatException e) {
            ToastHelper.showCustomToast(this, "Invalid number!", R.drawable.fun_3d_cartoon_teenage_boy);
            return;
        }

        questionsList.add(currentQuestion);
        answersList.add(String.valueOf(currentAnswer));
        userAnswersList.add(userText);

        if (Math.abs(userAnswer - currentAnswer) < 0.01) {
            score++;
            playSound(R.raw.success);
            Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
            displayCard.startAnimation(bounce);
            ToastHelper.showCustomToast(this, "Awesome, " + playerName + "! Correct!", R.drawable.fun_3d_cartoon_teenage_boy);
        } else {
            playSound(R.raw.failure);
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            displayCard.startAnimation(shake);
            ToastHelper.showCustomToast(this, "Oops! The answer was " + currentAnswer, R.drawable.fun_3d_cartoon_teenage_boy);
        }

        questionCount++;
        if (questionCount < totalQuestions) {
            nextQuestion();
        } else {
            showResults();
        }
        input.setText("");
    }

    private void nextQuestion() {
        Random rand = new Random();
        int a = rand.nextInt(10) + 1;
        int b = rand.nextInt(10) + 1;
        
        int opType = rand.nextInt(4);
        switch (opType) {
            case 0:
                currentQuestion = a + " + " + b;
                currentAnswer = a + b;
                break;
            case 1:
                if (a < b) { int temp = a; a = b; b = temp; }
                currentQuestion = a + " - " + b;
                currentAnswer = a - b;
                break;
            case 2:
                currentQuestion = a + " × " + b;
                currentAnswer = a * b;
                break;
            case 3:
                currentAnswer = a;
                a = a * b;
                currentQuestion = a + " ÷ " + b;
                break;
        }

        tvCalculation.setText(currentQuestion + " =");
        tvQuestionCounter.setText("Question: " + (questionCount + 1) + " / " + totalQuestions);
    }

    private void showResults() {
        Intent intent = new Intent(this, Results.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL", totalQuestions);
        intent.putExtra("PLAYER_NAME", playerName);
        intent.putStringArrayListExtra("QUESTIONS", questionsList);
        intent.putStringArrayListExtra("ANSWERS", answersList);
        intent.putStringArrayListExtra("USER_ANSWERS", userAnswersList);
        startActivity(intent);
        finish();
    }
}
