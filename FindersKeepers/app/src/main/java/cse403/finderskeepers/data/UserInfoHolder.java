package cse403.finderskeepers.data;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;

/**
 * Created by Artem on 11/1/2016.
 *
 * This class is a singleton which can be accessed anywhere in the application.
 * It holds information on the current user, since that info is needed almost
 * everywhere in the application.
 */

public class UserInfoHolder {
    private static final UserInfoHolder instance = new UserInfoHolder();

    private GoogleSignInResult signInInfo;

    // Private constructor to ensure only one instance exists
    private UserInfoHolder() {
        signInInfo = null;
    }

    // Return the one instance of this object
    public static UserInfoHolder getInstance() {
        return instance;
    }

    // Create user info based on sign in details
    public void initializeUser(GoogleSignInResult signInInfo) {
        this.signInInfo = signInInfo;
    }

    // Return display name of user
    public String getUserName() {
        return signInInfo.getSignInAccount().getDisplayName();
    }
}
