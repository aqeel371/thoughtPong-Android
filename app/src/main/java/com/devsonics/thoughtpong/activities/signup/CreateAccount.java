package com.devsonics.thoughtpong.activities.signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import android.widget.Toast;

import com.devsonics.thoughtpong.MainActivity;
import com.devsonics.thoughtpong.R;
import com.devsonics.thoughtpong.activities.login.Login;
import com.devsonics.thoughtpong.activities.verification.VerificationViewModel;
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseLogin;
import com.devsonics.thoughtpong.retofit_api.request_model.RequestSignUp;
import com.devsonics.thoughtpong.utils.Loader;
import com.devsonics.thoughtpong.utils.NetworkResult;
import com.devsonics.thoughtpong.utils.SharedPreferenceManager;

public class CreateAccount extends AppCompatActivity {
    EditText etFullName, etEmail;
    Button btCreateAccount;
    TextView btLogin;
    private String fullName, email;

    CreateAccountViewModel viewModel;
    Observer<? super NetworkResult<ResponseLogin>> signUpObserver;
    private String phoneNumber;
    Loader loader;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /**
         **Initialize ViewModel with Factory**
         */

        ViewModelProvider.Factory factory = CreateAccountViewModel.Companion.createFactory(getApplication());
        viewModel = new ViewModelProvider(this, factory).get(CreateAccountViewModel.class);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.transperant));
        }

        loader = new Loader(this);
        if (getIntent().getStringExtra("phoneNumber") != null)
            phoneNumber = getIntent().getStringExtra("phoneNumber");

        initObserver();


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
                if (phoneNumber == null || phoneNumber.isEmpty()) {
                    Toast.makeText(CreateAccount.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                viewModel.signUpApi(new RequestSignUp(email, fullName, phoneNumber));



            }
        });
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccount.this, Login.class));
                finish();
            }
        });
    }

    private void initObserver() {
        signUpObserver = (Observer<NetworkResult<ResponseLogin>>) responseLoginNetworkResult -> {
            if (responseLoginNetworkResult instanceof NetworkResult.Loading) {

                loader.showProgress();
            } else if (responseLoginNetworkResult instanceof NetworkResult.Success) {

                ResponseLogin responseLogin = responseLoginNetworkResult.getData();

                if (responseLogin != null) {

                    SharedPreferenceManager.INSTANCE.setUserLogin(true);
                    SharedPreferenceManager.INSTANCE.setUserData(responseLogin.getData());
                    SharedPreferenceManager.INSTANCE.setAccessToken(responseLogin.getToken());
                    SharedPreferenceManager.INSTANCE.setRefreshToken(responseLogin.getRefreshToken());
                    Intent intent = new Intent("VERIFICATION_COMPLETE");
                    sendBroadcast(intent);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finishAffinity();
                } else {
                    loader.showDialogMessage(responseLoginNetworkResult.getMessage(), getSupportFragmentManager());
                }
                loader.hideProgress();

            } else if (responseLoginNetworkResult instanceof NetworkResult.Error) {

                loader.hideProgress();

                loader.showDialogMessage(responseLoginNetworkResult.getMessage(), getSupportFragmentManager());


            }
        };
        viewModel.getSignUpLiveData().observe(this, signUpObserver);
    }


}