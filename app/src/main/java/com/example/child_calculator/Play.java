package com.example.child_calculator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
    FrameLayout balloonContainer;

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
        balloonContainer = findViewById(R.id.balloon_container);

        // Set difficulty text, color and total questions
        tvDifficultyDisplay.setText(mode);
        switch (mode) {
            case "EASY":
                tvDifficultyDisplay.setTextColor(Color.parseColor("#FF81C784"));
                totalQuestions = 10;
                break;
            case "MEDIUM":
                tvDifficultyDisplay.setTextColor(Color.parseColor("#FFFFB74D"));
                totalQuestions = 15;
                break;
            case "HARD":
                tvDifficultyDisplay.setTextColor(Color.parseColor("#FFE57373"));
                totalQuestions = 20;
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
        setNumberClick(R.id.minus_btn, "-");

        findViewById(R.id.clear_btn).setOnClickListener(v -> input.setText(""));
        findViewById(R.id.submit).setOnClickListener(v -> checkAnswer());
        findViewById(R.id.back_btn).setOnClickListener(v -> finish());

        nextQuestion();
    }

    private void setNumberClick(int id, String value) {
        View btn = findViewById(id);
        if (btn != null) {
            btn.setOnClickListener(v -> {
                String currentText = input.getText().toString();
                // Basic logic to prevent multiple minus signs or misplaced signs
                if (value.equals("-")) {
                    if (currentText.isEmpty()) {
                        input.append(value);
                    }
                } else {
                    input.append(value);
                }
            });
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
            startBalloonAnimation();
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

    private void startBalloonAnimation() {
        if (balloonContainer == null) return;
        Random rand = new Random();
        int balloonCount = 5 + rand.nextInt(6); // 5 to 10 balloons

        for (int i = 0; i < balloonCount; i++) {
            final ImageView balloon = new ImageView(this);
            balloon.setImageResource(R.drawable.balloon);
            
            // Randomize color
            int color = Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
            balloon.setColorFilter(color);

            int size = 100 + rand.nextInt(100);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            balloon.setLayoutParams(params);

            balloonContainer.addView(balloon);

            // Random horizontal position
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            balloon.setX(rand.nextInt(screenWidth - size));
            balloon.setY(getResources().getDisplayMetrics().heightPixels);

            // Animation: float up
            ObjectAnimator animator = ObjectAnimator.ofFloat(balloon, "translationY", -size - 200);
            animator.setDuration(2000 + rand.nextInt(2000));
            animator.setInterpolator(new AccelerateInterpolator());
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    balloonContainer.removeView(balloon);
                }
            });
            animator.start();
        }
    }

    private void nextQuestion() {
        Random rand = new Random();
        int a, b, c;
        int opType1, opType2;
        
        switch (mode) {
            case "HARD":
                // 3 operands for Hard mode
                a = rand.nextInt(20) + 1;
                b = rand.nextInt(15) + 1;
                c = rand.nextInt(10) + 1;
                opType1 = rand.nextInt(2); // + or -
                opType2 = rand.nextInt(2);
                
                String sOp1 = (opType1 == 0) ? " + " : " - ";
                String sOp2 = (opType2 == 0) ? " + " : " - ";
                
                currentQuestion = a + sOp1 + b + sOp2 + c;
                currentAnswer = (opType1 == 0) ? (a + b) : (a - b);
                currentAnswer = (opType2 == 0) ? (currentAnswer + c) : (currentAnswer - c);
                break;
                
            case "MEDIUM":
                a = rand.nextInt(25) + 5;
                b = rand.nextInt(20) + 5;
                opType1 = rand.nextInt(4);
                generateTwoOperandQuestion(a, b, opType1);
                break;
                
            default: // EASY
                a = rand.nextInt(10) + 1;
                b = rand.nextInt(10) + 1;
                opType1 = rand.nextInt(2);
                generateTwoOperandQuestion(a, b, opType1);
                break;
        }

        tvCalculation.setText(currentQuestion + " =");
        tvQuestionCounter.setText("Question: " + (questionCount + 1) + " / " + totalQuestions);
        
        triggerRandomAnimation();
    }

    private void triggerRandomAnimation() {
        Random rand = new Random();
        int choice = rand.nextInt(3);
        Animation anim;

        switch (choice) {
            case 0:
                anim = AnimationUtils.loadAnimation(this, R.anim.bounce);
                tvDifficultyDisplay.startAnimation(anim);
                break;
            case 1:
                anim = AnimationUtils.loadAnimation(this, R.anim.shake);
                tvQuestionCounter.startAnimation(anim);
                break;
            case 2:
                // Small pulse for the display card
                displayCard.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).withEndAction(() -> 
                    displayCard.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                ).start();
                break;
        }
    }

    private void generateTwoOperandQuestion(int a, int b, int opType) {
        switch (opType) {
            case 0:
                currentQuestion = a + " + " + b;
                currentAnswer = a + b;
                break;
            case 1:
                if (a < b && !mode.equals("HARD")) { int temp = a; a = b; b = temp; }
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
