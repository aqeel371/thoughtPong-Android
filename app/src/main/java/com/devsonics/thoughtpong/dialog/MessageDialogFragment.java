package com.devsonics.thoughtpong.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devsonics.thoughtpong.R;

public class MessageDialogFragment extends DialogFragment {
    public static final String DIALOG_TAG = "BaseProgressDialogFragment";
    private View dialogView = null;
    private String message = "";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogCustom);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.fragment_message_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setGravity(Gravity.CENTER);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dialogView != null) {
            TextView txt = dialogView.findViewById(R.id.message);
            txt.setText(message);
        }
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(Math.max(width, 500), Math.max(height, 300));
        }
    }

    public void setMessage(String message) {
        this.message = message;
        if (dialogView != null) {
            TextView txt = dialogView.findViewById(R.id.message);
            txt.setText(message);
        }
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message_dialog, container, false);
    }
}
