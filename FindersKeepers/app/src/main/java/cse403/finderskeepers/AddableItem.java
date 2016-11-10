package cse403.finderskeepers;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Kieran on 11/7/16.
 * AddableItem is an ImageView which contains image tags
 */
public class AddableItem extends ImageButton {
    private String tags;
    private int itemId;

    public AddableItem(Context c, String tags, int itemId){
        super(c);
        this.tags = tags;
        this.itemId = itemId;
    }

    /**
     * Returns a list of tags for the item
     */
    public String getTags() {
        return tags;
    }

    /**
     * Returns itemId of this item
     */
    public int getItemId() {
        return itemId;
    }
}
