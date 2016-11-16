package cse403.finderskeepers;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import cse403.finderskeepers.data.UserInfoHolder;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.util.List;



/**
 * Created by Jared on 10/27/2016.
 * Note: API specifications may change. To facilitate frontend/backend communication,
 * please refer to and update the separate API specification document for documentation of
 * API calls and functionality.
 */

public interface UserAPIService {

    @POST("/api/create_user")
    Call<ResponseBody> makeUser(@Body RequestBody request);

    @GET("/api/get_user/{id}")
    Call<ResponseBody> getUser(@Path("id") int id);

    @PUT("/api/update_user")
    Call<ResponseBody> updateUser(@Body RequestBody request);

    @GET("/api/get_item/{id}")
    Call<ResponseBody> getItem(@Path("id") int id);

    @POST("/api/create_item")
    Call<ResponseBody> makeItem(@Body RequestBody request);

    @PUT("/api/update_item")
    Call<ResponseBody> updateItem(@Body RequestBody request);

    @DELETE("/api/delete_item/{id}")
    Call<ResponseBody> deleteItem(@Path("id") int id);

    @GET("/api/get_wishlist/{id}")
    Call<ResponseBody> getWishlist(@Path("id") int id);

    @PUT("/api/set_wishlist")
    Call<ResponseBody> setWishlist(@Body RequestBody request);

    @GET("/api/get_inventory/{id}")
    Call<ResponseBody> getInventory(@Path("id") int id);

    @GET("/mock/api/get_trade/{id}")
    Call<ResponseBody> getTrade(@Path("id") int id);

    @GET("/mock/api/get_trades/{id}")
    Call<ResponseBody> getTrades(@Path("id") int id);

    @POST("/mock/api/start_trade")
    Call<ResponseBody> startTrade(@Body RequestBody request);

    @PUT("/mock/api/accept_trade")
    Call<ResponseBody> acceptTrade(@Body RequestBody request);

    @GET("/mock/api/get_users_within_radius")
    Call<ResponseBody> getNearbyUsers(@Body RequestBody request);

}
