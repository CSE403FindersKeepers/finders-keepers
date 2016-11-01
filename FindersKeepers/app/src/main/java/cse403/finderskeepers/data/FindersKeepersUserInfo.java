package cse403.finderskeepers.data;

/**
 * Created by Artem on 11/1/2016.
 *
 * This class holds information on a user of the app.
 */

public class FindersKeepersUserInfo {

    private String userName;

    // Construct a new FindersKeepersUserInfo, with the userName
    // being set to the given string
    public FindersKeepersUserInfo(String userName) {
        this.userName = userName;
    }

    // Return the user name of this user
    public String getUserName() {
        return userName;
    }
}
