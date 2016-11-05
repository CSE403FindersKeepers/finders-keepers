package cse403.finderskeepers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cse403.finderskeepers.data.UserInfoHolder;

public class UserSettingsActivity extends AppCompatActivity {

    private static final int GET_AVATAR = 1;
    private Geocoder geoCoder;

    private View.OnClickListener avatarListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent getAvatarIntent = new Intent();
            getAvatarIntent.setType("image/*");
            getAvatarIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(getAvatarIntent, "Select Image to Use as Avatar"), GET_AVATAR);
        }
    };

    private View.OnClickListener locationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            TextView locationText  = (TextView) findViewById(R.id.location_text);

            EditText zipEntered = (EditText) findViewById(R.id.edit_zip_field);

            double longitude, latitude;

            try {
                List<Address> addresses = geoCoder.getFromLocationName(zipEntered.getText().toString() + ", United States", 1);
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
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button avatarButton  = (Button) findViewById(R.id.action_change_avatar);
        avatarButton.setOnClickListener(this.avatarListener);

        Button locationButton  = (Button) findViewById(R.id.action_change_location);
        locationButton.setOnClickListener(this.locationListener);

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
