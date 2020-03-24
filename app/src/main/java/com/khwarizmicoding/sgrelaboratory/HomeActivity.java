package com.khwarizmicoding.sgrelaboratory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.khwarizmicoding.sgrelaboratory.Adapter.CategoryAdapter;
import com.khwarizmicoding.sgrelaboratory.Adapter.ProductAdapter;
import com.khwarizmicoding.sgrelaboratory.Api.ApiService;
import com.khwarizmicoding.sgrelaboratory.Api.RetrofitBuilder;
import com.khwarizmicoding.sgrelaboratory.Model.CategoryPost;
import com.khwarizmicoding.sgrelaboratory.Model.ProductPost;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private static boolean searchIconListener =false;
    private ApiService service;
    private TokenManager tokenManager;
    private Call<ProductPost> callProduct;
    private Call<CategoryPost> callCategory;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private RecyclerView recyclerViewProduct, recyclerViewCategory;
    private ImageView searchIcon, acc;
    private LinearLayout searchBar, topLogo;
    private DrawerLayout sideBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout loadingData, inValidInternet;
    private ConstraintLayout constraintLayout;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        constraintLayout=(ConstraintLayout)findViewById(R.id.linearLayout2);
        constraintLayout.setVisibility(View.VISIBLE);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawerLayout);
        loadingData=(RelativeLayout)findViewById(R.id.loadingData);
        inValidInternet=(RelativeLayout)findViewById(R.id.inValidInternet);
        inValidInternet.setVisibility(View.GONE);
        loadingData.setVisibility(View.VISIBLE);
        if(isConnected()){
            ButterKnife.bind(this);
            tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
            if(tokenManager.getToken() == null){
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
            service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
            searchIcon = (ImageView) findViewById(R.id.seachIcon);
            searchBar = (LinearLayout) findViewById(R.id.searchBar);
            topLogo = (LinearLayout) findViewById(R.id.topLogo);
            acc = (ImageView) findViewById(R.id.account);
            //Product Action
            recyclerViewProduct = (RecyclerView) findViewById(R.id.loadProduct);
            recyclerViewProduct.setLayoutManager(new LinearLayoutManager(this));
            loadProductList();
            //Category Action
            recyclerViewCategory = (RecyclerView) findViewById(R.id.loadCategory);
            recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            loadCategoryList();
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadProductList();
                    loadCategoryList();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    constraintLayout.setVisibility(View.GONE);
                    drawerLayout.setVisibility(View.VISIBLE);
                }
            }, 3000);
        }else{
            loadingData.setVisibility(View.GONE);
            inValidInternet.setVisibility(View.VISIBLE);
        }
    }
    private void loadProductList(){
        callProduct = service.products();
        callProduct.enqueue(new Callback<ProductPost>(){
            @Override
            public void onResponse(Call<ProductPost> call, Response<ProductPost> response) {
                if(response.isSuccessful()) {
                    productAdapter = new ProductAdapter(HomeActivity.this, response.body());
                    runAnimationProduct(recyclerViewProduct, productAdapter, 2);
                }else{
                    Toast.makeText(HomeActivity.this, "Success but not exits data", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ProductPost> call, Throwable t) {
                Toast.makeText(HomeActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadCategoryList(){
        callCategory = service.categories();
        callCategory.enqueue(new Callback<CategoryPost>(){
            @Override
            public void onResponse(Call<CategoryPost> call, Response<CategoryPost> response) {
                if(response.isSuccessful()) {
                    categoryAdapter = new CategoryAdapter(HomeActivity.this, response.body());
                    runAnimationCategory(recyclerViewCategory, categoryAdapter, 2);
                }else{
                    Toast.makeText(HomeActivity.this, "Success but not exits data", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<CategoryPost> call, Throwable t) {
                Toast.makeText(HomeActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void runAnimationProduct(RecyclerView recyclerView, ProductAdapter adapter, int type){
        Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;
        if (type == 2) controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_slide_from_right);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    private void runAnimationCategory(RecyclerView recyclerView, CategoryAdapter adapter, int type){
        Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;
        if (type == 2) controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    private boolean isConnected() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    @OnClick(R.id.seachIcon)
    void clickSearch() {
        if (!searchIconListener) {
            searchBar.setVisibility(View.VISIBLE);
            topLogo.setVisibility(View.GONE);
            searchIconListener =true;
        }else {
            searchBar.setVisibility(View.GONE);
            topLogo.setVisibility(View.VISIBLE);
            searchIconListener = false ;
        }
    }
    @OnClick(R.id.account)
    public void clickAccount() {
        sideBar = (DrawerLayout) findViewById(R.id.drawerLayout);
        sideBar.openDrawer(Gravity.LEFT);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (callProduct != null) {
            callProduct.cancel();
            callProduct = null;
        }
        if (callCategory != null) {
            callCategory.cancel();
            callCategory = null;
        }
    }
}
