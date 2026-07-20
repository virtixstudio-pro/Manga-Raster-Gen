package org.virtix.mangarastergen;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;

public class MainActivity extends AppCompatActivity {

    private ImageView previewImageView;
    private Slider densitySlider;
    private TextView densityLabel;
    private EditText inputOverlayText;
    
    private String currentMode = "radial";
    private int densityValue = 50;
    private String customText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewImageView = findViewById(R.id.previewImageView);
        densitySlider = findViewById(R.id.densitySlider);
        densityLabel = findViewById(R.id.densityLabel);
        inputOverlayText = findViewById(R.id.inputOverlayText);

        MaterialButton btnRadial = findViewById(R.id.btnModeRadial);
        MaterialButton btnDots = findViewById(R.id.btnModeDots);
        MaterialButton btnBox = findViewById(R.id.btnModeBox);

        btnRadial.setOnClickListener(v -> { currentMode = "radial"; renderPreview(); });
        btnDots.setOnClickListener(v -> { currentMode = "dots"; renderPreview(); });
        btnBox.setOnClickListener(v -> { currentMode = "box"; renderPreview(); });

        densitySlider.addOnChangeListener((slider, value, fromUser) -> {
            densityValue = (int) value;
            densityLabel.setText("Densité / Lignes : " + densityValue);
            renderPreview();
        });

        inputOverlayText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                customText = s.toString();
                renderPreview();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        renderPreview();
    }

    private void renderPreview() {
        int width = 800;
        int height = 800;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        if (currentMode.equals("radial")) {
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(3f);
            float cx = width / 2f;
            float cy = height / 2f;
            float maxR = (float) Math.hypot(cx, cy);

            for (int i = 0; i < densityValue; i++) {
                double angle = (2 * Math.PI / densityValue) * i;
                float x1 = cx + (float) Math.cos(angle) * (maxR * 0.25f);
                float y1 = cy + (float) Math.sin(angle) * (maxR * 0.25f);
                float x2 = cx + (float) Math.cos(angle) * maxR;
                float y2 = cy + (float) Math.sin(angle) * maxR;
                canvas.drawLine(x1, y1, x2, y2, paint);
            }
        } else if (currentMode.equals("dots")) {
            paint.setColor(Color.BLACK);
            int spacing = Math.max(12, 600 / (densityValue + 1));
            for (int y = 20; y < height; y += spacing) {
                for (int x = 20; x < width; x += spacing) {
                    canvas.drawCircle(x, y, 4f, paint);
                }
            }
        } else if (currentMode.equals("box")) {
            // Tracé d'une case Manga stylisée (cadre épais)
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(12f);
            canvas.drawRect(40, 40, width - 40, height - 40, paint);

            // Texte superposé
            if (!customText.isEmpty()) {
                Paint textPaint = new Paint();
                textPaint.setColor(Color.BLACK);
                textPaint.setTextSize(48f);
                textPaint.setAntiAlias(true);
                textPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(customText, width / 2f, height / 2f, textPaint);
            }
        }

        previewImageView.setImageBitmap(bitmap);
    }
}
