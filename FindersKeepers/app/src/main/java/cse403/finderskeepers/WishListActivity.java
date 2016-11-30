package cse403.finderskeepers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cse403.finderskeepers.data.UserInfoHolder;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static cse403.finderskeepers.UserSettingsActivity.JSON;

public class WishListActivity extends AppCompatActivity {
    private List<Boolean> selected;
    private List<String> listItems;
    private String[] items;
    private ListView scrollView;
    private Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        final ImageView image = (ImageView) findViewById(R.id.add_delete);
        this.selected = new ArrayList<Boolean>();
        this.c = this;
        ImageView v = (ImageView) findViewById(R.id.add_delete);
        final EditText edit = (EditText) findViewById(R.id.field);
        v.setImageResource(R.mipmap.add);


        /////set tags

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
            e.printStackTrace();
        } catch (IOException e) {
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String tagString = "";
        try {
            if(UserJSON == null) throw new JSONException("OH NOE");
            JSONArray tag = UserJSON.getJSONArray("wishlist");
            UserJSON.getJSONArray("wishlist");
            for(int i = 0; i < tag.length() - 1; i++) {
                tagString += tag.getString(i) + " ";
            }
            if(tag.length() > 0) tagString += tag.getString(tag.length() - 1);
            else tagString = "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ////

        final Scanner scan = new Scanner(tagString);
        this.scrollView = (ListView) findViewById(R.id.taglist);
        scrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(selected.get(position)){
                    view.setBackgroundColor(Color.TRANSPARENT);
                    selected.set(position, false);
                }else{
                    view.setBackgroundColor(Color.parseColor("#FFF000"));
                    selected.set(position, true);
                }
                image.setImageResource(R.mipmap.add);
                for(int i = selected.size() - 1; i >= 0; i--){
                    if(selected.get(i)){
                        image.setImageResource(R.mipmap.delete);
                        break;
                    }
                }
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            boolean clicked = false;
            @Override
            public void onClick(View v) {
                if(clicked){
                    for(int i = selected.size() - 1; i >= 0; i--){
                        if(selected.get(i)){
                            selected.remove(i);
                            listItems.remove(i);
                        }
                    }
                    items = new String[listItems.size()];
                    listItems.toArray(items);
                    ArrayAdapter adapter = new ArrayAdapter(c, android.R.layout.simple_list_item_1, items);
                    scrollView.setAdapter(adapter);
                    image.setImageResource(R.mipmap.add);
                }else{
                    listItems.add(edit.getText().toString());
                    items = new String[listItems.size()];
                    listItems.toArray(items);
                    selected.add(false);
                    ArrayAdapter adapter = new ArrayAdapter(c, android.R.layout.simple_list_item_1, items);
                    scrollView.setAdapter(adapter);
                    edit.setText("");
                    image.setImageResource(R.mipmap.delete);
                }
                clicked = !clicked;

                JSONObject requestJSON = new JSONObject();
                try {
                    JSONArray wishlist = new JSONArray();
                    for(String s : listItems) {
                        wishlist.put(s);
                    }
                    requestJSON.put("wishlist", wishlist);
                    requestJSON.put("user_id", UserInfoHolder.getInstance().getUID());
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                Log.d("WishlistJSON: ", requestJSON.toString() );
                RequestBody requestBody = RequestBody.create(JSON, requestJSON.toString());
                UserAPIService userapiservice = UserInfoHolder.getInstance().getAPIService();
                Call<ResponseBody> updateTagsCall = userapiservice.setWishlist(requestBody);
                try {
                    Response<ResponseBody> response = updateTagsCall.execute();
                    if (response.code() != 200) {
                        throw new IOException("HTTP Error " + response.code());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Intent intent = new Intent(WishListActivity.this, DisconnectionError.class);
                    startActivity(intent);
                }
            }
        });

        this.listItems = new ArrayList<String>();
        while(scan.hasNext()){
            selected.add(false);
            listItems.add(scan.next());
        }
        this.items = new String[listItems.size()];
        listItems.toArray(items);
        ArrayAdapter adapter = new ArrayAdapter(c, android.R.layout.simple_list_item_1, items);
        scrollView.setAdapter(adapter);
    }
}