
package com.devsonics.thoughtpong.custumviews;

public class Tag {
    private String name;
    private int iconResId;
    private boolean isSelected; // Track selection state

    public Tag(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
        this.isSelected = false; // Initialize as not selected
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
