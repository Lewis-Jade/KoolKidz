package com.example.child_calculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    EditText etPlayerName;
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
        etPlayerName = findViewById(R.id.et_player_name);
        btnQuit = findViewById(R.id.btn_quit);
        btnHistory = findViewById(R.id.btn_history);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String playerName = etPlayerName.getText().toString().trim();
            if (playerName.isEmpty()) {
                // Using Custom Toast
                ToastHelper.showCustomToast(this, "Please enter your name first!", R.drawable.fun_3d_cartoon_teenage_boy);
                radioGroup.clearCheck();
                return;
            }

            String mode = "";
            if (checkedId == R.id.easy) mode = "EASY";
            else if (checkedId == R.id.medium) mode = "MEDIUM";
            else if (checkedId == R.id.hard) mode = "HARD";

            if (!mode.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, Play.class);
                intent.putExtra("MODE", mode);
                intent.putExtra("PLAYER_NAME", playerName);
                startActivity(intent);
                radioGroup.clearCheck();
            }
        });

        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        });

        btnQuit.setOnClickListener(v -> {
            MusicManager.stopMusic();
            finishAffinity();
            System.exit(0);
        });
    }
}
