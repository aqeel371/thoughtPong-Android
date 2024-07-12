package com.devsonics.thoughtpong.custumviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.devsonics.thoughtpong.R;

public class SpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private String[] topics;
    private int[] icons;

    public SpinnerAdapter(@NonNull Context context, @NonNull String[] topics, @NonNull int[] icons) {
        super(context, R.layout.spinner_item, topics);
        this.context = context;
        this.topics = topics;
        this.icons = icons;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createViewFromResource(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createViewFromResource(position, convertView, parent);
    }

    private View createViewFromResource(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        ImageView icon = view.findViewById(R.id.spinner_item_icon);
        TextView text = view.findViewById(R.id.spinner_item_text);

        icon.setImageResource(icons[position]);
        text.setText(topics[position]);

        return view;
    }
}
