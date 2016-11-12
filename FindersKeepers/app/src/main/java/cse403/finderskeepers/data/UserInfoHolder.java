package cse403.finderskeepers.data;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import cse403.finderskeepers.UserAPIService;

/**
 * Created by Artem on 11/1/2016.
 *
 * This class is a singleton which can be accessed anywhere in the application.
 * It holds information on the current user, since that info is needed almost
 * everywhere in the application.
 */

public class UserInfoHolder {
    public static final String SERVER_ADDRESS = "http://10.0.2.2:5000"; //For local testing (note: still uses remote db)
    /*"http://ec2-35-163-100-235.us-west-2.compute.amazonaws.com"; //For remote testing */
    private static final UserInfoHolder instance = new UserInfoHolder();

    private UserAPIService userapiservice;
    private GoogleSignInResult signInInfo;
    private Bitmap avatar;
    private Location location;
    private int UID;

    // Private constructor to ensure only one instance exists
    private UserInfoHolder() {
        signInInfo = null;
    }

    // Set the userapiservice object

    public void setAPIService(UserAPIService userapiservice){ this.userapiservice = userapiservice; }

    // Get the userapiservice object

    public UserAPIService getAPIService(){ return userapiservice; }

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

    // Set UID of this user
    public void setUID(int UID) { this.UID = UID; }

    //Return unique user ID
    public int getUID() {return this.UID; }

    // Set user avatar to given Bitmap
    public void setAvatar(Bitmap avatar) { this.avatar = avatar; }

    // Return Bitmap of user avatar
    public Bitmap getAvatar() { return this.avatar; }

    // Set user location to current
    public void setLocation(Location location) { this.location = location; }

    // Get user location
    public Location getLocation() { return this.location; }

    // Get user email
    public String getEmail() { return signInInfo.getSignInAccount().getEmail(); }
}
