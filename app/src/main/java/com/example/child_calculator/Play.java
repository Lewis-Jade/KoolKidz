package com.example.child_calculator;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Random;

public class Play extends AppCompatActivity {

    TextView tvCalculation;
    EditText input;
    View displayCard;

    int questionCount = 0;
    int totalQuestions = 10;
    int score = 0;

    String currentQuestion;
    double currentAnswer;

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

        tvCalculation = findViewById(R.id.tvcalculation);
        input = findViewById(R.id.input);
        displayCard = findViewById(R.id.display_card);

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
            Toast.makeText(this, "Enter an answer!", Toast.LENGTH_SHORT).show();
            return;
        }

        double userAnswer;
        try {
            userAnswer = Double.parseDouble(userText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number!", Toast.LENGTH_SHORT).show();
            return;
        }

        questionsList.add(currentQuestion);
        answersList.add(String.valueOf(currentAnswer));
        userAnswersList.add(userText);

        if (Math.abs(userAnswer - currentAnswer) < 0.01) {
            score++;
            playSound(R.raw.levelup_sound); // Play "Success" sound
            Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
            displayCard.startAnimation(bounce);
            Toast.makeText(this, "Awesome! Correct!", Toast.LENGTH_SHORT).show();
        } else {
            playSound(R.raw.wrong_sound); // Play "Wrong" sound
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            displayCard.startAnimation(shake);
            Toast.makeText(this, "Oops! The answer was " + currentAnswer, Toast.LENGTH_SHORT).show();
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
    }

    private void showResults() {
        Intent intent = new Intent(this, Results.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL", totalQuestions);
        intent.putStringArrayListExtra("QUESTIONS", questionsList);
        intent.putStringArrayListExtra("ANSWERS", answersList);
        intent.putStringArrayListExtra("USER_ANSWERS", userAnswersList);
        startActivity(intent);
        finish();
    }
}
