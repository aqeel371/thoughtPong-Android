package com.devsonics.thoughtpong.custumviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class WaveView extends View {
    private Paint paint;
    private float[] waveData;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStrokeWidth(15); // Set the stroke width
        paint.setStyle(Paint.Style.STROKE);
    }

    public void updateWaveData(float[] waveData) {
        this.waveData = waveData;
        invalidate(); // Request to redraw the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (waveData == null) {
            return;
        }

        int width = getWidth();
        int height = getHeight();
        int centerY = height / 2;
        int halfWidth = width / 2;

        float maxAmplitude = 1.0f; // Normalized maximum amplitude

        // Apply gradient from left (#FF7979) to right (#A6A5A5)
        LinearGradient gradient = new LinearGradient(
                0, 0, width, 0,
                Color.parseColor("#FF7979"),
                Color.parseColor("#A6A5A5"),
                Shader.TileMode.CLAMP
        );
        paint.setShader(gradient);

        // Calculate the number of samples to display based on view width
        int numSamples = Math.min(waveData.length, halfWidth / 25); // Adjust 25 to change the density of the lines
        float sampleStep = (float) waveData.length / numSamples;

        // Draw the sound waves with gradient
        for (int i = 0; i < numSamples; i++) {
            int sampleIndex = Math.round(i * sampleStep);
            float xLeft = (float) i / (numSamples - 1) * halfWidth;
            float yLeft = (waveData[sampleIndex] / maxAmplitude) * centerY;

            float xRight = halfWidth + (float) i / (numSamples - 1) * halfWidth;
            float yRight = (waveData[sampleIndex] / maxAmplitude) * centerY;

            // Left side lines
            canvas.drawLine(xLeft, centerY, xLeft, centerY - yLeft, paint);

            // Right side lines
            canvas.drawLine(xRight, centerY, xRight, centerY - yRight, paint);
        }
    }
}
