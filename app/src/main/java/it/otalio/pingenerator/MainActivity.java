package it.otalio.pingenerator;

import android.app.Activity;
import android.os.Bundle;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.security.SecureRandom;

public class MainActivity extends Activity {
    private final SecureRandom random = new SecureRandom();
    private TextView pinText, countText;
    private CheckBox allowZero;
    private SeekBar digitsBar;
    private String currentPin = "";
    private SharedPreferences preferences;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        preferences = getSharedPreferences("settings", MODE_PRIVATE);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(36), dp(24), dp(24));
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(0xFFF6F8FC);

        TextView title = new TextView(this);
        title.setText("PIN Generator");
        title.setTextSize(26);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(0xFF15263B);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Generatore numerico sicuro • Offline");
        subtitle.setTextSize(14);
        subtitle.setPadding(0, dp(8), 0, dp(35));
        root.addView(subtitle);

        pinText = new TextView(this);
        pinText.setTextSize(36);
        pinText.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        pinText.setTextColor(0xFF183B72);
        pinText.setGravity(Gravity.CENTER);
        pinText.setPadding(dp(8), dp(30), dp(8), dp(30));
        root.addView(pinText, new LinearLayout.LayoutParams(-1, dp(125)));

        Button generate = new Button(this);
        generate.setText("GENERA NUOVO PIN");
        root.addView(generate, new LinearLayout.LayoutParams(-1, -2));
        generate.setOnClickListener(v -> generatePin());

        Button copy = new Button(this);
        copy.setText("COPIA PIN");
        root.addView(copy, new LinearLayout.LayoutParams(-1, -2));
        copy.setOnClickListener(v -> {
            if (currentPin.isEmpty()) return;
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("PIN", currentPin));
            Toast.makeText(this, "PIN copiato", Toast.LENGTH_SHORT).show();
        });

        countText = new TextView(this);
        countText.setTextSize(17);
        countText.setPadding(0, dp(34), 0, dp(10));
        root.addView(countText);

        digitsBar = new SeekBar(this);
        digitsBar.setMax(12);
        digitsBar.setProgress(preferences.getInt("digits", 6) - 4);
        root.addView(digitsBar, new LinearLayout.LayoutParams(-1, -2));

        allowZero = new CheckBox(this);
        allowZero.setText("Consenti zero come prima cifra");
        allowZero.setChecked(preferences.getBoolean("zero", true));
        root.addView(allowZero);

        digitsBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar bar, int value, boolean fromUser) {
                int digits = value + 4;
                countText.setText("Numero di cifre: " + digits);
                if (fromUser) { preferences.edit().putInt("digits", digits).apply(); generatePin(); }
            }
            @Override public void onStartTrackingTouch(SeekBar bar) {}
            @Override public void onStopTrackingTouch(SeekBar bar) {}
        });
        allowZero.setOnCheckedChangeListener((button, checked) -> {
            preferences.edit().putBoolean("zero", checked).apply();
            generatePin();
        });
        countText.setText("Numero di cifre: " + (digitsBar.getProgress() + 4));
        setContentView(root);
        generatePin();
    }

    private void generatePin() {
        int digits = digitsBar.getProgress() + 4;
        StringBuilder pin = new StringBuilder(digits);
        for (int i = 0; i < digits; i++) {
            pin.append(i == 0 && !allowZero.isChecked() ? random.nextInt(9) + 1 : random.nextInt(10));
        }
        currentPin = pin.toString();
        pinText.setText(currentPin);
        pinText.setTextSize(digits > 12 ? 23 : digits > 8 ? 28 : 36);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
