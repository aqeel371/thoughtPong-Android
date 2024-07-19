package com.devsonics.thoughtpong;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Switch;

import com.devsonics.thoughtpong.activities.login.Login;
import com.devsonics.thoughtpong.utils.SharedPreferenceManager;
import com.google.firebase.auth.FirebaseAuth;


public class SettingFragment extends Fragment {
    SeekBar seekBarVolume;
    Switch aSwitchStartupVolume;
    ConstraintLayout btnPrivacyPolicy, btnTermsConditions, btnContactUs, btnShareApp, btnLogout;


    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        seekBarVolume = view.findViewById(R.id.seekbar_volume);
        aSwitchStartupVolume = view.findViewById(R.id.sw_start_sound);
        btnPrivacyPolicy = view.findViewById(R.id.btn_privacy_policy);
        btnTermsConditions = view.findViewById(R.id.btn_terms_conditions);
        btnContactUs = view.findViewById(R.id.btn_contact_us);
        btnShareApp = view.findViewById(R.id.btn_share_app);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PrivacyPolicy.class));
            }
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            SharedPreferenceManager.INSTANCE.setUserData(null);
            SharedPreferenceManager.INSTANCE.setUserLogin(false);
            startActivity(new Intent(requireActivity(), Login.class));
            requireActivity().finishAffinity();
        });

        return view;
    }
}