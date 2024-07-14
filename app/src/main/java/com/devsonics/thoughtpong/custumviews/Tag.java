
package com.devsonics.thoughtpong.custumviews;

public class Tag {
    private Long id;
    String imageUrl;
    private String name;
    private int iconResId;
    private boolean isSelected = false; // Track selection state


    public Tag(String name, int iconResId, String imageUrl) {
        this.name = name;
        this.iconResId = iconResId;
        this.isSelected = false; // Initialize as not selected
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
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
