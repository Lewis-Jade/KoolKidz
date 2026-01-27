package com.example.child_calculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    RadioGroup radioGroup;

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

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String mode = "";
            if (checkedId == R.id.easy) mode = "EASY";
            else if (checkedId == R.id.medium) mode = "MEDIUM";
            else if (checkedId == R.id.hard) mode = "HARD";

            if (!mode.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, Play.class);
                intent.putExtra("MODE", mode);
                startActivity(intent);
                
                // Optional: Clear selection so it's fresh if they come back
                radioGroup.clearCheck();
            }
        });
    }
}
