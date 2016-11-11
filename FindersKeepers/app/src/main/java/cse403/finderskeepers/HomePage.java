package cse403.finderskeepers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import cse403.finderskeepers.data.UserInfoHolder;
import cse403.finderskeepers.UserAPIService;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static cse403.finderskeepers.UserSettingsActivity.JSON;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Adjust thread policy to allow for network access
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Create Retrofit service for api calls
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserInfoHolder.SERVER_ADDRESS)
                .build();

        UserAPIService userapiservice = retrofit.create(UserAPIService.class);

        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView userName = (TextView) findViewById(R.id.user_name_text);
        userName.setText(UserInfoHolder.getInstance().getUserName());

        //Button with a click listener which allows user to add an item
        ImageButton img = (ImageButton) findViewById(R.id.add_item);
        img.setOnClickListener(this.itemListener);

        //get UID - replace this with UID from request
        int UID = 0;
        try {
            Call<ResponseBody> userID = userapiservice.makeUser(
                    RequestBody.create(JSON,
                            new JSONObject().put("email", UserInfoHolder.getInstance().getEmail()).toString()));
            Response<ResponseBody> doCall = userID.execute();

            Log.d("Response val for uid:", " " + doCall.code());

            if (doCall.code() != 200){
                throw new IOException("OMG HTTP \n\n\n\n\n\n\nn\n\n ERRUR");
            }


            JSONObject UIDJson = new JSONObject(doCall.body().string());
            UID = UIDJson.getInt("user_id");

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.toString();
            e.printStackTrace();
            finish();
        }
        Log.d("DID IT WORK?!?!?!?!?", "" + UID);
        UserInfoHolder.getInstance().setUID(UID);

        //fetch avatar - populate this URL with URL from response
        URL getImg = null;
        try {
            getImg = new URL("http://i.imgur.com/ibsZi5R.png");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JSONObject UserJSON = null;
        try {
            Call <ResponseBody> user = userapiservice.getUser(UID);
            Response<ResponseBody> doCall = user.execute();

            Log.d("Response val for user:", " " + doCall.code());

            if (doCall.code() != 200){
                throw new IOException("OMG HTTP \n\n\n\n\n\n\nn\n\n ERRUR");
            }


            UserJSON = new JSONObject(doCall.body().string());
            getImg = new URL(UserJSON.getString("image_url"));

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (getImg.toString() != "") {
                Bitmap image = BitmapFactory.decodeStream(getImg.openConnection().getInputStream());
                UserInfoHolder.getInstance().setAvatar(image);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        ImageView userAvatar = (ImageView) findViewById(R.id.user_avatar);
        if (UserInfoHolder.getInstance().getAvatar() != null) {
            userAvatar.setImageBitmap(UserInfoHolder.getInstance().getAvatar());
        }

        //TODO: fetch tags - replace this array with populated one
        JSONArray tags = null;
        String tagString = "";
        try {
            if(UserJSON == null) throw new JSONException("OH NOE");
            UserJSON.getJSONArray("wishlist");
            for(int i = 0; i < tags.length() - 1; i++) {
                tagString += tags.getString(i) + " ";
            }
            tagString += tags.getString(tags.length() - 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Set tags in TextView
        TextView tagText = (TextView) findViewById(R.id.editTags);
        tagText.setText(tagString);

        //TODO: populate inventory - populate this JSONArray with array of items

        try {
            if(UserJSON == null) throw new JSONException("OH NOE");
            JSONArray inventory = UserJSON.getJSONArray("inventory");
            for (int i = 0; i < inventory.length(); i++) {
                JSONObject item = inventory.getJSONObject(i);
                URL itemImage = new URL(item.getString("image_url"));
                JSONArray itemTags = item.getJSONArray("tags");

                // Id, tags, and bitmap of this item
                int itemID = item.getInt("item_id");
                String itemTagString = "";
                Bitmap itemBitmap = null;

                // populate tag string
                try {
                    for (int j = 0; i < itemTags.length() - 1; j++) {
                        itemTagString += itemTags.getString(j) + " ";
                    }
                    itemTagString += itemTags.getString(itemTags.length() - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // populate bitmap
                try {
                    itemBitmap = BitmapFactory.decodeStream(itemImage.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Add item to layout as button
                //TODO: Default image if itemBitmap == null
                LinearLayout items = (LinearLayout) findViewById(R.id.item_list);
                if (itemBitmap != null) {
                    AddableItem newItemButton = new AddableItem(this, itemTagString, itemID);
                    newItemButton.setImageBitmap(itemBitmap);
                    items.addView(newItemButton, 0);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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
            Intent addItemIntent = new Intent(HomePage.this, AddItemWindowActivity.class);
            finish();
            startActivity(addItemIntent);
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
}
