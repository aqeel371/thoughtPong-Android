package com.devsonics.thoughtpong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;
import com.devsonics.thoughtpong.R;

public class Login extends AppCompatActivity {
    CountryCodePicker ccp;
    EditText editTextCarrierNumber;
    String phoneNumber;
    Button logIn;
    TextView creatAccount;
    private BroadcastReceiver verificationCompleteReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.transperant));
        }
        ccp = findViewById(R.id.cc_picker);
        logIn = findViewById(R.id.btn_logIn);
        creatAccount = findViewById(R.id.btn_creat_account);
        editTextCarrierNumber = findViewById(R.id.et_phone);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = ccp.getFullNumberWithPlus(); // Phone Number extracted here
                if (editTextCarrierNumber.getText().toString().isEmpty()) {
                    editTextCarrierNumber.setError("Please Enter Phone Number");
                } else {
                    Intent intent = new Intent(Login.this, Verification.class);
                    startActivity(intent);
                }
            }
        });

        creatAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, CreateAccount.class));
                finish();
            }
        });

        // Register the broadcast receiver
        verificationCompleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish(); // Finish Login activity when broadcast is received
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(verificationCompleteReceiver, new IntentFilter("VERIFICATION_COMPLETE"), RECEIVER_EXPORTED);
        }else {
            registerReceiver(verificationCompleteReceiver, new IntentFilter("VERIFICATION_COMPLETE"));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the broadcast receiver
        if (verificationCompleteReceiver != null) {
            unregisterReceiver(verificationCompleteReceiver);
        }
    }
}
