package com.devsonics.thoughtpong.activities.verification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.devsonics.thoughtpong.activities.signup.CreateAccount;
import com.devsonics.thoughtpong.MainActivity;
import com.devsonics.thoughtpong.R;
import com.devsonics.thoughtpong.retofit_api.request_model.RequestLogin;
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseLogin;
import com.devsonics.thoughtpong.token_manager.TokenManager;
import com.devsonics.thoughtpong.utils.Loader;
import com.devsonics.thoughtpong.utils.NetworkResult;
import com.devsonics.thoughtpong.utils.SharedPreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Verification extends AppCompatActivity {
    FloatingActionButton backButton;
    EditText otp1, otp2, otp3, otp4, otp5, otp6;
    Button verifyButton;
    TextView reSendButton;
    private String otp;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String VerificationTAG = "VerificationTAG";
    private String verificationId = "", phoneNumber = "";
    VerificationViewModel viewModel;
    Observer<? super NetworkResult<ResponseLogin>> loginObserver;
    Loader loader;


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
        loader = new Loader(this);

        /**
         **Initialize ViewModel with Factory**
         */

        ViewModelProvider.Factory factory = VerificationViewModel.Companion.createFactory(getApplication());
        viewModel = new ViewModelProvider(this, factory).get(VerificationViewModel.class);


        initObserver();

        verificationId = getIntent().getStringExtra("verificationId");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        mAuth = FirebaseAuth.getInstance();
        initAuthCallback();


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
                if (otp1.getText().toString().isEmpty() || otp2.getText().toString().isEmpty() || otp3.getText().toString().isEmpty() || otp4.getText().toString().isEmpty() || otp5.getText().toString().isEmpty() || otp6.getText().toString().isEmpty()) {
                    loader.showDialogMessage("Incomplete Verification Code", getSupportFragmentManager());
                    return;
                }
                otp = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString() + otp5.getText().toString() + otp6.getText().toString();
                // OTP verification code here
                if (verificationId == null || verificationId.isEmpty()) {
                    loader.showDialogMessage("Unable to fetch verificationId", getSupportFragmentManager());
                    return;
                }
                loader.showProgress();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                signInWithPhoneAuthCredential(credential);

            }
        });

        reSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(phoneNumber, TokenManager.getInstance().getForceResendingToken());
            }
        });
    }

    private void initObserver() {
        loginObserver = (Observer<NetworkResult<ResponseLogin>>) responseLoginNetworkResult -> {
            if (responseLoginNetworkResult instanceof NetworkResult.Loading) {

                loader.showProgress();
            } else if (responseLoginNetworkResult instanceof NetworkResult.Success) {

                ResponseLogin responseLogin = responseLoginNetworkResult.getData();

                if (responseLogin != null) {
                    if (responseLogin.getData() != null) {
                        SharedPreferenceManager.INSTANCE.setUserLogin(true);
                        SharedPreferenceManager.INSTANCE.setUserData(responseLogin.getData());
                        SharedPreferenceManager.INSTANCE.setAccessToken(responseLogin.getToken());
                        SharedPreferenceManager.INSTANCE.setRefreshToken(responseLogin.getRefreshToken());
                        Intent intent = new Intent("VERIFICATION_COMPLETE");
                        sendBroadcast(intent);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finishAffinity();
                    }else {
                        navigateToCreateAccount();
                    }
                } else {
                    navigateToCreateAccount();
                }
                loader.hideProgress();


            } else if (responseLoginNetworkResult instanceof NetworkResult.Error) {

                loader.hideProgress();

                if (responseLoginNetworkResult.getResponseCode() != null && responseLoginNetworkResult.getResponseCode() == 401) {
                    Intent intent = new Intent(getApplicationContext(), CreateAccount.class);
                    intent.putExtra("phoneNumber", phoneNumber);
                    startActivity(intent);
                    finishAffinity();
                } else {
                    loader.showDialogMessage(responseLoginNetworkResult.getMessage(), getSupportFragmentManager());
                }

            }
        };
        viewModel.getLoginLiveData().observe(this, loginObserver);
    }

    private void navigateToCreateAccount() {
        Intent intent = new Intent(getApplicationContext(), CreateAccount.class);
        intent.putExtra("phoneNumber", phoneNumber);
        startActivity(intent);
        finishAffinity();
    }

    void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // If OTP verification is successful, send broadcast and finish
                    viewModel.loginApi(new RequestLogin(phoneNumber));
                } else {
                    loader.hideProgress();
                    // Sign in failed, display a message and update the UI
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        loader.showDialogMessage("Invalid Code", getSupportFragmentManager());
                    } else {
                        loader.showDialogMessage("Signing Failed", getSupportFragmentManager());
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loader.hideProgress();
            }
        });
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        loader.showProgress();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void initAuthCallback() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(VerificationTAG, "Verification Complete");
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loader.hideProgress();
                Log.d(VerificationTAG, "Verification Failed = " + e.getMessage());
                loader.showDialogMessage("Verification Failed", getSupportFragmentManager());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                Log.d(VerificationTAG, "Code Sent");
                loader.hideProgress();
                loader.showDialogMessage("Code has been Resend", getSupportFragmentManager());
                Verification.this.verificationId = verificationId;
                TokenManager.getInstance().setForceResendingToken(forceResendingToken);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                loader.hideProgress();
                Log.d(VerificationTAG, "Time out");
            }
        };
    }


}
