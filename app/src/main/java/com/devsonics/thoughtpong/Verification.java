package com.devsonics.thoughtpong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.devsonics.thoughtpong.R;

public class Verification extends AppCompatActivity {
    FloatingActionButton backButton;
    EditText otp1, otp2, otp3, otp4, otp5, otp6;
    Button verifyButton;
    TextView reSendButton;
    private String otp;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.transperant));
        }
        backButton = findViewById(R.id.btn_back);
        otp1 = findViewById(R.id.editText1);
        otp2 = findViewById(R.id.editText2);
        otp3 = findViewById(R.id.editText3);
        otp4 = findViewById(R.id.editText4);
        otp5 = findViewById(R.id.editText5);
        otp6 = findViewById(R.id.editText6);
        verifyButton = findViewById(R.id.btn_create_account);
        reSendButton = findViewById(R.id.btn_login);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        otp2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && otp2.getText().length() == 0) {
                    otp1.requestFocus();
                    return true;
                }
                return false;
            }
        });

        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    otp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        otp3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && otp3.getText().length() == 0) {
                    otp2.requestFocus();
                    return true;
                }
                return false;
            }
        });

        otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    otp5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        otp4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && otp4.getText().length() == 0) {
                    otp3.requestFocus();
                    return true;
                }
                return false;
            }
        });

        otp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    otp6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        otp5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && otp5.getText().length() == 0) {
                    otp4.requestFocus();
                    return true;
                }
                return false;
            }
        });


        otp6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        otp6.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && otp6.getText().length() == 0) {
                    otp5.requestFocus();
                    return true;
                }
                return false;
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp1.getText().toString().isEmpty() ||
                        otp2.getText().toString().isEmpty() ||
                        otp3.getText().toString().isEmpty() ||
                        otp4.getText().toString().isEmpty() ||
                        otp5.getText().toString().isEmpty() ||
                        otp6.getText().toString().isEmpty()) {
                    Toast.makeText(Verification.this, "Incomplete Verification Code", Toast.LENGTH_SHORT).show();
                    return;
                }
                otp = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString();
                // OTP verification code here


                // If OTP verification is successful, send broadcast and finish
                Intent intent = new Intent("VERIFICATION_COMPLETE");
                sendBroadcast(intent);
                finish();
                {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        });

        reSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Resend OTP code here
            }
        });
    }
}
