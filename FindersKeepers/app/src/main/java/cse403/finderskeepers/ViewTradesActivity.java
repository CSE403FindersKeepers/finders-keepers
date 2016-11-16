package cse403.finderskeepers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ViewTradesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trades);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO: Add logic to populate incoming/outgoing/completed trades
    }

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
