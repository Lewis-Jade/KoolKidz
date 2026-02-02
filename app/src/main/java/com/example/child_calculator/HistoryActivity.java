package com.example.child_calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Map;
import java.util.TreeMap;

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
            emptyTv.setGravity(Gravity.CENTER);
            if (kidFont != null) emptyTv.setTypeface(kidFont);
            historyContainer.addView(emptyTv);
        } else {
            // Sort entries to show latest first
            TreeMap<String, Object> sortedEntries = new TreeMap<>((a, b) -> b.compareTo(a));
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                if (entry.getKey().startsWith("history_")) {
                    sortedEntries.put(entry.getKey(), entry.getValue());
                }
            }

            for (Map.Entry<String, Object> entry : sortedEntries.entrySet()) {
                String data = entry.getValue().toString();
                String[] parts = data.split(" \\| ");
                
                String name = "Hero";
                String scoreStr = "0/0";
                String mode = "EASY";

                if (parts.length >= 3) {
                    name = parts[0];
                    scoreStr = parts[1];
                    mode = parts[2];
                }

                // Calculate if it's an excellent score (>= 90%)
                boolean excellent = false;
                try {
                    String[] scoreParts = scoreStr.split("/");
                    int s = Integer.parseInt(scoreParts[0]);
                    int t = Integer.parseInt(scoreParts[1]);
                    if (t > 0 && (s * 100 / t) >= 90) excellent = true;
                } catch (Exception ignored) {}

                // Create a sleek Card for each history item
                CardView card = new CardView(this);
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                cardParams.setMargins(0, 8, 0, 16);
                card.setLayoutParams(cardParams);
                card.setRadius(24f);
                card.setCardElevation(6f);
                card.setUseCompatPadding(true);

                LinearLayout cardContent = new LinearLayout(this);
                cardContent.setOrientation(LinearLayout.VERTICAL);
                cardContent.setPadding(32, 24, 32, 24);
                cardContent.setBackgroundColor(Color.WHITE);

                // Row 1: Name and Mode
                LinearLayout row1 = new LinearLayout(this);
                row1.setOrientation(LinearLayout.HORIZONTAL);
                
                TextView tvName = new TextView(this);
                tvName.setText(name);
                tvName.setTextSize(20f);
                tvName.setTextColor(Color.parseColor("#FF6A1B9A"));
                tvName.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1));
                if (kidFont != null) tvName.setTypeface(kidFont);

                TextView tvMode = new TextView(this);
                tvMode.setText(mode);
                tvMode.setTextSize(14f);
                tvMode.setPadding(16, 4, 16, 4);
                tvMode.setTextColor(Color.WHITE);
                tvMode.setBackgroundResource(R.drawable.btb_background);
                if (mode.equals("HARD")) tvMode.getBackground().setTint(Color.parseColor("#FFE57373"));
                else if (mode.equals("MEDIUM")) tvMode.getBackground().setTint(Color.parseColor("#FFFFB74D"));
                else tvMode.getBackground().setTint(Color.parseColor("#FF81C784"));
                if (kidFont != null) tvMode.setTypeface(kidFont);

                row1.addView(tvName);
                row1.addView(tvMode);

                // Row 2: Score and Star
                LinearLayout row2 = new LinearLayout(this);
                row2.setOrientation(LinearLayout.HORIZONTAL);
                row2.setGravity(Gravity.CENTER_VERTICAL);
                row2.setPadding(0, 8, 0, 0);

                TextView tvScore = new TextView(this);
                tvScore.setText("Score: " + scoreStr);
                tvScore.setTextSize(18f);
                tvScore.setTextColor(Color.parseColor("#FF039BE5"));
                if (kidFont != null) tvScore.setTypeface(kidFont);

                row2.addView(tvScore);

                if (excellent) {
                    TextView tvStar = new TextView(this);
                    tvStar.setText(" ⭐ EXCELLENT!");
                    tvStar.setTextSize(16f);
                    tvStar.setTextColor(Color.parseColor("#FFFFD600"));
                    if (kidFont != null) tvStar.setTypeface(kidFont);
                    row2.addView(tvStar);
                }

                cardContent.addView(row1);
                cardContent.addView(row2);
                card.addView(cardContent);
                historyContainer.addView(card);
            }
        }

        findViewById(R.id.btn_back_home).setOnClickListener(v -> finish());
    }
}
