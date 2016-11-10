package cse403.finderskeepers;

import android.content.Context;
import android.media.Image;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Kieran on 11/7/16.
 * AddableItem is an ImageView which contains image tags
 */
public class AddableItem extends ImageView {
    private List<String> tags;

    public AddableItem(Context c, String tags){
        super(c);
        this.tags = parseTags(tags);
    }

    /**
     * Returns a list of tags for the item
     */
    public List<String> getTags(){
        List<String> tagCopy = new ArrayList<String>();
        for(String item : tags){
            tagCopy.add(item);
        }
        return tagCopy;
    }

    /**
     * Takes in the string of tags and returns a list of
     * tags
     */
    private List<String> parseTags(String tags){
        Scanner scan = new Scanner(tags);
        List<String> itemTags = new ArrayList<String>();
        while(scan.hasNext()){
            itemTags.add(scan.next());
        }
        scan.close();
        return itemTags;
    }
}
