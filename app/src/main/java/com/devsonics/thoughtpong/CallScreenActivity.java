package com.devsonics.thoughtpong;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.devsonics.thoughtpong.custumviews.RipplePulseLayout;
import com.devsonics.thoughtpong.custumviews.WaveView;
import com.devsonics.thoughtpong.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallScreenActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private CircleImageView callIcon, profilePic;
    private TextView callTimerOrWaitingText;
    private ImageButton btnSpeaker, btnEndCall, btnMute;
    private RipplePulseLayout ripplePulseLayout;
    private WaveView waveView;

    private Handler callTimerHandler = new Handler();
    private Runnable callTimerRunnable;
    private long startTime;

    private MediaPlayer mediaPlayer;
    private Visualizer visualizer;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.bg_status_bar));
        }


        callIcon = findViewById(R.id.icon_call);
        profilePic = findViewById(R.id.profile_pic);
        callTimerOrWaitingText = findViewById(R.id.call_timer_or_waiting);
        btnSpeaker = findViewById(R.id.btn_speaker);
        btnEndCall = findViewById(R.id.btn_end_call);
        btnMute = findViewById(R.id.btn_mute);
        ripplePulseLayout = findViewById(R.id.ripple_pulse_layout);
        waveView = findViewById(R.id.sound_waves);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        ripplePulseLayout.startRippleAnimation();
        callTimerOrWaitingText.setText("Waiting....");
        profilePic.setVisibility(View.GONE);
        callIcon.setVisibility(View.VISIBLE);

        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ripplePulseLayout.stopRippleAnimation();
                startCallTimer();
                profilePic.setVisibility(View.VISIBLE);
                callIcon.setVisibility(View.GONE);
                playAudioFile();
            }
        }, 3000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAccepted) finish();
    }

    private void startCallTimer() {
        startTime = System.currentTimeMillis();

        callTimerRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - startTime;
                updateTimerUI(elapsedTime);
                callTimerHandler.postDelayed(this, 1000);
            }
        };

        callTimerHandler.post(callTimerRunnable);
    }

    private void updateTimerUI(long elapsedTime) {
        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) (elapsedTime / (1000 * 60)) % 60;
        int hours = (int) (elapsedTime / (1000 * 60 * 60)) % 24;

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        callTimerOrWaitingText.setText(timeString);
    }

    private void stopCallTimer() {
        callTimerHandler.removeCallbacks(callTimerRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (visualizer != null) {
            visualizer.setEnabled(false);
        }
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (visualizer != null) {
            visualizer.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCallTimer();
        if (visualizer != null) {
            visualizer.release();
            visualizer = null;
        }
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } finally {
                mediaPlayer = null;
            }
        }
    }

    private void playAudioFile() {
        mediaPlayer = MediaPlayer.create(this, R.raw.test_audio);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                setupVisualizerFxAndUI();
            }
        });
    }

    private void setupVisualizerFxAndUI() {
        try {
            visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

            Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                    float[] waveData = new float[bytes.length];
                    for (int i = 0; i < bytes.length; i++) {
                        waveData[i] = bytes[i] / 128.0f;
                    }
                    waveView.updateWaveData(waveData);
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                    // Not used
                }
            };

            visualizer.setDataCaptureListener(captureListener, Visualizer.getMaxCaptureRate() / 2, true, false);
            visualizer.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Visualizer", "Error setting up Visualizer: " + e.getMessage());
        }
    }

    private void endCall() {
        stopCallTimer();
        if (visualizer != null) {
            visualizer.release();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        startActivity(new Intent(CallScreenActivity.this, MainActivity.class));
        finish();
    }
}
