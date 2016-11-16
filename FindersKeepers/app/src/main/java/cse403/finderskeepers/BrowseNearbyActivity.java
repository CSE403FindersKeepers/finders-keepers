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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import cse403.finderskeepers.data.UserInfoHolder;

public class BrowseNearbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_nearby);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO: Add items to view
        URL getImg = null;
        try {
            getImg = new URL("http://i.imgur.com/ibsZi5R.png");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (getImg != null && !getImg.toString().equals("")) {
            Bitmap image = null;
            try {
                image = BitmapFactory.decodeStream(getImg.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
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
            //TODO: add user info to extras in intent

            startActivity(viewUserIntent);
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
