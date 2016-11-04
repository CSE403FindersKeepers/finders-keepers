package cse403.finderskeepers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.Manifest;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import android.content.pm.PackageManager;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.IOException;

import cse403.finderskeepers.data.UserInfoHolder;

public class UserSettingsActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int GET_AVATAR = 1;
    private static final int LOCATION_PERMISSIONS = 2;

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;

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
            setLocation();
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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Button locationButton  = (Button) findViewById(R.id.action_change_location);
        locationButton.setEnabled(false);
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

    @Override
    public void onConnected(Bundle connectionHint) {
        Button locationButton  = (Button) findViewById(R.id.action_change_location);
        locationButton.setEnabled(true);
        setLocation();
    }

    private void setLocation() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck == 0) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                TextView locationText  = (TextView) findViewById(R.id.location_text);
                locationText.setText("Latitude: " + String.valueOf(mLastLocation.getLatitude()
                        + "\nLongitude: " + String.valueOf(mLastLocation.getLongitude())));
            } else {
                Log.d("ApiClientStatus", "" + mGoogleApiClient.isConnected());
            }
        } else {
            requestPermissions(new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION }, LOCATION_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == LOCATION_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.setLocation();
            } else {
                TextView locationText = (TextView) findViewById(R.id.location_text);
                locationText.setText("Location permissions not granted");
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {

    }
}
