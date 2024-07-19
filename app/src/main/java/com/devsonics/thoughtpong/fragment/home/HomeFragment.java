package com.devsonics.thoughtpong.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.devsonics.thoughtpong.activities.waiting.CallScreenActivity;
import com.devsonics.thoughtpong.MainActivity;
import com.devsonics.thoughtpong.custumviews.Tag;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import com.devsonics.thoughtpong.R;
import com.devsonics.thoughtpong.retofit_api.request_model.RequestCall;
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseCall;
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseGetTopic;
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseRefreshCall;
import com.devsonics.thoughtpong.utils.Loader;
import com.devsonics.thoughtpong.utils.NetworkResult;
import com.devsonics.thoughtpong.utils.SharedPreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class HomeFragment extends Fragment {
    LinearLayout letTalkButton;
    TextView tvProfileName, tvInfo;
    CircleImageView ivProfile;

    private ConstraintLayout tagContainer;
    private List<Tag> tagList = new ArrayList();
    private static final int COLUMNS = 3;

    HomeViewModel viewModel;
    private MainActivity mActivity;
    Observer<? super NetworkResult<ResponseGetTopic>> topicsObserver;
    Loader loader;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String randomName = null;
    private String randomNameForUser2 = null;
    private ListenerRegistration idtListener = null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        /**
         **Initialize ViewModel with Factory**
         */

        ViewModelProvider.Factory factory = HomeViewModel.Companion.createFactory(mActivity.getApplication());
        viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        loader = new Loader(mActivity);


        initObserver();
        initIDTListener();


        tagContainer = view.findViewById(R.id.tag_container);
        letTalkButton = view.findViewById(R.id.let_talk_button);
        tvProfileName = view.findViewById(R.id.tv_profile_name); //set Name of user
        tvInfo = view.findViewById(R.id.tv_info);                 //set info of user
        ivProfile = view.findViewById(R.id.image_view_profile);    //set Profile Picture of user

        letTalkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Tag> selectedTags = getSelectedTags();
                if (selectedTags.isEmpty()) {
                    Toast.makeText(getActivity(), "Please select at least one Topic", Toast.LENGTH_SHORT).show();
                } else {
                    handleLetsTalkButtonClick(selectedTags);
                }
            }
        });
        String text = SharedPreferenceManager.INSTANCE.getUserData().getFullName();
        tvProfileName.setText("Hello, " + text);

        if (SharedPreferenceManager.INSTANCE.getUserData().getImage() != null && !SharedPreferenceManager.INSTANCE.getUserData().getImage().isEmpty()) {
            String profileUrl = "https://bdpos.store/api/thought/Images/" + SharedPreferenceManager.INSTANCE.getUserData().getImage();
            Glide.with(mActivity).load(profileUrl).into(ivProfile);
        }


        viewModel.getTopicsApi();


        return view;
    }

    private void initObserver() {
        topicsObserver = (Observer<NetworkResult<ResponseGetTopic>>) response -> {
            if (response instanceof NetworkResult.Loading) {

                loader.showProgress();
            } else if (response instanceof NetworkResult.Success) {
                tagList.clear();

                for (ResponseGetTopic.ResponseGetTopicItem item : response.getData()) {
                    tagList.add(new Tag(item.getId(), item.getName(), R.drawable.ic_photography, Uri.parse("https://bdpos.store/api/thought/Images/" + item.getIcon())));
                }
                viewModel.refreshCall();
//                populateTagContainer();

                loader.hideProgress();

            } else if (response instanceof NetworkResult.Error) {
                Log.d("GetTopicsApiResponse", response.getResponseCode() + " " + response.getMessage());

                loader.hideProgress();

                loader.showDialogMessage(response.getMessage(), mActivity.getSupportFragmentManager());

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
                    randomNameForUser2 = response.getData().getChannelName();
                    db.collection("waiting").document(randomNameForUser2).set(user).addOnSuccessListener(unused -> {
                        StringBuilder selectedTagIds = new StringBuilder();
                        List<Tag> selectedTags = getSelectedTags();
                        for (Tag tag : selectedTags) {
                            selectedTagIds.append(tag.getId()).append(",");
                        }

                        if (selectedTagIds.length() > 0) {
                            selectedTagIds.deleteCharAt(selectedTagIds.length() - 1);
                        }
                        Intent intent = new Intent(getContext(), CallScreenActivity.class);
                        intent.putExtra("channelName", randomNameForUser2);
                        intent.putExtra("userType", 2);
                        intent.putExtra("SelectedTag", selectedTagIds.toString());
                        startActivity(intent);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(mActivity, "Failure: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } else if (response.getData().getData() != null) {
                    /**User1
                     * */
                    user.put("Users1", SharedPreferenceManager.INSTANCE.getUserData().getId());
                    db.collection("waiting").document(randomName).set(user).addOnSuccessListener(unused -> {

                        StringBuilder selectedTagIds = new StringBuilder();
                        List<Tag> selectedTags = getSelectedTags();
                        for (Tag tag : selectedTags) {
                            selectedTagIds.append(tag.getId()).append(",");
                        }

                        if (selectedTagIds.length() > 0) {
                            selectedTagIds.deleteCharAt(selectedTagIds.length() - 1);
                        }

                        Intent intent = new Intent(getContext(), CallScreenActivity.class);
                        intent.putExtra("channelName", randomName);
                        intent.putExtra("userType", 1);
                        intent.putExtra("SelectedTag", selectedTagIds.toString());
                        startActivity(intent);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(mActivity, "Failure: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
                loader.hideProgress();

            } else if (response instanceof NetworkResult.Error) {
                Log.d("GetTopicsApiResponse", response.getResponseCode() + " " + response.getMessage());
                loader.hideProgress();
                loader.showDialogMessage(response.getMessage(), mActivity.getSupportFragmentManager());
            }
        };

        Observer<? super NetworkResult<ResponseCall>> endCallObserver = (Observer<NetworkResult<ResponseCall>>) response -> {
            if (response instanceof NetworkResult.Loading) {

                loader.showProgress();
            } else if (response instanceof NetworkResult.Success) {

                loader.hideProgress();
            } else if (response instanceof NetworkResult.Error) {
                Log.d("GetTopicsApiResponse", response.getResponseCode() + " " + response.getMessage());
                loader.hideProgress();
                loader.showDialogMessage(response.getMessage(), mActivity.getSupportFragmentManager());
            }

        };

        Observer<? super NetworkResult<ResponseRefreshCall>> refreshCallObserver = (Observer<NetworkResult<ResponseRefreshCall>>) response -> {
            if (response instanceof NetworkResult.Loading) {

                loader.showProgress();
            } else if (response instanceof NetworkResult.Success) {

                populateTagContainer(response.getData().getData());
                loader.hideProgress();

            } else if (response instanceof NetworkResult.Error) {
                Log.d("GetTopicsApiResponse", response.getResponseCode() + " " + response.getMessage());
                loader.hideProgress();
                loader.showDialogMessage(response.getMessage(), mActivity.getSupportFragmentManager());
            }

        };

        viewModel.getGetTopicsLiveData().observe(getViewLifecycleOwner(), topicsObserver);
        viewModel.getCallLiveData().observe(getViewLifecycleOwner(), callObserver);
        viewModel.getEndCallLiveData().observe(getViewLifecycleOwner(), endCallObserver);
        viewModel.getRefreshCallLiveData().observe(getViewLifecycleOwner(), refreshCallObserver);
    }

    private void populateTagContainer(List<ResponseRefreshCall.Data> data) {

        // Clear previous views
        tagContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ConstraintSet constraintSet = new ConstraintSet();
        int verticalMargin = getResources().getDimensionPixelSize(R.dimen.tag_margin_vertical);
        int[] rowIds = new int[(tagList.size() / COLUMNS) + 1];
        int previousId = ConstraintSet.PARENT_ID;

        for (int i = 0; i < tagList.size(); i++) {
            final Tag tag = tagList.get(i);
            View tagView = inflater.inflate(R.layout.item_tag, tagContainer, false);

            ImageView tagIcon = tagView.findViewById(R.id.tag_icon);
            TextView tagName = tagView.findViewById(R.id.tag_name);
            LottieAnimationView animationView = tagView.findViewById(R.id.activeAnimation);
            Boolean showAnimation = false;
            for (com.devsonics.thoughtpong.retofit_api.response_model.ResponseRefreshCall.Data item : data) {
                if (item.getCategory().equals(String.valueOf(tag.getId()))) {
                    showAnimation = true;
                }
            }
            if (showAnimation) {
                animationView.setVisibility(View.VISIBLE);
                animationView.playAnimation();
            } else {
                animationView.setVisibility(View.GONE);
            }

            /*
            // Custom animation speed or duration.
            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
            animator.addUpdateListener(animation -> {
                animationView.setProgress((Float) animation.getAnimatedValue());
            });
            animator.start();*/

            tagIcon.setImageResource(tag.getIconResId());
            Glide.with(mActivity).load(tag.getImageUrl()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.d("GlideImage", e.getMessage() + "\n" + tag.getImageUrl());
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    Log.d("GlideImage", "Ok " + "\n" + tag.getImageUrl());
                    tagIcon.setImageDrawable(resource);
                    return true;
                }
            }).into(tagIcon);

            tagName.setText(tag.getName());

            int id = View.generateViewId();
            tagView.setId(id);
            tagView.setSelected(tag.isSelected());
            tagContainer.addView(tagView);
            constraintSet.clone(tagContainer);

            int rowIndex = i / COLUMNS;

            if (i % COLUMNS == 0) {

                if (i == 0) {
                    constraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                } else {
                    constraintSet.connect(id, ConstraintSet.TOP, previousId, ConstraintSet.BOTTOM);
                    constraintSet.setMargin(id, ConstraintSet.TOP, verticalMargin);
                }
                previousId = id;
            } else {

                constraintSet.connect(id, ConstraintSet.TOP, rowIds[rowIndex], ConstraintSet.TOP);
                constraintSet.connect(id, ConstraintSet.START, rowIds[rowIndex], ConstraintSet.END);
            }


            rowIds[rowIndex] = id;


            constraintSet.setMargin(id, ConstraintSet.START, getResources().getDimensionPixelSize(R.dimen.tag_margin_horizontal));


            tagView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    tag.toggleSelected();

                    v.setSelected(tag.isSelected());

                    if (tag.isSelected()) {
                        v.setBackgroundResource(R.drawable.selected_tag_background);
//                        tagIcon.setImageDrawable();


                    } else {
                        v.setBackgroundResource(R.drawable.bg_tag);
                    }
                }
            });

            if (tag.isSelected()) {
                tagView.setBackgroundResource(R.drawable.selected_tag_background);
            } else {
                tagView.setBackgroundResource(R.drawable.bg_tag);
            }

            constraintSet.applyTo(tagContainer);
        }
    }

    // Method to get the list of selected tags
    private List<Tag> getSelectedTags() {
        List<Tag> selectedTags = new ArrayList<>();
        for (Tag tag : tagList) {
            if (tag.isSelected()) {
                selectedTags.add(tag);
            }
        }
        return selectedTags;
    }

    // Method to handle the Let's Talk button click
    private void handleLetsTalkButtonClick(List<Tag> selectedTags) {
        StringBuilder selectedTagNames = new StringBuilder();
        StringBuilder selectedTagIds = new StringBuilder();
        for (Tag tag : selectedTags) {
            selectedTagNames.append(tag.getName()).append(", ");
            selectedTagIds.append(tag.getId()).append(",");
        }

        if (selectedTagIds.length() > 0) {
            selectedTagIds.deleteCharAt(selectedTagIds.length() - 1);
        }

        if (selectedTagNames.length() > 0) {
            selectedTagNames.setLength(selectedTagNames.length() - 2);
        }

        randomName = generateRandomName();
        List<String> tagIds = Arrays.asList(selectedTagIds.toString().split(","));

        Log.d("RequestCall", "RoomName: " + randomName + " tagIds: " + selectedTagIds);


        RequestCall requestCall = new RequestCall(randomName, tagIds);
        viewModel.callApi(requestCall);
        Map<String, String> data = new HashMap<>();
//        data.put("NameT", randomName);
        data.put("NameT", generateRandomName());
        db.collection("collectionT").document("IDT").set(data).addOnSuccessListener(unused -> {
        }).addOnFailureListener(e -> {
            Toast.makeText(mActivity, "IDT Failure: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
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

    private void initIDTListener() {
        final DocumentReference idtRef = db.collection("collectionT").document("IDT");
        if (idtListener != null) {
            idtListener.remove();
        }
        idtListener = idtRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.d("listener", "IDT Listen failed. " + error.getMessage());
                return;
            }

            //Refresh Call API
            viewModel.refreshCall();
            Log.d("listener", "IDT Listener Called on " + value.get("NameT"));
        });
    }

}
