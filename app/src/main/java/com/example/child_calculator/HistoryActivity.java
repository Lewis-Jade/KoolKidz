package com.example.child_calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout historyContainer = findViewById(R.id.history_container);
        Typeface kidFont = ResourcesCompat.getFont(this, R.font.fuzzybubbles_bold);

        SharedPreferences sharedPref = getSharedPreferences("KidzCalculatorPrefs", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPref.getAll();

        if (allEntries == null || allEntries.isEmpty()) {
            TextView emptyTv = new TextView(this);
            emptyTv.setText("No history yet. Start playing!");
            emptyTv.setTextSize(22f);
            emptyTv.setTextColor(Color.GRAY);
            emptyTv.setPadding(20, 20, 20, 20);
            if (kidFont != null) emptyTv.setTypeface(kidFont);
            historyContainer.addView(emptyTv);
        } else {
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                if (entry.getKey().startsWith("history_")) {
                    TextView tv = new TextView(this);
                    tv.setText(entry.getValue().toString());
                    tv.setTextSize(20f);
                    tv.setPadding(0, 16, 0, 16);
                    tv.setTextColor(Color.parseColor("#FF039BE5"));
                    if (kidFont != null) tv.setTypeface(kidFont);
                    historyContainer.addView(tv);

                    // Add a small divider
                    View divider = new View(this);
                    divider.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 2));
                    divider.setBackgroundColor(Color.LTGRAY);
                    historyContainer.addView(divider);
                }
            }
        }

        findViewById(R.id.btn_back_home).setOnClickListener(v -> finish());
    }
}
