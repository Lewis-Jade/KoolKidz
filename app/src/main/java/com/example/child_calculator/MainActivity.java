package com.example.child_calculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    Button btnQuit, btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        radioGroup = findViewById(R.id.rd_radio_group);
        btnQuit = findViewById(R.id.btn_quit);
        btnHistory = findViewById(R.id.btn_history);

        // Use click listeners on buttons instead of checked change listener to avoid re-triggering issues
        setupDifficultyButton(R.id.easy);
        setupDifficultyButton(R.id.medium);
        setupDifficultyButton(R.id.hard);

        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        });

        btnQuit.setOnClickListener(v -> {
            MusicManager.stopMusic();
            finishAffinity();
            System.exit(0);
        });
    }

    private void setupDifficultyButton(int id) {
        RadioButton rb = findViewById(id);
        if (rb != null) {
            rb.setOnClickListener(v -> {
                showNamePrompt(id);
                // Temporarily uncheck so it doesn't look stuck
                radioGroup.clearCheck();
            });
        }
    }

    private void showNamePrompt(int difficultyId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.KidDialogTheme);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_name_prompt, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.et_dialog_name);
        Button btnStart = dialogView.findViewById(R.id.btn_dialog_start);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        
        btnStart.setOnClickListener(v -> {
            String playerName = etName.getText().toString().trim();
            if (playerName.isEmpty()) playerName = "Hero";

            String mode = "";
            if (difficultyId == R.id.easy) mode = "EASY";
            else if (difficultyId == R.id.medium) mode = "MEDIUM";
            else if (difficultyId == R.id.hard) mode = "HARD";

            Intent intent = new Intent(MainActivity.this, Play.class);
            intent.putExtra("MODE", mode);
            intent.putExtra("PLAYER_NAME", playerName);
            startActivity(intent);
            
            dialog.dismiss();
        });

        dialog.show();
    }
}
