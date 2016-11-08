package cse403.finderskeepers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.view.Menu;
import android.view.MenuItem;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

import cse403.finderskeepers.data.UserInfoHolder;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView userName = (TextView) findViewById(R.id.user_name_text);
        userName.setText(UserInfoHolder.getInstance().getUserName());

        if (UserInfoHolder.getInstance().getAvatar() != null) {
            ImageView userAvatar = (ImageView) findViewById(R.id.user_avatar);
            userAvatar.setImageBitmap(UserInfoHolder.getInstance().getAvatar());
        }

        //Button with a click listener which allows user to add an item
        ImageButton img = (ImageButton) findViewById(R.id.add_item);
        img.setOnClickListener(this.itemListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
        return true;
    }

    /**
     * Listener which opens image gallery for user to select item image
     */
    private View.OnClickListener itemListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent getImageIntent = new Intent();
            getImageIntent.setType("image/*");
            getImageIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(getImageIntent, "Select Image to Use as Item"), 1);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                Intent intent = new Intent(HomePage.this, UserSettingsActivity.class);
                finish();
                startActivity(intent);
                return true;
            case R.id.action_user_page:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
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
            Bitmap itemImage = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            try {
                parcelFileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            //Get input string for the tags
            EditText edit = (EditText) findViewById(R.id.editTags);

            //Create the item the user wants to add
            AddableItem currentAvatar = new AddableItem(getApplicationContext(), edit.getText().toString());
            currentAvatar.setImageBitmap(itemImage);
            UserInfoHolder.getInstance().setAvatar(itemImage);

            //Add the item to the beginning to the item list
            LinearLayout addableItems = (LinearLayout)
                    findViewById(R.id.item_list);
            addableItems.addView(currentAvatar, 0);
        }
    }
}
