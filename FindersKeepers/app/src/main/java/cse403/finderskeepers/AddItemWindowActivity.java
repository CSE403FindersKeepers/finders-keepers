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
import android.widget.ImageView;
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
    private Bitmap itemImage;
    private List<String> tags;
    private int GET_AVATAR = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_additem_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tags = new ArrayList<>();

        ImageButton addItem = (ImageButton) findViewById(R.id.add_item_img);
        Button upload = (Button) findViewById(R.id.upload_button);
        upload.setOnClickListener(this.uploadItemListener);
        addItem.setOnClickListener(this.itemPicListener);
    }

    private View.OnClickListener uploadItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View view){
          finish();
        }
    };

    //Listener for item add buttons
    private View.OnClickListener itemPicListener = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            Intent getAvatarIntent = new Intent();
            getAvatarIntent.setType("image/*");
            getAvatarIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(getAvatarIntent, "Select Image to Use as Avatar"), GET_AVATAR);
        }
    };

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
            Bitmap item = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            try {
                parcelFileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            ImageView img = (ImageView) findViewById(R.id.add_item_img);
            img.setImageBitmap(item);
            UserInfoHolder.getInstance().setAvatar(item);
        }
    }


}
