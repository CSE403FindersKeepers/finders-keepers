package cse403.finderskeepers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import cse403.finderskeepers.data.UserInfoHolder;
import okhttp3.ResponseBody;
import retrofit2.Call;

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

        }
    };

    /**
     * Listener which deselects your items
     */
    private View.OnClickListener deselectOwnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    /**
     * Listener which selects the other users's items
     */
    private View.OnClickListener selectOtherListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };


    /**
     * Listener which deselect's the other's user's items
     */
    private View.OnClickListener deselectOtherListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    /**
     * Listener which proposes trade
     */
    private View.OnClickListener proposeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private void populatePage() {
        UserAPIService userapiservice = UserInfoHolder.getInstance().getAPIService();

        LinearLayout ourItems = (LinearLayout) findViewById(R.id.this_user_item_list);
        LinearLayout theirItems = (LinearLayout) findViewById(R.id.their_item_list);

        Call<ResponseBody> ourInventory = userapiservice.getInventory(UserInfoHolder.getInstance().getUID());
        Call<ResponseBody> theirInventory = userapiservice.getInventory(otherUID);
    }
}
