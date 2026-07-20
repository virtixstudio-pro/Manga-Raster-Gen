package org.virtix.rasterxstudio;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;

public class MainActivity extends AppCompatActivity {

    private ImageView previewImageView;
    private Slider densitySlider;
    private TextView densityLabel;
    
    private String currentMode = "radial";
    private int densityValue = 50;
    
    private float touchX = 400f;
    private float touchY = 400f;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewImageView = findViewById(R.id.previewImageView);
        densitySlider = findViewById(R.id.densitySlider);
        densityLabel = findViewById(R.id.densityLabel);

        MaterialButton btnRadial = findViewById(R.id.btnModeRadial);
        MaterialButton btnDots = findViewById(R.id.btnModeDots);
        MaterialButton btnBox = findViewById(R.id.btnModeBox);
        MaterialButton btnExportNst = findViewById(R.id.btnExportNst);

        btnRadial.setOnClickListener(v -> { currentMode = "radial"; renderPreview(); });
        btnDots.setOnClickListener(v -> { currentMode = "dots"; renderPreview(); });
        btnBox.setOnClickListener(v -> { currentMode = "box"; renderPreview(); });

        densitySlider.addOnChangeListener((slider, value, fromUser) -> {
            densityValue = (int) value;
            densityLabel.setText("Densité / Lignes : " + densityValue);
            renderPreview();
        });

        previewImageView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                float viewWidth = previewImageView.getWidth();
                float viewHeight = previewImageView.getHeight();
                
                if (viewWidth > 0 && viewHeight > 0) {
                    touchX = (event.getX() / viewWidth) * 800f;
                    touchY = (event.getY() / viewHeight) * 800f;
                    renderPreview();
                }
            }
            return true;
        });

        btnExportNst.setOnClickListener(v -> generateNstFormat());

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
        paint.setColor(Color.BLACK);

        if (currentMode.equals("radial")) {
            paint.setStrokeWidth(3f);
            float maxR = (float) Math.hypot(width, height);

            for (int i = 0; i < densityValue; i++) {
                double angle = (2 * Math.PI / densityValue) * i;
                float x1 = touchX + (float) Math.cos(angle) * (maxR * 0.15f);
                float y1 = touchY + (float) Math.sin(angle) * (maxR * 0.15f);
                float x2 = touchX + (float) Math.cos(angle) * maxR;
                float y2 = touchY + (float) Math.sin(angle) * maxR;
                canvas.drawLine(x1, y1, x2, y2, paint);
            }
        } else if (currentMode.equals("dots")) {
            int spacing = Math.max(12, 600 / (densityValue + 1));
            for (int y = 20; y < height; y += spacing) {
                for (int x = 20; x < width; x += spacing) {
                    canvas.drawCircle(x, y, 4f, paint);
                }
            }
        } else if (currentMode.equals("box")) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(12f);
            canvas.drawRect(40, 40, width - 40, height - 40, paint);
        }

        previewImageView.setImageBitmap(bitmap);
    }

    private void generateNstFormat() {
        String nstContent = "RASTERX_STUDIO_NST_V1\n" +
                "ECOSYSTEM:VIRTIX\n" +
                "MODE:" + currentMode + "\n" +
                "CENTER_X:" + touchX + "\n" +
                "CENTER_Y:" + touchY + "\n" +
                "DENSITY:" + densityValue + "\n" +
                "END_OF_BLOCK";

        Toast.makeText(this, "Format .nst généré pour l'écosystème !", Toast.LENGTH_LONG).show();
    }
}
