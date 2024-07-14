package com.devsonics.thoughtpong.fragment.home;

import android.content.Context;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.devsonics.thoughtpong.CallScreenActivity;
import com.devsonics.thoughtpong.MainActivity;
import com.devsonics.thoughtpong.activities.verification.VerificationViewModel;
import com.devsonics.thoughtpong.custumviews.Tag;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import com.devsonics.thoughtpong.R;
import com.devsonics.thoughtpong.retofit_api.model.ResponseGetTopic;
import com.devsonics.thoughtpong.utils.Loader;
import com.devsonics.thoughtpong.utils.NetworkResult;

public class HomeFragment extends Fragment {
    LinearLayout letTalkButton;
    TextView tvProfileName, tvInfo;
    CircleImageView ivProfile;

    private ConstraintLayout tagContainer;
    private List<Tag> tagList;
    private static final int COLUMNS = 3;

    HomeViewModel viewModel;
    private MainActivity mActivity;
    Observer<? super NetworkResult<ResponseGetTopic>> topicsObserver;
    Loader loader;

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

        viewModel.getTopicsApi();


        return view;
    }

    private void initObserver() {
        topicsObserver = (Observer<NetworkResult<ResponseGetTopic>>) response -> {
            if (response instanceof NetworkResult.Loading) {

                loader.showProgress();
            } else if (response instanceof NetworkResult.Success) {

                tagList = new ArrayList<>();

                for (ResponseGetTopic.ResponseGetTopicItem item : response.getData()) {
                    tagList.add(new Tag(item.getName(), R.drawable.ic_photography, "https://bdpos.store/api/thought/Images/" + item.getName()));
                }
                populateTagContainer();

                loader.hideProgress();

            } else if (response instanceof NetworkResult.Error) {
                Log.d("GetTopicsApiResponse", response.getResponseCode() + " " + response.getMessage());

                loader.hideProgress();

                loader.showDialogMessage(response.getMessage(), mActivity.getSupportFragmentManager());

            }
        };
        viewModel.getGetTopicsLiveData().observe(getViewLifecycleOwner(), topicsObserver);
    }

    private void populateTagContainer() {
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

            tagIcon.setImageResource(tag.getIconResId());
            Glide.with(requireContext()).load(tag.getImageUrl()).into(tagIcon);

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
        for (Tag tag : selectedTags) {
            selectedTagNames.append(tag.getName()).append(", ");
        }

        if (selectedTagNames.length() > 0) {
            selectedTagNames.setLength(selectedTagNames.length() - 2);
        }
        // Show a toast or handle the tags as needed
        Toast.makeText(getActivity(), "Selected Tags: " + selectedTagNames, Toast.LENGTH_SHORT).show();
        // Add further logic to handle the selected tags
        startActivity(new Intent(getContext(), CallScreenActivity.class));
    }
}