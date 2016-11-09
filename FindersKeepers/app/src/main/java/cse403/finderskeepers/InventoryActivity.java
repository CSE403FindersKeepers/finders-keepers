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
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import cse403.finderskeepers.data.UserInfoHolder;

/**
 * Created by Jared on 11/8/2016.
 */

public class InventoryActivity extends AppCompatActivity {


    //Create our activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton addItem = (ImageButton) findViewById(R.id.add_item);
        ImageButton addWishlist = (ImageButton) findViewById(R.id.add_wishlist_item);
        retrieveItems(UserInfoHolder.getInstance());
        addItem.setOnClickListener(this.invListener);
        addWishlist.setOnClickListener(this.wishListener);
    }

    //Retrieves Wishlist & Inventory items for given user from server, and displays them in the
    //inventory.
    private void retrieveItems(UserInfoHolder user){
        /*TODO:
         * Make API call for inventory of given user. Once we get the response, parse the json
         * and use it to create AddableItems that we then add to the inventory page.
         * These AddableItems should be clickable to opens some sort of info page -- haven't gotten
         * that far yet. */
        String query = "/mock/api/get_inventory/" + UserInfoHolder.getInstance().getUID();
        try {
            URLConnection con = new URL(UserInfoHolder.SERVER_ADDRESS + query).openConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    //Listener for the Inventory buttons
    private View.OnClickListener invListener = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            Intent addItemIntent = new Intent(InventoryActivity.this, AddItemWindowActivity.class);
            startActivityForResult(Intent.createChooser(addItemIntent, "Add a new item"), 1);
        }
    };

    //Listener for the Wihslist buttons
    private View.OnClickListener wishListener = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            /*TODO:
             * Should behave as the tag adding in AddItemWindowActivity, but instead of having
              * a separate upload button, immediately communicates changes to server.
              * (might change this? but should be OK I think, if a bit slow)*/
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*TODO:
         * Refresh inventory when AddItemWindowActivity closes. */
    }
}

