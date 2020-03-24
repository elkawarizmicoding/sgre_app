package com.khwarizmicoding.sgrelaboratory.Api;

import com.khwarizmicoding.sgrelaboratory.Model.CategoryPost;
import com.khwarizmicoding.sgrelaboratory.Model.ProductPost;

import retrofit2.Call;
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
    Call<CategoryPost> categories();

    @GET("product")
    Call<ProductPost> products();
}
