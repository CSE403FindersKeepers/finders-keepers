package cse403.finderskeepers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.TextView;

import com.google.firebase.auth.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.UserDataHandler;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import cse403.finderskeepers.data.UserInfoHolder;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class UserSettingsActivity extends AppCompatActivity {

    private static final int GET_AVATAR = 1;
    private Geocoder geoCoder;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private View.OnClickListener avatarListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent getAvatarIntent = new Intent();
            getAvatarIntent.setType("image/*");
            getAvatarIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(getAvatarIntent, "Select Image to Use as Avatar"), GET_AVATAR);
        }
    };

    private View.OnClickListener updateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            TextView locationText  = (TextView) findViewById(R.id.location_text);

            EditText zipEntered = (EditText) findViewById(R.id.edit_zip_field);

            double longitude, latitude;

            try {
                List<Address> addresses = geoCoder.getFromLocationName(zipEntered.getText().toString().trim() + ", United States", 1);
                if (addresses == null || zipEntered.getText().toString().equals("")) {
                    Log.d("zip", zipEntered.getText().toString());
                    locationText.setText("Invalid ZIP code");
                    return;
                }
                longitude = addresses.get(0).getLongitude();
                latitude = addresses.get(0).getLatitude();
            } catch (IOException e) {
                locationText.setText("Unable to translate ZIP to location");
                e.printStackTrace();
                return;
            }

            Location userLocation = new Location("");
            userLocation.setLatitude(latitude);
            userLocation.setLongitude(longitude);
            UserInfoHolder.getInstance().setLocation(userLocation);

            locationText.setText("Latitude: " + latitude + " Longitude: " + longitude);
            UserSettingsActivity.this.updateUser();
        }
    };

    private void updateUser() {
        ImageView avatar = (ImageView) findViewById(R.id.user_avatar);
        int UID = UserInfoHolder.getInstance().getUID();

        EditText zipEntered = (EditText) findViewById(R.id.edit_zip_field);
        String zipcodeText = zipEntered.getText().toString();

        // Check if zipcode has been entered
        int ZIP = 0;
        if(zipcodeText.length() != 0) {
            ZIP = Integer.parseInt(zipEntered.getText().toString());
        }

        String name = UserInfoHolder.getInstance().getUserName();

        // encode the current image as base64 JPEG
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Drawable drawable = avatar.getDrawable();
        Bitmap image = ((BitmapDrawable) drawable).getBitmap();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        // create request JSON
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON.put("name", name);
            requestJSON.put("user_id", UID);
            requestJSON.put("avatar", encodedImage);

            //Don't change zipcode unless text placed in zipcode field
            if(zipcodeText.length() != 0)
                requestJSON.put("zipcode", ZIP);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody requestBody = RequestBody.create(JSON, requestJSON.toString());

        //TODO: DO CALLS TO UPDATE AVATAR/LOCATION VIA API SERVER
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button avatarButton  = (Button) findViewById(R.id.action_change_avatar);
        avatarButton.setOnClickListener(this.avatarListener);

        Button locationButton  = (Button) findViewById(R.id.action_change_location);
        locationButton.setOnClickListener(this.updateListener);

        if (UserInfoHolder.getInstance().getAvatar() != null) {
            ImageView userAvatar = (ImageView) findViewById(R.id.user_avatar);
            userAvatar.setImageBitmap(UserInfoHolder.getInstance().getAvatar());
        }

        geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        Location userLocation;

        if((userLocation = UserInfoHolder.getInstance().getLocation()) != null) {
            EditText zipEntered = (EditText) findViewById(R.id.edit_zip_field);
            try {
                List<Address> addresses = geoCoder.getFromLocation(userLocation.getLatitude(),userLocation.getLongitude(), 1);
                zipEntered.setText(addresses.get(0).getPostalCode());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_settings_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                return true;
            case R.id.action_user_page:
                Intent intent = new Intent(UserSettingsActivity.this, HomePage.class);
                finish();
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GET_AVATAR && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            ParcelFileDescriptor parcelFileDescriptor;
            try {
                parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImage, "r");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            // error getting file descriptor
            if (parcelFileDescriptor == null) {
                return;
            }

            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap avatar = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            try {
                parcelFileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            ImageView currentAvatar = (ImageView) findViewById(R.id.user_avatar);
            currentAvatar.setImageBitmap(avatar);
            UserInfoHolder.getInstance().setAvatar(avatar);
        }
    }
}
