package cse403.finderskeepers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import cse403.finderskeepers.data.UserInfoHolder;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static cse403.finderskeepers.UserSettingsActivity.JSON;

public class HomePage extends AppCompatActivity {

    private static final int REQUEST_STUFF = 14582;
    private UserAPIService userapiservice;

    private void disconnectionError(){
        Intent intent = new Intent(HomePage.this, DisconnectionError.class);
        startActivity(intent);
    }

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

        userapiservice = retrofit.create(UserAPIService.class);
        UserInfoHolder.getInstance().setAPIService(userapiservice);

        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView userName = (TextView) findViewById(R.id.user_name_text);
        userName.setText(UserInfoHolder.getInstance().getUserName());

        //Button with a click listener which allows user to add an item
        ImageButton img = (ImageButton) findViewById(R.id.add_item);
        img.setOnClickListener(this.itemListener);

        Button updateTagsButton = (Button) findViewById(R.id.update_tags);
        updateTagsButton.setOnClickListener(this.updateTagsListener);
       requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STUFF);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode){
            case REQUEST_STUFF: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    return;
                } else {
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
        return true;
    }

    /**
     * Listener which opens item addition page
     */
    private View.OnClickListener itemListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent addItemIntent = new Intent(HomePage.this, AddItemWindowActivity.class);
            startActivity(addItemIntent);
        }
    };

    /**
     * Listener for tag update button
     */
    private View.OnClickListener updateTagsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            userapiservice = UserInfoHolder.getInstance().getAPIService();
            JSONObject requestJSON = new JSONObject();
            try {
                EditText tags = (EditText) findViewById(R.id.edit_tags);
                Scanner scanner = new Scanner(tags.getText().toString());
                JSONArray wishlist = new JSONArray();
                while (scanner.hasNext()) {
                    wishlist.put(scanner.next());
                }
                requestJSON.put("wishlist", wishlist);
                requestJSON.put("user_id", UserInfoHolder.getInstance().getUID());
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            Log.d("WishlistJSON: ", requestJSON.toString() );
            RequestBody requestBody = RequestBody.create(JSON, requestJSON.toString());
            Call<ResponseBody> updateTagsCall = userapiservice.setWishlist(requestBody);
            try {
                Response<ResponseBody> response = updateTagsCall.execute();
                if (response.code() != 200) {
                    throw new IOException("HTTP Error " + response.code());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Intent intent = new Intent(HomePage.this, DisconnectionError.class);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onStart(){
        super.onStart();
        populateUserPage();
    }

    private void populateUserPage(){
        //get UID - replace this with UID from request


        LinearLayout items = (LinearLayout) findViewById(R.id.item_list);
        while (items.getChildCount() > 1) {
            items.removeViewAt(0);
        }

        UserAPIService userapiservice = UserInfoHolder.getInstance().getAPIService();

        int UID = -1;
        try {
            String instr = new JSONObject().put("email", UserInfoHolder.getInstance().getEmail()).toString();
            Log.d("JSON sent: ", instr);
            Call<ResponseBody> userID = userapiservice.makeUser(RequestBody.create(JSON, instr));
            Log.d("URL Accessed: ", userID.request().url().toString());
            Response<ResponseBody> doCall = userID.execute();

            Log.d("Response val for uid:", " " + doCall.code());

            if (doCall.code() != 200){
                throw new IOException("OMG HTTP \n\n\n\n\n\n\nn\n\n ERRUR");
            }


            JSONObject UIDJson = new JSONObject(doCall.body().string());
            UID = UIDJson.getInt("user_id");

        } catch (JSONException e) {
            disconnectionError();
            e.printStackTrace();
        } catch (IOException e) {
            disconnectionError();
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
            disconnectionError();
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

            String jsonval = doCall.body().string();

            Log.d("UserJSON String:", jsonval);

            UserJSON = new JSONObject(jsonval).getJSONObject("user");
            getImg = new URL(UserJSON.getString("image_url"));

        } catch (JSONException e) {
            disconnectionError();
            e.printStackTrace();
        } catch (IOException e) {
            disconnectionError();
            e.printStackTrace();
        }

        try {
            if (getImg != null && !getImg.toString().equals("")) {
                Bitmap image = BitmapFactory.decodeStream(getImg.openConnection().getInputStream());
                UserInfoHolder.getInstance().setAvatar(image);
            }
        } catch(IOException e) {
            disconnectionError();
            e.printStackTrace();
        }

        ImageView userAvatar = (ImageView) findViewById(R.id.user_avatar);
        if (UserInfoHolder.getInstance().getAvatar() != null) {
            userAvatar.setImageBitmap(UserInfoHolder.getInstance().getAvatar());
        }

        String tagString = "";
        try {
            if(UserJSON == null) throw new JSONException("OH NOE");
            JSONArray tags = UserJSON.getJSONArray("wishlist");
            UserJSON.getJSONArray("wishlist");
            for(int i = 0; i < tags.length() - 1; i++) {
                tagString += tags.getString(i) + " ";
            }
            if(tags.length() > 0) tagString += tags.getString(tags.length() - 1);
            else tagString = "";
        } catch (JSONException e) {
            disconnectionError();
            e.printStackTrace();
        }

        // Set tags in TextView
        TextView tagText = (TextView) findViewById(R.id.edit_tags);
        tagText.setText(tagString);

        try {
            if(UserJSON == null) throw new JSONException("OH NOE");
            UserInfoHolder.getInstance().setZip( UserJSON.getInt("zipcode"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                    for (int j = 0; j < itemTags.length() - 1; j++) {
                        if (!itemTags.getString(j).equals("null"))itemTagString += itemTags.getString(j) + " ";
                    }
                    if (!itemTags.getString(itemTags.length() - 1).equals("null")) itemTagString += itemTags.getString(itemTags.length() - 1);
                } catch (JSONException e) {
                    disconnectionError();
                    e.printStackTrace();
                }

                // populate bitmap
                try {
                    itemBitmap = BitmapFactory.decodeStream(itemImage.openConnection().getInputStream());
                } catch (IOException e) {
                    disconnectionError();
                    e.printStackTrace();
                }

                // Add item to layout as button
                //TODO: Default image if itemBitmap == null
                if (itemBitmap != null) {
                    AddableItem newItemButton = new AddableItem(this, itemTagString, itemID);
                    LinearLayout.LayoutParams params = new LinearLayout
                            .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    newItemButton.setImageBitmap(itemBitmap);
                    newItemButton.setLayoutParams(params);
                    newItemButton.setAdjustViewBounds(true);
                    newItemButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    newItemButton.setOnClickListener(editItemListener);
                    items.addView(newItemButton, 0);
                }
            }
        } catch (JSONException e) {
            disconnectionError();
            e.printStackTrace();
        } catch (MalformedURLException e) {
            disconnectionError();
            e.printStackTrace();
        }
    }

    /**
     * Listener which opens item editing window
     */
    private View.OnClickListener editItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent addItemIntent = new Intent(HomePage.this, AddItemWindowActivity.class);
            /*
            Drawable drawable = ((AddableItem) view).getDrawable();
            Bitmap image = ((BitmapDrawable) drawable).getBitmap();*/
            addItemIntent.putExtra("ITEM_ID", ((AddableItem) view).getItemId());
            addItemIntent.putExtra("TAGS", ((AddableItem) view).getTags());
            startActivity(addItemIntent);
        }
    };

    private void err(){
        Intent intent = new Intent(HomePage.this, DisconnectionError.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(HomePage.this, UserSettingsActivity.class);
                finish();
                startActivity(settingsIntent);
                return true;
            case R.id.action_user_page:
                return true;
            case R.id.action_browse_users:
                Intent browseIntent = new Intent(HomePage.this, BrowseNearbyActivity.class);
                finish();
                startActivity(browseIntent);
                return true;
            case R.id.action_view_trades:
                Intent viewTradesIntent = new Intent(HomePage.this, ViewTradesActivity.class);
                finish();
                startActivity(viewTradesIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
