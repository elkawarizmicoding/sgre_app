package dz.univbechar.sgrelaboratory.Api;

import java.util.List;

import dz.univbechar.sgrelaboratory.Model.CategoryRep;
import dz.univbechar.sgrelaboratory.Model.OrderModel;
import dz.univbechar.sgrelaboratory.Model.ProductSelect;
import dz.univbechar.sgrelaboratory.Model.OrderRep;
import dz.univbechar.sgrelaboratory.Model.ProductRep;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.GET;

public interface ApiService {

    @POST("login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("email") String username, @Field("password") String password);

    @POST("refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);

    @GET("category")
    Call<CategoryRep> categories();

    @GET("product")
    Call<ProductRep> products();

    @POST("order/store")
    Call<OrderRep> storeOrder(@Body OrderModel body);
}
