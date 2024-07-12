package com.devsonics.thoughtpong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.devsonics.thoughtpong.R;

public class CreateAccount extends AppCompatActivity {
    EditText etFullName,etEmail;
    Button btCreateAccount;
    TextView btLogin;
    private String fullName,email;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.transperant));
        }
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        btCreateAccount = findViewById(R.id.btn_create_account);
        btLogin = findViewById(R.id.btn_login);
        btCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullName = etFullName.getText().toString();
                email = etEmail.getText().toString();
                if (fullName.isEmpty()) {
                    etFullName.setError("Full name is required");
                    etFullName.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    etEmail.setError("Email is required");
                    etEmail.requestFocus();
                    return;
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmail.setError("Enter a valid email address");
                    etEmail.requestFocus();
                    return;
                }

                // Here you can add your logic to create the account or perform login
                startActivity(new Intent(CreateAccount.this,MainActivity.class));
                finish();


            }
        });
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccount.this,Login.class));
                finish();
            }
        });
    }


}