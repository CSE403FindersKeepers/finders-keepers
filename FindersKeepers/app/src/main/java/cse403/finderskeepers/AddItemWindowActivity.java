package cse403.finderskeepers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cse403.finderskeepers.data.UserInfoHolder;

/**
 * Created by Jared on 11/8/2016.
 */

public class AddItemWindowActivity extends AppCompatActivity {

    private String imgURL;
    private List<String> tags;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_additem_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tags = new ArrayList<String>();

        ImageButton addItem = (ImageButton) findViewById(R.id.add_item_img);
        Button addTag = (Button) findViewById(R.id.add_item_tag);
        Button upload = (Button) findViewById(R.id.upload_button);
        upload.setOnClickListener(this.uploadItemListener);
        addItem.setOnClickListener(this.itemPicListener);
        addTag.setOnClickListener(this.itemTagListener);
    }

    private View.OnClickListener uploadItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View view){
           /*TODO:
            * Upload the new item, first ensuring that the imgURL and tags are set,
             * then use the API to add it to inventory.
             * If successful, close the addItem page, otherwise show an error.
             * Note: Should authenticate on user's google token for a successful upload.
             * This should be done using the getIDToken option for security -- However,
             * the account login and remote server need to be configured correctly.
             * See https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInAccount#getId()
             * and https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInOptions.Builder#requestIdToken(java.lang.String)*/
        }
    };

    //Listener for item add buttons
    private View.OnClickListener itemPicListener = new View.OnClickListener() {
        @Override
        public void onClick(View view){

        }
    };

    //Listener for item add buttons
    private View.OnClickListener itemTagListener = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            /*TODO:
             * Open an interactive text-entry box that allows user to type in a tag,
              * then press enter. (There must be some view for this?)
              * Then, add the tag to the list.
              * Then create a new button in the Tag list with the designated text.*/
        }
    };

    /*TODO:
     * Need a button in the layout + a click listener for tags that have already been created,
      * to allow us to remove them. Not sure how to do this yet, because the button has to be
      * dynamically made and bound to the listener, and able to communicate its ID to the listener.*/


}
