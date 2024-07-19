
package com.devsonics.thoughtpong.custumviews;

import android.net.Uri;


public class Tag {
    private int id;
    Uri imageUrl;
    private String name;
    private int iconResId;
    private boolean isSelected = false; // Track selection state


    public Tag(Integer id, String name, int iconResId, Uri imageUrl) {
        this.id = id;
        this.name = name;
        this.iconResId = iconResId;
        this.isSelected = false; // Initialize as not selected
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Uri getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void toggleSelected() {
        isSelected = !isSelected;
    }
}
