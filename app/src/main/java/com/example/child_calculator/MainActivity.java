package com.example.child_calculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    Button btnQuit, btnHistory;
    ScrollView scrollView;
    TextView tvWelcomeName;
    ImageButton btnEditName;
    
    String playerName;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences("KidzCalculatorPrefs", Context.MODE_PRIVATE);
        playerName = sharedPref.getString("SAVED_PLAYER_NAME", null);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        radioGroup = findViewById(R.id.rd_radio_group);
        btnQuit = findViewById(R.id.btn_quit);
        btnHistory = findViewById(R.id.btn_history);
        scrollView = findViewById(R.id.main_scroll_view);
        tvWelcomeName = findViewById(R.id.tv_welcome_name);
        btnEditName = findViewById(R.id.btn_edit_name);

        if (playerName == null) {
            showNamePrompt(-1); // Initial name prompt
        } else {
            updateWelcomeMessage();
        }

        btnEditName.setOnClickListener(v -> showNamePrompt(-1));

        if (scrollView != null) {
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_UP));
        }

        setupDifficultyButton(R.id.easy);
        setupDifficultyButton(R.id.medium);
        setupDifficultyButton(R.id.hard);

        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        });

        btnQuit.setOnClickListener(v -> {
            try {
                Class.forName("com.example.child_calculator.MusicManager");
                MusicManager.stopMusic();
            } catch (ClassNotFoundException ignored) {}
            
            finishAffinity();
            System.exit(0);
        });
    }

    private void updateWelcomeMessage() {
        if (playerName != null) {
            tvWelcomeName.setText("Hello, " + playerName + "!");
        }
    }

    private void setupDifficultyButton(int id) {
        RadioButton rb = findViewById(id);
        if (rb != null) {
            rb.setOnClickListener(v -> {
                if (playerName == null) {
                    showNamePrompt(id);
                } else {
                    startGame(id);
                }
                radioGroup.clearCheck();
            });
        }
    }

    private void startGame(int difficultyId) {
        String mode = "";
        if (difficultyId == R.id.easy) mode = "EASY";
        else if (difficultyId == R.id.medium) mode = "MEDIUM";
        else if (difficultyId == R.id.hard) mode = "HARD";

        Intent intent = new Intent(MainActivity.this, Play.class);
        intent.putExtra("MODE", mode);
        intent.putExtra("PLAYER_NAME", playerName);
        startActivity(intent);
    }

    private void showNamePrompt(int difficultyId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.KidDialogTheme);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_name_prompt, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.et_dialog_name);
        Button btnStart = dialogView.findViewById(R.id.btn_dialog_start);

        if (playerName != null) {
            etName.setText(playerName);
            btnStart.setText("Update Name");
        }

        AlertDialog dialog = builder.create();
        dialog.setCancelable(playerName != null); // Only cancelable if we already have a name
        
        btnStart.setOnClickListener(v -> {
            String inputName = etName.getText().toString().trim();
            
            if (inputName.isEmpty()) {
                etName.setError("Please enter your name!");
                return;
            }
            
            if (inputName.length() < 2) {
                etName.setError("Name is too short!");
                return;
            }

            playerName = inputName;
            sharedPref.edit().putString("SAVED_PLAYER_NAME", playerName).apply();
            updateWelcomeMessage();
            
            dialog.dismiss();

            // If prompt was triggered by choosing difficulty, start game now
            if (difficultyId != -1) {
                startGame(difficultyId);
            }
        });

        dialog.show();
    }
}
