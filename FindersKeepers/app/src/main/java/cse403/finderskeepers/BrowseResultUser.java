package cse403.finderskeepers;

import android.content.Context;
import android.widget.ImageButton;

/**
 * Created by Artem on 11/16/2016.
 */

public class BrowseResultUser extends ImageButton {
    private int userId;

    public BrowseResultUser(Context c, int userId){
        super(c);
        this.userId = userId;
    }

    /**
     * Returns user's user id
     */
    public int getUserId() {
        return userId;
    }
}
