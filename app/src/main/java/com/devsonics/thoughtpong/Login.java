package com.devsonics.thoughtpong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devsonics.thoughtpong.token_manager.TokenManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {
    CountryCodePicker ccp;
    EditText editTextCarrierNumber;
    String phoneNumber;
    Button logIn;
    TextView creatAccount;
    private BroadcastReceiver verificationCompleteReceiver;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String LogInTAG = "LogInTAG";
    private String phNum = "";
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.transperant));
        }

        mAuth = FirebaseAuth.getInstance();
        initAuthCallback();

        initProgressDialog();


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
                    if (!validatePhoneNumber()) {
                        Toast.makeText(Login.this, "Invalid Number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String countryCode = ccp.getSelectedCountryCodeWithPlus();
                    String phoneNumber = editTextCarrierNumber.getText().toString().replace(" ", "");
                    phNum = countryCode + phoneNumber;
                    Log.d(LogInTAG, "phone Number = " + phNum);
                    showProgress();
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(phNum)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(Login.this)                 // (optional) Activity for callback binding
                                    // If no activity is passed, reCAPTCHA verification can not be used.
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);

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
        } else {
            registerReceiver(verificationCompleteReceiver, new IntentFilter("VERIFICATION_COMPLETE"));
        }

    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
    }

    private void showProgress() {
        if (progressDialog == null)
            initProgressDialog();
        progressDialog.show();
    }

    private void hideProgress() {
        if (progressDialog == null)
            initProgressDialog();
        progressDialog.hide();
    }


    private void initAuthCallback() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(LogInTAG, "Verification Complete");
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                hideProgress();
                Log.d(LogInTAG, "Verification Failed = " + e.getMessage());
                Toast.makeText(Login.this, "Verification failed\n" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                Log.d(LogInTAG, "Code Sent");
                hideProgress();
                Intent intent = new Intent(Login.this, Verification.class);
                intent.putExtra("verificationId", verificationId);
                intent.putExtra("phoneNumber", phNum);
                TokenManager.getInstance().setForceResendingToken(forceResendingToken);
                startActivity(intent);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                hideProgress();
                Log.d(LogInTAG, "Time out");
            }
        };
    }

    void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgress();
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(Login.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Login.this, "Invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgress();
                    }
                });
    }

    private Boolean validatePhoneNumber() {
        String countryCode = ccp.getSelectedCountryCode();
        String phoneNumber = editTextCarrierNumber.getText().toString();

        return isValidPhoneNumber(phoneNumber, countryCode);
    }

    private boolean isValidPhoneNumber(String phoneNumber, String countryCode) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneNumberUtil.parse("+" + countryCode + phoneNumber, null);
            return phoneNumberUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            Log.d(LogInTAG, "Invalid Phone Number");
            e.printStackTrace();
            return false;
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
