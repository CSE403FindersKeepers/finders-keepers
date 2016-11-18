package cse403.finderskeepers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.vision.text.Line;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import cse403.finderskeepers.data.UserInfoHolder;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static cse403.finderskeepers.UserSettingsActivity.JSON;

public class ProposeTradeActivity extends AppCompatActivity {

    private int otherUID = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propose_trade);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("USERID")){
            otherUID = getIntent().getExtras().getInt("USERID");
        }

        Button proposeTradeButton = (Button) findViewById(R.id.send_trade);
        proposeTradeButton.setOnClickListener(proposeListener);

        populatePage();

        // TODO: Populate Item Lists

        // TODO: Add Item Selection Functionality - move items from "Your Items" and "Their Items" to chosen item groups

        // TODO: Add Item Deselection Functionality - move items back after they're clicked on in the other list
    }

    /**
     * Listener which selects your items
     */
    private View.OnClickListener selectOwnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout unselected = (LinearLayout) findViewById(R.id.this_user_item_list);
            LinearLayout selected = (LinearLayout) findViewById(R.id.this_user_selected_item_list);
            unselected.removeView(view);
            selected.addView(view);
            view.setOnClickListener(deselectOwnListener);
        }
    };

    /**
     * Listener which deselects your items
     */
    private View.OnClickListener deselectOwnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout unselected = (LinearLayout) findViewById(R.id.this_user_item_list);
            LinearLayout selected = (LinearLayout) findViewById(R.id.this_user_selected_item_list);
            selected.removeView(view);
            unselected.addView(view);
            view.setOnClickListener(selectOwnListener);
        }
    };

    /**
     * Listener which selects the other users's items
     */
    private View.OnClickListener selectOtherListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout unselected = (LinearLayout) findViewById(R.id.their_item_list);
            LinearLayout selected = (LinearLayout) findViewById(R.id.their_selected_item_list);
            unselected.removeView(view);
            selected.addView(view);
            view.setOnClickListener(deselectOtherListener);
        }
    };


    /**
     * Listener which deselect's the other's user's items
     */
    private View.OnClickListener deselectOtherListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout unselected = (LinearLayout) findViewById(R.id.their_item_list);
            LinearLayout selected = (LinearLayout) findViewById(R.id.their_selected_item_list);
            selected.removeView(view);
            unselected.addView(view);
            view.setOnClickListener(selectOtherListener);
        }
    };

    /**
     * Listener which proposes trade
     */
    private View.OnClickListener proposeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout ourItems = (LinearLayout) findViewById(R.id.this_user_selected_item_list);
            LinearLayout theirItems = (LinearLayout) findViewById(R.id.their_selected_item_list);

            JSONObject startTradeObject = new JSONObject();
            try {
                startTradeObject.put("initiator_id", UserInfoHolder.getInstance().getUID());
                startTradeObject.put("recipient_id", otherUID);
                JSONArray ourItemIds = new JSONArray();
                JSONArray theirItemIds = new JSONArray();

                int i;
                for (i = 0; i < ourItems.getChildCount(); i++) {
                    AddableItem ourItem = (AddableItem) ourItems.getChildAt(i);
                    ourItemIds.put(ourItem.getItemId());
                }

                for (i = 0; i < theirItems.getChildCount(); i++) {
                    AddableItem theirItem = (AddableItem) theirItems.getChildAt(i);
                    theirItemIds.put(theirItem.getItemId());
                }

                startTradeObject.put("offered_item_ids", ourItemIds);
                startTradeObject.put("requested_item_ids", theirItemIds);

                UserAPIService userAPIService = UserInfoHolder.getInstance().getAPIService();

                RequestBody requestBody = RequestBody.create(JSON, startTradeObject.toString());
                Call<ResponseBody> startTrade = userAPIService.startTrade(requestBody);
                startTrade.execute();
                finish();
            } catch (JSONException e) {
                disconnectionError();
                e.printStackTrace();
            } catch (IOException e) {
                disconnectionError();
                e.printStackTrace();
            }
        }
    };

    private void populatePage() {
        UserAPIService userapiservice = UserInfoHolder.getInstance().getAPIService();

        LinearLayout ourItems = (LinearLayout) findViewById(R.id.this_user_item_list);
        LinearLayout theirItems = (LinearLayout) findViewById(R.id.their_item_list);

        Call<ResponseBody> ourInventory = userapiservice.getInventory(UserInfoHolder.getInstance().getUID());
        Call<ResponseBody> theirInventory = userapiservice.getInventory(otherUID);

        try {
            Response<ResponseBody> doOurInventoryCall = ourInventory.execute();
            Response<ResponseBody> doTheirInventoryCall = theirInventory.execute();

            if (doOurInventoryCall.code() != 200 || doTheirInventoryCall.code() != 200) {
                throw new IOException("HTTP ERROR");
            }

            String ourInventoryString = doOurInventoryCall.body().string();
            String theirInventoryString = doTheirInventoryCall.body().string();



            JSONArray ourInventoryArray = new JSONObject(ourInventoryString).getJSONArray("items");
            JSONArray theirInventoryArray = new JSONObject(theirInventoryString).getJSONArray("items");

            populateLayoutWithItems(ourInventoryArray, ourItems, selectOwnListener);
            populateLayoutWithItems(theirInventoryArray, theirItems, selectOtherListener);
        } catch (IOException e) {
            disconnectionError();
            e.printStackTrace();
        } catch (JSONException e) {
            disconnectionError();
            e.printStackTrace();
        }
    }

    private void populateLayoutWithItems(JSONArray inventory, LinearLayout layout, View.OnClickListener listener) {
        for (int i = 0; i < inventory.length(); i++) {
            try {
                JSONObject item = inventory.getJSONObject(i);
                Bitmap image;

                URL itemImageURL = new URL(item.getString("image_url"));
                image = BitmapFactory.decodeStream(itemImageURL.openConnection().getInputStream());

                if (image != null) {
                    AddableItem newItem = new AddableItem(this, "", item.getInt("item_id"));
                    LinearLayout.LayoutParams params = new LinearLayout
                            .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    newItem.setImageBitmap(image);
                    newItem.setLayoutParams(params);
                    newItem.setAdjustViewBounds(true);
                    newItem.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    newItem.setOnClickListener(listener);
                    layout.addView(newItem, 0);
                }
            } catch (JSONException e) {
                disconnectionError();
                e.printStackTrace();
            } catch (MalformedURLException e) {
                disconnectionError();
                e.printStackTrace();
            } catch (IOException e) {
                disconnectionError();
                e.printStackTrace();
            }

        }
    }

    private void disconnectionError(){
        Intent intent = new Intent(ProposeTradeActivity.this, DisconnectionError.class);
        startActivity(intent);
    }
}
