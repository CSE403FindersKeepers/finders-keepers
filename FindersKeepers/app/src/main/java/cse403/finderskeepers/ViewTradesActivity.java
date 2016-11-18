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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

public class ViewTradesActivity extends AppCompatActivity {

    private void disconnectionError(){
        Intent intent = new Intent(ViewTradesActivity.this, DisconnectionError.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trades);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        populateTrades();

        // TODO: Add logic to populate incoming/outgoing/completed trades
    }


    private void populateTrades(){
        UserAPIService userapiservice = UserInfoHolder.getInstance().getAPIService();


        try {
            Call<ResponseBody> trad = userapiservice.getTrades(UserInfoHolder.getInstance().getUID());
            Response<ResponseBody> doCall = trad.execute();

            JSONObject TradesJSON = new JSONObject(doCall.body().string());

            if(TradesJSON == null) throw new JSONException("OH NOE");
            Log.d("Trades Response: ", TradesJSON.toString());
            JSONArray trades = TradesJSON.getJSONArray("trades");
            for (int i = 0; i < trades.length(); i++) {
                JSONObject trade = trades.getJSONObject(i);
                int OUID = trade.getInt("initiator_id");
                Call <ResponseBody> origUserCall = userapiservice.getUser(OUID);
                Response<ResponseBody> origUserResp = origUserCall.execute();
                JSONObject origUserJSON = new JSONObject(origUserResp.body().string());
                Log.d("TradUserJSON: ", origUserJSON.toString());
                origUserJSON = origUserJSON.getJSONObject("user");
                URL avatar = new URL(origUserJSON.getString("image_url"));

                // Id, tags, and bitmap of this item
                int tradeID = trade.getInt("trade_id");
                String status = trade.getString("status");
                Bitmap itemBitmap = null;

                // populate bitmap
                try {
                    itemBitmap = BitmapFactory.decodeStream(avatar.openConnection().getInputStream());
                } catch (IOException e) {
                    disconnectionError();
                    e.printStackTrace();
                }

                // Add item to layout as button
                LinearLayout section = null;
                switch(status) {
                    case "PENDING":
                        if (trade.getInt("initiator_id") == UserInfoHolder.getInstance().getUID())
                             section = (LinearLayout) findViewById(R.id.outgoing_trades_list);
                        else section = (LinearLayout) findViewById(R.id.incoming_trades_list);
                        break;
                    case "ACCEPTED":
                        section = (LinearLayout) findViewById(R.id.completed_trades_list);
                        break;
                    default:
                        section = (LinearLayout) findViewById(R.id.rejected_trades_list);
                        break;
                }


                if (itemBitmap != null) {
                    AddableItem newItemButton = new AddableItem(this, status, tradeID);
                    LinearLayout.LayoutParams params = new LinearLayout
                            .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    newItemButton.setImageBitmap(itemBitmap);
                    newItemButton.setLayoutParams(params);
                    newItemButton.setAdjustViewBounds(true);
                    newItemButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    newItemButton.setOnClickListener(viewTradeListener);
                    section.addView(newItemButton, 0);
                }
            }
        } catch (JSONException e) {
            disconnectionError();
            e.printStackTrace();
        } catch (MalformedURLException e) {
            disconnectionError();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener viewTradeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent addItemIntent = new Intent(ViewTradesActivity.this, ViewUserTradeActivity.class);
            addItemIntent.putExtra("TRADEID", ((AddableItem) view).getItemId());
            addItemIntent.putExtra("STATUS", ((AddableItem) view).getTags());
            startActivity(addItemIntent);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_trades_activity, menu);
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
                Intent settingsIntent = new Intent(ViewTradesActivity.this, UserSettingsActivity.class);
                finish();
                startActivity(settingsIntent);
                return true;
            case R.id.action_user_page:
                Intent homePageIntent = new Intent(ViewTradesActivity.this, HomePage.class);
                finish();
                startActivity(homePageIntent);
                return true;
            case R.id.action_browse_users:
                Intent browseUsersIntent = new Intent(ViewTradesActivity.this, BrowseNearbyActivity.class);
                finish();
                startActivity(browseUsersIntent);
                return true;
            case R.id.action_view_trades:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
