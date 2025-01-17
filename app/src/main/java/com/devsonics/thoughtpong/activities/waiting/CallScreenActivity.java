package com.devsonics.thoughtpong.activities.waiting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.devsonics.thoughtpong.MainActivity;
import com.devsonics.thoughtpong.R;
import com.devsonics.thoughtpong.custumviews.RipplePulseLayout;
import com.devsonics.thoughtpong.custumviews.Tag;
import com.devsonics.thoughtpong.custumviews.WaveView;
import com.devsonics.thoughtpong.fragment.home.HomeViewModel;
import com.devsonics.thoughtpong.retofit_api.request_model.RequestCall;
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseCall;
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseGetTopic;
import com.devsonics.thoughtpong.retofit_api.response_model.UserData;
import com.devsonics.thoughtpong.utils.Loader;
import com.devsonics.thoughtpong.utils.NetworkResult;
import com.devsonics.thoughtpong.utils.SharedPreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;

public class CallScreenActivity extends AppCompatActivity {

    CallScreenViewModel viewModel;
    Loader loader;
    UserData otherUserData;


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
    private String channelName = null;
    private String selectedTag = null;
    private String token = null;
    private int userType = 0;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration user2Listener = null;
    private ListenerRegistration userListener = null;
    private final String listenerTag = "Listener";
    private boolean showAcceptRejectDialog = false;
    private boolean isSpeaker = false;
    private boolean isMute = false;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        /**
         **Initialize ViewModel with Factory**
         */

        ViewModelProvider.Factory factory = CallScreenViewModel.Companion.createFactory(getApplication());
        viewModel = new ViewModelProvider(this, factory).get(CallScreenViewModel.class);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.bg_status_bar));
        }

        loader = new Loader(this);

        initObserver();


        channelName = getIntent().getStringExtra("channelName");
        selectedTag = getIntent().getStringExtra("SelectedTag");
        token = getIntent().getStringExtra("token");
        userType = getIntent().getIntExtra("userType", 0);

        callIcon = findViewById(R.id.icon_call);
        profilePic = findViewById(R.id.profile_pic);
        callTimerOrWaitingText = findViewById(R.id.call_timer_or_waiting);
        btnSpeaker = findViewById(R.id.btn_speaker);
        btnEndCall = findViewById(R.id.btn_end_call);
        btnMute = findViewById(R.id.btn_mute);
        ripplePulseLayout = findViewById(R.id.ripple_pulse_layout);
        waveView = findViewById(R.id.sound_waves);

        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSpeaker = !isSpeaker;
                configSpeaker();
            }
        });
        btnMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMute = !isMute;
                configMic();
            }
        });

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

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ripplePulseLayout.stopRippleAnimation();
                startCallTimer();
                profilePic.setVisibility(View.VISIBLE);
                callIcon.setVisibility(View.GONE);
                playAudioFile();
            }
        }, 3000);*/

        initAgora();
        if (userType == 2) {
            listenerForUser2(channelName);
        } else if (userType == 1) {
            listenerForUser1(channelName);
            Toast.makeText(this, channelName, Toast.LENGTH_SHORT).show();
            joinChannel();
        }
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
    private void configSpeaker(){
        if(isSpeaker){
            btnSpeaker.setImageResource(R.drawable.ic_speaker_loud);
        } else {
            btnSpeaker.setImageResource(R.drawable.ic_speaker);
        }
        mRtcEngine.setEnableSpeakerphone(isSpeaker);
    }
    private void configMic(){
        if(isMute){
            btnMute.setImageResource(R.drawable.ic_open_speaker);
        } else {
            btnMute.setImageResource(R.drawable.ic_mute_speaker);
        }
        mRtcEngine.muteLocalAudioStream(isMute);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRtcEngine != null) {
            // Leave the channel
            mRtcEngine.leaveChannel();
            mRtcEngine = null;
            // Destroy the engine
            RtcEngine.destroy();
        }

       /* stopCallTimer();
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
        }*/
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

    private void endCall1() {
        stopCallTimer();
        if (visualizer != null) {
            visualizer.release();
        }
        visualizer = null;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        startActivity(new Intent(CallScreenActivity.this, MainActivity.class));
        finish();
    }

    private void endCall() {
        List<String> tagIds = Arrays.asList(selectedTag.split(","));
        Log.d("RequestCall", "End call RoomName: " + channelName + " tagIds: " + selectedTag + " params:" + tagIds);
        RequestCall requestCall = new RequestCall(channelName, tagIds);
        viewModel.endCall(requestCall);
        if (mRtcEngine != null) {
            // Leave the channel
            mRtcEngine.leaveChannel();
            mRtcEngine = null;
            // Destroy the engine
            RtcEngine.destroy();
        }

        /*stopCallTimer();
        if (visualizer != null) {
            visualizer.release();
        }
        visualizer = null;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        startActivity(new Intent(CallScreenActivity.this, MainActivity.class));
        finish();*/
    }

    private void listenerForUser1(String channelName) {

        final DocumentReference userRef = db.collection("waiting").document(channelName);

        if (userListener != null) {
            userListener.remove();
        }

        userListener = userRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("FirebaseFireStoreException", "Listen failed.", e);
                Toast.makeText(this, "User Listener Failed", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Boolean isAccepted = snapshot.getBoolean("isAccepted");
                Long users2Value = snapshot.getLong("Users2");
                Long users1Value = snapshot.getLong("Users1");

                if (users2Value != null) {
                    if (SharedPreferenceManager.INSTANCE.getUserData() != null) {
                        if (users2Value != SharedPreferenceManager.INSTANCE.getUserData().getId()) {
                            showAcceptRejectDialog = true;
                            viewModel.getUser(String.valueOf(users2Value));
                        }
                    }
                } /*else if (users1Value != null) {
                    System.out.println("Users1 field updated: " + users1Value);
                } else {
                    System.out.println("not present.");
                }*/
            } else {
                System.out.println("Current data: null");
            }
        });
    }

    private void listenerForUser2(String channelName) {
        final DocumentReference user2Ref = db.collection("waiting").document(channelName);

        if (user2Listener != null) {
            user2Listener.remove();
        }

        user2Listener = user2Ref.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("FirebaseFireStoreException", "Listen failed.", e);
                Toast.makeText(this, "User Listener Failed", Toast.LENGTH_SHORT).show();
                return;
            }
            Boolean isAccepted = snapshot.getBoolean("isAccepted");

            Long users2Value = snapshot.getLong("Users2");

            if (isAccepted != null) {
//                    if (users2Value != null) {
                //User 1 reject endCall for User 2

                if (!isAccepted) {

                    List<String> tagIds = Arrays.asList(selectedTag.split(","));
                    Log.d("RequestCall", "RoomName: " + channelName + " tagIds: " + selectedTag);
                    RequestCall requestCall = new RequestCall(channelName, tagIds);
                    viewModel.endCall(requestCall);
                } else {
                    //User 1 Accept the call
                    Toast.makeText(this, channelName + " call Accepted", Toast.LENGTH_SHORT).show();
                    joinChannel();
                }
//                    }
            }

        });
    }


    private void initObserver() {

        Observer<? super NetworkResult<ResponseCall>> user1EndCallObserver = (Observer<NetworkResult<ResponseCall>>) response -> {
            if (response instanceof NetworkResult.Loading) {

                loader.showProgress();
            } else if (response instanceof NetworkResult.Success) {
                Map<String, String> data = new HashMap<>();
//                data.put("NameT", channelName);
                data.put("NameT", generateRandomName());
                db.collection("collectionT").document("IDT").set(data).addOnSuccessListener(unused -> {
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "IDT Failure: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
                channelName = generateRandomName();

                List<String> tagIds = Arrays.asList(selectedTag.split(","));

                Log.d("RequestCall", "RoomName: " + channelName + " tagIds: " + selectedTag);


                RequestCall requestCall = new RequestCall(channelName, tagIds);
                /** RejoinCall
                 * */
                viewModel.callApi(requestCall);
//                data.put("NameT", channelName);
                data.put("NameT", generateRandomName());
                db.collection("collectionT").document("IDT").set(data).addOnSuccessListener(unused -> {
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "IDT Failure: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
                loader.hideProgress();
            } else if (response instanceof NetworkResult.Error) {
                Log.d("Error", response.getResponseCode() + " " + response.getMessage());
                loader.hideProgress();
                loader.showDialogMessage(response.getMessage(), getSupportFragmentManager());
            }

        };

        Observer<? super NetworkResult<ResponseCall>> endCallObserver = (Observer<NetworkResult<ResponseCall>>) response -> {
            if (response instanceof NetworkResult.Loading) {

                loader.showProgress();
            } else if (response instanceof NetworkResult.Success) {
                Map<String, String> data = new HashMap<>();
//                data.put("NameT", channelName);
                data.put("NameT", generateRandomName());
                db.collection("collectionT").document("IDT").set(data).addOnSuccessListener(unused -> {
                    loader.hideProgress();
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "IDT Failure: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else if (response instanceof NetworkResult.Error) {
                Log.d("Error", response.getResponseCode() + " " + response.getMessage());
                loader.hideProgress();
                loader.showDialogMessage(response.getMessage(), getSupportFragmentManager());
            }

        };

        Observer<? super NetworkResult<ResponseCall>> callObserver = (Observer<NetworkResult<ResponseCall>>) response -> {
            if (response instanceof NetworkResult.Loading) {

                loader.showProgress();
            } else if (response instanceof NetworkResult.Success) {

                Map<String, Long> user = new HashMap<>();

                if (response.getData().getUser() != null) {
                    /**
                     * User2
                     * */
                    user.put("Users2", SharedPreferenceManager.INSTANCE.getUserData().getId());
                    channelName = response.getData().getChannelName();
                    db.collection("waiting").document(channelName).set(user).addOnSuccessListener(unused -> {
                        listenerForUser2(channelName);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failure: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } else if (response.getData().getData() != null) {
                    /**User1
                     **/
                    user.put("Users1", SharedPreferenceManager.INSTANCE.getUserData().getId());
                    db.collection("waiting").document(channelName).set(user).addOnSuccessListener(unused -> {
                        listenerForUser1(channelName);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failure: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }

                loader.hideProgress();

            } else if (response instanceof NetworkResult.Error) {
                Log.d("GetTopicsApiResponse", response.getResponseCode() + " " + response.getMessage());
                loader.hideProgress();
                loader.showDialogMessage(response.getMessage(), getSupportFragmentManager());
            }

        };


        Observer<? super NetworkResult<UserData>> getUserObserver = (Observer<NetworkResult<UserData>>) response -> {
            if (response instanceof NetworkResult.Loading) {

                loader.showProgress();
            } else if (response instanceof NetworkResult.Success) {
                if (showAcceptRejectDialog) {
                    showAcceptRejectDialog(response.getData());
                } else {
                    otherUserData = response.getData();
                    UpdateOtherUserInf0();
                }

                loader.hideProgress();

            } else if (response instanceof NetworkResult.Error) {
                Log.d("GetTopicsApiResponse", response.getResponseCode() + " " + response.getMessage());

                loader.hideProgress();

                loader.showDialogMessage(response.getMessage(), this.getSupportFragmentManager());

            }
        };

        viewModel.getEndCallLiveData().observe(this, endCallObserver);
        viewModel.getCallLiveData().observe(this, callObserver);
        viewModel.getUser1EndCallLiveData().observe(this, user1EndCallObserver);
        viewModel.getUserLiveData().observe(this, getUserObserver);
    }

    private void UpdateOtherUserInf0() {

        callTimerOrWaitingText.setText(otherUserData.getFullName());
        if (otherUserData.getImage() != null && !otherUserData.getImage().isEmpty()) {
            String profileUrl = "https://bdpos.store/api/thought/Images/" + otherUserData.getImage();
            Glide.with(this).load(profileUrl).into(callIcon);
        }

    }

    private void showAcceptRejectDialog(UserData data) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(data.getFullName() + " wants to join").setPositiveButton("Accept", (dialog, which) -> {

                    Map<String, Boolean> isAccepted = new HashMap<>();
                    isAccepted.put("isAccepted", true);
                    db.collection("waiting").document(channelName).set(isAccepted).addOnSuccessListener(unused -> {
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to Accept : \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                    dialog.dismiss();
                }).setNegativeButton("Reject", (dialog, which) -> {

                    Map<String, Boolean> isAccepted = new HashMap<>();
                    isAccepted.put("isAccepted", false);
                    db.collection("waiting").document(channelName).set(isAccepted).addOnSuccessListener(unused -> {


                        List<String> tagIds = Arrays.asList(selectedTag.split(","));

                        Log.d("RequestCall", "RoomName: " + channelName + " tagIds: " + selectedTag);


                        RequestCall requestCall = new RequestCall(channelName, tagIds);
                        viewModel.user1EndCall(requestCall);

                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to Reject : \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                    dialog.dismiss();
                }).setCancelable(false) // Prevent dialog from being dismissed by clicking outside
                .show();
    }


    public static String generateRandomName() {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final int NAME_LENGTH = 8;
        final SecureRandom random = new SecureRandom();
        StringBuilder randomName = new StringBuilder(NAME_LENGTH);
        for (int i = 0; i < NAME_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            randomName.append(CHARACTERS.charAt(index));
        }
        return randomName.toString();
    }

    /**
     * Agora
     **/
    private RtcEngine mRtcEngine;

    private void initAgora() {
        // Create an RtcEngineConfig object and configure it
        RtcEngineConfig config = new RtcEngineConfig();
        config.mContext = getBaseContext();
        config.mAppId = "71e94636243f43799fa959a773e5712d";
        config.mEventHandler = mRtcEventHandler;
// Create and initialize the RtcEngine
        try {
            mRtcEngine = RtcEngine.create(config);
        } catch (Exception e) {
            Toast.makeText(this, "Agora Exception " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void joinChannel() {
        ChannelMediaOptions options = new ChannelMediaOptions();
// Set the user role to BROADCASTER or AUDIENCE according to the scenario
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
// In the live broadcast scenario, set the channel profile to BROADCASTING (live broadcast scenario)
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
// Publish the audio collected by the microphone
        options.publishMicrophoneTrack = true;
// Automatically subscribe to all audio streams
        options.autoSubscribeAudio = true;
// Use the temporary token to join the channel
// Specify the user ID yourself and ensure it is unique within the channel
        int userId = (int) SharedPreferenceManager.INSTANCE.getUserData().getId();
        mRtcEngine.joinChannel(/*token*/null, channelName, userId, options);
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        // Callback when successfully joining the channel
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            runOnUiThread(() -> {
                Toast.makeText(CallScreenActivity.this, "Join channel success", Toast.LENGTH_SHORT).show();
            });
        }

        // Callback when a remote user or host joins the current channel
        @Override
        // Listen for remote hosts in the channel to get the host's uid information
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            runOnUiThread(() -> {
                Toast.makeText(CallScreenActivity.this, "User joined: " + uid, Toast.LENGTH_SHORT).show();
                showAcceptRejectDialog = false;
                viewModel.getUser(String.valueOf(uid));
            });
        }

        // Callback when a remote user or host leaves the current channel
        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            runOnUiThread(() -> {
                endCall();

            });
        }
    };
}
