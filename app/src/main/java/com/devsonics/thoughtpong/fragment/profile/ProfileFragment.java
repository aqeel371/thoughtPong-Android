package com.devsonics.thoughtpong.fragment.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.devsonics.thoughtpong.MainActivity;
import com.devsonics.thoughtpong.activities.waiting.CallScreenActivity;
import com.devsonics.thoughtpong.custumviews.SpinnerAdapter;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import com.devsonics.thoughtpong.R;
import com.devsonics.thoughtpong.custumviews.Tag;
import com.devsonics.thoughtpong.fragment.home.HomeViewModel;
import com.devsonics.thoughtpong.retofit_api.request_model.RequestUpdateProfile;
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseCall;
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseUpdateProfile;
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseUploadImage;
import com.devsonics.thoughtpong.retofit_api.response_model.UserData;
import com.devsonics.thoughtpong.utils.Loader;
import com.devsonics.thoughtpong.utils.NetworkResult;
import com.devsonics.thoughtpong.utils.SharedPreferenceManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    ProfileViewModel viewModel;
    private MainActivity mActivity;
    Loader loader;

    TextView tvProfileName;
    EditText etFullName, etEmail;
    CircleImageView ivProfilePic, ivChangeProfilePic;
    ConstraintLayout btnSave;
    private Spinner select_topic_spinner;
    private Uri mImageCaptureUri;

    private static final int PICK_FROM_FILE = 2;

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Uri imageUri = null;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         **Initialize ViewModel with Factory**
         */

        ViewModelProvider.Factory factory = ProfileViewModel.Companion.createFactory(mActivity.getApplication());
        viewModel = new ViewModelProvider(this, factory).get(ProfileViewModel.class);
        loader = new Loader(mActivity);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() != Activity.RESULT_OK) return;
            Intent data = result.getData();

            Bitmap bitmap = null;
            String path = "";

            if (data != null && data.getData() != null) {
                mImageCaptureUri = data.getData();
                path = getRealPathFromURI(mImageCaptureUri); // from Gallery

                if (path == null) path = mImageCaptureUri.getPath(); // from File Manager

                if (path != null) bitmap = BitmapFactory.decodeFile(path);
            }

            if (bitmap != null) {
                ivProfilePic.setImageBitmap(bitmap);
                imageUri = mImageCaptureUri;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        tvProfileName = view.findViewById(R.id.full_name_text);
        etFullName = view.findViewById(R.id.full_name_input);
        etEmail = view.findViewById(R.id.email_input);
        ivProfilePic = view.findViewById(R.id.profile_image);
        ivChangeProfilePic = view.findViewById(R.id.change_profile_image);
        select_topic_spinner = view.findViewById(R.id.select_topic_spinner);
        btnSave = view.findViewById(R.id.btn_save);

        // Set default values
        tvProfileName.setText("Floyd Miles"); // Set Name
        etFullName.setHint("Floyd Miles");
        etEmail.setHint("Floyd.Miles@gmail.com");
        ivProfilePic.setImageDrawable(getResources().getDrawable(R.drawable.pp));


        initObserver();
        setUserData();

        btnSave.setOnClickListener(v -> updateProfile());
        // Define topics and corresponding icons
        String[] topics = getResources().getStringArray(R.array.topics_array);
        int[] icons = {R.drawable.ic_photography, R.drawable.ic_music, R.drawable.ic_writing, R.drawable.ic_food, R.drawable.ic_nature, R.drawable.ic_fashion, R.drawable.ic_entertainment, R.drawable.ic_game, R.drawable.ic_cooking};

        // Set up the Spinner with the custom adapter
        SpinnerAdapter adapter = new SpinnerAdapter(requireContext(), topics, icons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        select_topic_spinner.setAdapter(adapter);

        ivChangeProfilePic.setOnClickListener(v -> openImagePicker());

        return view;
    }

    private void setUserData() {
        String email = SharedPreferenceManager.INSTANCE.getUserData().getEmail();
        String fullName = SharedPreferenceManager.INSTANCE.getUserData().getFullName();
        String profileImage = SharedPreferenceManager.INSTANCE.getUserData().getImage();
        etEmail.setText(email);
        etFullName.setText(fullName);
        tvProfileName.setText(fullName);
        if (profileImage != null && !profileImage.isEmpty()) {
            String profileUrl = "https://bdpos.store/api/thought/Images/" + profileImage;
            Glide.with(this).load(profileUrl).into(ivProfilePic);
        }

    }

    private void updateProfile() {

        if (imageUri != null) {

            String filePath = getRealPathFromURI(mImageCaptureUri);
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), file);

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            viewModel.uploadImageApi(body);
        } else {
            if (etEmail.getText().toString().trim().isEmpty()) {
                etEmail.setError("can't be null");
                return;
            }
            if (etFullName.getText().toString().trim().isEmpty()) {
                etFullName.setError("can't be null");
                return;
            }

            String email = etEmail.getText().toString().trim();
            String fullName = etFullName.getText().toString().trim();

            RequestUpdateProfile requestUpdateProfile = new RequestUpdateProfile(email,null, fullName);

            viewModel.updateProfile(requestUpdateProfile);

        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_FROM_FILE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);

        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    private void initObserver() {
        Observer<? super NetworkResult<ResponseUploadImage>> uploadImageObserver = (Observer<NetworkResult<ResponseUploadImage>>) response -> {
            if (response instanceof NetworkResult.Loading) {

                loader.showProgress();
            } else if (response instanceof NetworkResult.Success) {

                UserData data = SharedPreferenceManager.INSTANCE.getUserData();
                data.setImage(response.getData().getSuccess());
                SharedPreferenceManager.INSTANCE.setUserData(data);

                if (etEmail.getText().toString().trim().isEmpty()) {
                    etEmail.setError("can't be null");
                    return;
                }
                if (etFullName.getText().toString().trim().isEmpty()) {
                    etFullName.setError("can't be null");
                    return;
                }

                String email = etEmail.getText().toString().trim();
                String fullName = etFullName.getText().toString().trim();

                RequestUpdateProfile requestUpdateProfile = new RequestUpdateProfile(email, response.getData().getSuccess(), fullName);
                viewModel.updateProfile(requestUpdateProfile);
                loader.hideProgress();

            } else if (response instanceof NetworkResult.Error) {
                Log.d("GetTopicsApiResponse", response.getResponseCode() + " " + response.getMessage());
                loader.hideProgress();
                loader.showDialogMessage(response.getMessage(), mActivity.getSupportFragmentManager());
            }
        };
        Observer<? super NetworkResult<ResponseUpdateProfile>> updateProfileObserver = (Observer<NetworkResult<ResponseUpdateProfile>>) response -> {
            if (response instanceof NetworkResult.Loading) {

                loader.showProgress();
            } else if (response instanceof NetworkResult.Success) {

                UserData data = new UserData();

                data.setImage(response.getData().getImage());
                data.setid(response.getData().getId());
                data.setEmail(response.getData().getEmail());
                data.setPhone(response.getData().getPhone());
                data.setFullName(response.getData().getFullName());

                SharedPreferenceManager.INSTANCE.setUserData(data);

                Toast.makeText(mActivity, "Updated Successfully", Toast.LENGTH_SHORT).show();
                loader.hideProgress();

            } else if (response instanceof NetworkResult.Error) {
                Log.d("GetTopicsApiResponse", response.getResponseCode() + " " + response.getMessage());
                loader.hideProgress();
                loader.showDialogMessage(response.getMessage(), mActivity.getSupportFragmentManager());
            }
        };

        viewModel.getUploadImageLiveData().observe(getViewLifecycleOwner(), uploadImageObserver);
        viewModel.getUpdateProfileLiveData().observe(getViewLifecycleOwner(), updateProfileObserver);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }
}
