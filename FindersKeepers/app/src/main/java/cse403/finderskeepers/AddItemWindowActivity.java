package cse403.finderskeepers;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cse403.finderskeepers.data.UserInfoHolder;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static cse403.finderskeepers.UserSettingsActivity.JSON;

/**
 * Created by Jared on 11/8/2016.
 */

public class AddItemWindowActivity extends AppCompatActivity {

    private int GET_IMAGE = 1;
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private boolean imageSet;

    // Item info

    // Is item being edited: true = being edited, false = being created
    private boolean edit;

    // id of item, if edited
    private int itemId;

    //API Service for network communication
    private UserAPIService userapiservice;

    private void disconnectionError(){
        Intent intent = new Intent(AddItemWindowActivity.this, DisconnectionError.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_additem_page);
        userapiservice = UserInfoHolder.getInstance().getAPIService();
        ImageButton img = (ImageButton) findViewById(R.id.add_item_img);
        Button upload = (Button) findViewById(R.id.upload_button); //TODO: URGENT: RETURNING NULL?!\

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("ITEM_ID") && getIntent().getExtras().containsKey("TAGS")) {

            itemId = getIntent().getExtras().getInt("ITEM_ID");
            Bitmap image = null;

            String tags = getIntent().getExtras().getString("TAGS");
            EditText editTags = (EditText) findViewById(R.id.editTags);
            editTags.setText(tags);

            LinearLayout layout = (LinearLayout) findViewById(R.id.content_inventory_page);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0,
                    1);

            // add button to delete item
            Button deleteItemButton = new Button(this);
            deleteItemButton.setText("Delete Item");
            deleteItemButton.setOnClickListener(deleteItemListener);
            deleteItemButton.setLayoutParams(params);
            layout.addView(deleteItemButton);

            try {
                Response<ResponseBody> doCall = userapiservice.getItem(this.itemId).execute();

                if (doCall.code() != 200) {
                    throw new IOException("OMG HTTP ERROR");
                }

                String jsonval = doCall.body().string();
                JSONObject itemObj = new JSONObject(jsonval);
                URL imgloc = new URL(itemObj.getString("image_url"));

                try {
                    if (!imgloc.toString().equals("")) {
                        image = BitmapFactory.decodeStream(imgloc.openConnection().getInputStream());
                    }
                } catch (IOException e) {
                    disconnectionError();
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Change text of add item button if item info exists
            if(upload != null) upload.setText("Update Item");
            this.edit = true;
            img.setImageBitmap(image);
            imageSet = true;
        } else {
            this.edit = false;
            imageSet = false;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (upload != null) upload.setOnClickListener(this.uploadItemListener);
        img.setOnClickListener(this.itemPicListener);
    }

    private View.OnClickListener uploadItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            if (!imageSet) {
                return;
            }
            EditText tags = (EditText) findViewById(R.id.editTags);
            Scanner scanner = new Scanner(tags.getText().toString());

            // create tags array
            JSONArray jsonTags = new JSONArray();
            while(scanner.hasNext()) {
                jsonTags.put(scanner.next());
            }

            // encode the current image as base64 JPEG
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageButton addItem = (ImageButton) findViewById(R.id.add_item_img);
            Drawable drawable = addItem.getDrawable();
            Bitmap image = ((BitmapDrawable) drawable).getBitmap();
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageBytes = stream.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            // create request JSON
            if(edit) {
                JSONObject requestJSON = new JSONObject();
                try {
                    requestJSON.put("tags", jsonTags);
                    requestJSON.put("item_id", AddItemWindowActivity.this.itemId);
                    requestJSON.put("item_image_data", encodedImage);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                RequestBody requestBody = RequestBody.create(JSON, requestJSON.toString());
                Call<ResponseBody> itemUpdate = userapiservice.updateItem(requestBody);

                try {
                   Response<ResponseBody> updateResult = itemUpdate.execute();
                    if (updateResult.code() != 200){
                        throw new IOException("HTTP Error");
                    }
                    JSONObject updateResJSON = new JSONObject(updateResult.body().string());
                    String err = updateResJSON.getString("error");
                    if(!err.equals("")) throw new NetworkErrorException(err);

                } catch (IOException e) {
                    disconnectionError();
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NetworkErrorException e) {
                    e.printStackTrace();
                }

            } else {
                JSONObject requestJSON = new JSONObject();
                try {
                    requestJSON.put("tags", jsonTags);
                    requestJSON.put("user_id", UserInfoHolder.getInstance().getUID());
                    requestJSON.put("item_image_data", encodedImage);
                    requestJSON.put("title", "");
                    requestJSON.put("description", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                RequestBody requestBody = RequestBody.create(JSON, requestJSON.toString());
                Call<ResponseBody> itemCreate = userapiservice.makeItem(requestBody);

                try {
                    Response<ResponseBody> updateResult = itemCreate.execute();
                    if (updateResult.code() != 200){
                        throw new IOException("HTTP Error " + updateResult.code());
                    }
                    JSONObject updateResJSON = new JSONObject(updateResult.body().string());
                    String err = updateResJSON.getString("error");
                    if(!err.equals("")) throw new NetworkErrorException(err);

                } catch (IOException e) {
                    disconnectionError();
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NetworkErrorException e) {
                    e.printStackTrace();
                }
            }

            finish();
        }
    };

    //Listener for item add buttons
    private View.OnClickListener itemPicListener = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            Intent getImageIntent = new Intent();
            getImageIntent.setType("image/*");
            getImageIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(getImageIntent, "Select Image for Item"), GET_IMAGE);
        }
    };

    //Listener for item delete button
    private View.OnClickListener deleteItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            userapiservice = UserInfoHolder.getInstance().getAPIService();
            Call<ResponseBody> deleteCall = userapiservice.deleteItem(AddItemWindowActivity.this.itemId);
            Response<ResponseBody> response;
            try {
                response = deleteCall.execute();
                if (response.code() != 200) {
                    Log.d("HTTP ERROR CODE: ", "" + response.code());
                    throw new IOException("Disconnected");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Intent intent = new Intent(AddItemWindowActivity.this, DisconnectionError.class);
                startActivity(intent);
            }
            finish();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GET_IMAGE && resultCode == Activity.RESULT_OK) {
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
            imageSet = true;
        }
    }
}
