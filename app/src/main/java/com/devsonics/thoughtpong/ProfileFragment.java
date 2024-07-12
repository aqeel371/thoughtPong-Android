package com.devsonics.thoughtpong;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.fragment.app.Fragment;

import com.devsonics.thoughtpong.custumviews.SpinnerAdapter;

import de.hdodenhof.circleimageview.CircleImageView;
import com.devsonics.thoughtpong.R;

public class ProfileFragment extends Fragment {

    TextView tvProfileName;
    EditText etFullName, etEmail;
    CircleImageView ivProfilePic, ivChangeProfilePic;
    private Spinner select_topic_spinner;
    private Uri mImageCaptureUri;

    private static final int PICK_FROM_FILE = 2;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != Activity.RESULT_OK) return;
                    Intent data = result.getData();

                    Bitmap bitmap = null;
                    String path = "";

                    if (data != null && data.getData() != null) {
                        mImageCaptureUri = data.getData();
                        path = getRealPathFromURI(mImageCaptureUri); // from Gallery

                        if (path == null)
                            path = mImageCaptureUri.getPath(); // from File Manager

                        if (path != null)
                            bitmap = BitmapFactory.decodeFile(path);
                    }

                    if (bitmap != null) {
                        ivProfilePic.setImageBitmap(bitmap);
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        tvProfileName = view.findViewById(R.id.full_name_text);
        etFullName = view.findViewById(R.id.full_name_input);
        etEmail = view.findViewById(R.id.email_input);
        ivProfilePic = view.findViewById(R.id.profile_image);
        ivChangeProfilePic = view.findViewById(R.id.change_profile_image);
        select_topic_spinner = view.findViewById(R.id.select_topic_spinner);

        // Set default values
        tvProfileName.setText("Floyd Miles"); // Set Name
        etFullName.setHint("Floyd Miles");
        etEmail.setHint("Floyd.Miles@gmail.com");
        ivProfilePic.setImageDrawable(getResources().getDrawable(R.drawable.pp));

        // Define topics and corresponding icons
        String[] topics = getResources().getStringArray(R.array.topics_array);
        int[] icons = {
                R.drawable.ic_photography,
                R.drawable.ic_music,
                R.drawable.ic_writing,
                R.drawable.ic_food,
                R.drawable.ic_nature,
                R.drawable.ic_fashion,
                R.drawable.ic_entertainment,
                R.drawable.ic_game,
                R.drawable.ic_cooking
        };

        // Set up the Spinner with the custom adapter
        SpinnerAdapter adapter = new SpinnerAdapter(requireContext(), topics, icons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        select_topic_spinner.setAdapter(adapter);

        ivChangeProfilePic.setOnClickListener(v -> openImagePicker());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
}
