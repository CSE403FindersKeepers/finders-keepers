package cse403.finderskeepers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import cse403.finderskeepers.data.UserInfoHolder;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class OtherUserPageActivity extends AppCompatActivity {

    private int uid;

    private void disconnectionError(){
        Intent intent = new Intent(OtherUserPageActivity.this, DisconnectionError.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        uid = -1;
        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("USERID")){
            uid = getIntent().getExtras().getInt("USERID");
        }

        populatePage();

        //TODO: populate avatar

        //TODO: populate user name text field

        //TODO: populate item list - use AddableItems, add viewItemListener

        //TODO: populate tags field
    }

    private void populatePage() {
        UserAPIService userapiservice = UserInfoHolder.getInstance().getAPIService();

        URL getImg = null;

        LinearLayout items = (LinearLayout) findViewById(R.id.item_list);
        while (items.getChildCount() > 1) {
            items.removeViewAt(0);
        }

        JSONObject UserJSON = null;
        try {
            Call<ResponseBody> user = userapiservice.getUser(uid);
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

        Bitmap image = null;

        try {
            if (getImg != null && !getImg.toString().equals("")) {
                image = BitmapFactory.decodeStream(getImg.openConnection().getInputStream());
            }
        } catch (IOException e) {
            disconnectionError();
            e.printStackTrace();
        }

        ImageView userAvatar = (ImageView) findViewById(R.id.user_avatar);
        if (UserInfoHolder.getInstance().getAvatar() != null) {
            userAvatar.setImageBitmap(image);
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
        TextView tagText = (TextView) findViewById(R.id.user_tags);
        tagText.setText(tagString);

        TextView userName = (TextView) findViewById(R.id.user_name_text);
        try {
            userName.setText(UserJSON.getString("name"));
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
                    newItemButton.setOnClickListener(viewItemListener);
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
     * Listener which opens item viewing window
     */
    private View.OnClickListener viewItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent addItemIntent = new Intent(OtherUserPageActivity.this, OtherUserItemViewActivity.class);
            addItemIntent.putExtra("ITEM_ID", ((AddableItem) view).getItemId());
            addItemIntent.putExtra("TAGS", ((AddableItem) view).getTags());
            startActivity(addItemIntent);
        }
    };

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(OtherUserPageActivity.this, UserSettingsActivity.class);
                finish();
                startActivity(settingsIntent);
                return true;
            case R.id.action_user_page:
                Intent homePageIntent = new Intent(OtherUserPageActivity.this, HomePage.class);
                finish();
                startActivity(homePageIntent);
                return true;
            case R.id.action_browse_users:
                Intent browseIntent = new Intent(OtherUserPageActivity.this, BrowseNearbyActivity.class);
                finish();
                startActivity(browseIntent);
                return true;
            case R.id.action_view_trades:
                Intent viewTradesIntent = new Intent(OtherUserPageActivity.this, ViewTradesActivity.class);
                finish();
                startActivity(viewTradesIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
