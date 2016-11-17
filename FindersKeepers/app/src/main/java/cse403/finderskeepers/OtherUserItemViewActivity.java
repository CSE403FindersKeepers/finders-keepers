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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import cse403.finderskeepers.data.UserInfoHolder;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class OtherUserItemViewActivity extends AppCompatActivity {

    private int itemId;

    private UserAPIService userapiservice;

    private void disconnectionError() {
        Intent intent = new Intent(OtherUserItemViewActivity.this, DisconnectionError.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_item_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView img = (ImageView) findViewById(R.id.view_item_img);

        userapiservice = UserInfoHolder.getInstance().getAPIService();

        itemId = getIntent().getExtras().getInt("ITEM_ID");
        Bitmap image = null;

        String tags = getIntent().getExtras().getString("TAGS");
        TextView editTags = (TextView) findViewById(R.id.view_item_tags);
        editTags.setText(tags);


        try {
            Response<ResponseBody> doCall = userapiservice.getItem(this.itemId).execute();

            if (doCall.code() != 200) {
                throw new IOException("OMG HTTP ERROR");
            }

            String jsonval = doCall.body().string();
            JSONObject itemObj = new JSONObject(jsonval).getJSONObject("item");
            URL imgloc = new URL(itemObj.getString("image_url"));

            try {
                if (!imgloc.toString().equals("")) {
                    image = BitmapFactory.decodeStream(imgloc.openConnection().getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
                disconnectionError();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Change text of add item button if item info exists
        img.setImageBitmap(image);
    }

}
