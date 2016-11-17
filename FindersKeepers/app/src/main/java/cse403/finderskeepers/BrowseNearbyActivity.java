package cse403.finderskeepers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import cse403.finderskeepers.data.UserInfoHolder;
import static cse403.finderskeepers.UserSettingsActivity.JSON;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class BrowseNearbyActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_nearby);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO: Add items to view
        JSONArray usersArray = new JSONArray();

        LinearLayout users = (LinearLayout) findViewById(R.id.nearby_users_layout);
        UserAPIService userapiservice = UserInfoHolder.getInstance().getAPIService();

        if (UserInfoHolder.getInstance().getZip() == -1) {
            TextView labelText = (TextView) findViewById(R.id.browse_nearby_text_view);
            labelText.setText("Please Enter a ZIP Code on the Settings Screen");
            return;
        }

        try {
            JSONObject queryObj = new JSONObject().put("zipcode", UserInfoHolder.getInstance().getZip());
            queryObj.put("radius", 20).toString();
            Call<ResponseBody> nearbyUsers = userapiservice.getNearbyUsers(RequestBody.create(JSON, queryObj.toString()));
            Response<ResponseBody> doCall = nearbyUsers.execute();

            if (doCall.code() != 200){
                throw new IOException("HTTP ERROR");
            }

            JSONObject nearbyJSON = new JSONObject(doCall.body().string());
            usersArray = nearbyJSON.getJSONArray("users");

        } catch (JSONException e) {
            disconnectionError();
            e.printStackTrace();
        } catch (IOException e) {
            disconnectionError();
            e.printStackTrace();
        }

        for (int i = 0; i < usersArray.length(); i++) {
            URL getImg = null;
            int UID = -1;
            String userName = "";
            try {
                getImg = new URL(usersArray.getJSONObject(i).getString("image_url"));
                UID = usersArray.getJSONObject(i).getInt("user_id");
                userName = usersArray.getJSONObject(i).getString("name");
            } catch (MalformedURLException e) {
                disconnectionError();
                e.printStackTrace();
            } catch (JSONException e) {
                disconnectionError();
                e.printStackTrace();
            }

            if (getImg != null && !getImg.toString().equals("")) {
                Bitmap image = null;
                try {
                    image = BitmapFactory.decodeStream(getImg.openConnection().getInputStream());
                } catch (IOException e) {
                    disconnectionError();
                    e.printStackTrace();
                }
                if (image != null) {
                    BrowseResultUser newUserResult = new BrowseResultUser(this, UID);
                    LinearLayout.LayoutParams params = new LinearLayout
                            .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    newUserResult.setImageBitmap(image);
                    newUserResult.setLayoutParams(params);
                    newUserResult.setAdjustViewBounds(true);
                    newUserResult.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    newUserResult.setOnClickListener(userClickListener);
                    users.addView(newUserResult);

                    TextView userLabel = new TextView(this);
                    userLabel.setLayoutParams(params);
                    userLabel.setText(userName);
                    userLabel.setTextSize(30);
                    users.addView(userLabel);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browse_nearby_activity, menu);
        return true;
    }

    /**
     * Listener which opens user page
     */
    private View.OnClickListener userClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent viewUserIntent = new Intent(BrowseNearbyActivity.this, OtherUserPageActivity.class);
            viewUserIntent.putExtra("USERID", view.getId());
            startActivity(viewUserIntent);
        }
    };

    private void disconnectionError(){
        Intent intent = new Intent(BrowseNearbyActivity.this, DisconnectionError.class);
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
                Intent settingsIntent = new Intent(BrowseNearbyActivity.this, UserSettingsActivity.class);
                finish();
                startActivity(settingsIntent);
                return true;
            case R.id.action_user_page:
                Intent homePageIntent = new Intent(BrowseNearbyActivity.this, HomePage.class);
                finish();
                startActivity(homePageIntent);
                return true;
            case R.id.action_browse_users:
                return true;
            case R.id.action_view_trades:
                Intent viewTradesIntent = new Intent(BrowseNearbyActivity.this, ViewTradesActivity.class);
                finish();
                startActivity(viewTradesIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
