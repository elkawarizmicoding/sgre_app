package dz.univbechar.sgrelaboratory;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dz.univbechar.sgrelaboratory.Adapter.CartAdapter;
import dz.univbechar.sgrelaboratory.Adapter.CategoryAdapter;
import dz.univbechar.sgrelaboratory.Adapter.LoadingCategoryAdapter;
import dz.univbechar.sgrelaboratory.Adapter.LoadingProductAdapter;
import dz.univbechar.sgrelaboratory.Adapter.ProductAdapter;
import dz.univbechar.sgrelaboratory.Api.ApiError;
import dz.univbechar.sgrelaboratory.Api.ApiService;
import dz.univbechar.sgrelaboratory.Api.RetrofitBuilder;
import dz.univbechar.sgrelaboratory.Model.CartModel;
import dz.univbechar.sgrelaboratory.Model.CartAction;
import dz.univbechar.sgrelaboratory.Model.CategoryModel;
import dz.univbechar.sgrelaboratory.Model.CategoryRep;
import dz.univbechar.sgrelaboratory.Model.OrderModel;
import dz.univbechar.sgrelaboratory.Model.ProductSelect;
import dz.univbechar.sgrelaboratory.Model.OrderRep;
import dz.univbechar.sgrelaboratory.Model.ProductModel;
import dz.univbechar.sgrelaboratory.Model.ProductRep;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProduct, recyclerViewCategory, recyclerViewCart;
    private CategoryModel[] dataCategory = new CategoryModel[5];
    private ProductModel[] dataProduct = new ProductModel[10];
    private ApiService service;
    private AwesomeValidation validator;
    private TokenManager tokenManager;
    private Call<ProductRep> callProduct;
    private Call<CategoryRep> callCategory;
    private Call<OrderRep> callOrderRep;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private RelativeLayout reSearch, reCategory, reProduct, reCartView;
    private ProgressDialog progressDialog;
    private TextView countCartView;
    private CartAction cartAction;
    private ImageView shoppingAction, homeAction;
    private boolean isOpenSearch = false;
    private AlertDialog alertDialog;
    TextInputLayout nameTheme;
    TextInputLayout nameTeacher;
    TextInputLayout nameStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homeAction=(ImageView)findViewById(R.id.HomeAction);
        shoppingAction=(ImageView)findViewById(R.id.ShoppingAction);
        reSearch=(RelativeLayout)findViewById(R.id.Search);
        reCategory=(RelativeLayout)findViewById(R.id.Category);
        reProduct=(RelativeLayout)findViewById(R.id.Product);
        reCartView=(RelativeLayout)findViewById(R.id.CartView);
        countCartView=(TextView)findViewById(R.id.countCartView);
        //loadCategory
        recyclerViewCategory = (RecyclerView) findViewById(R.id.loadCategory);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        //loadProduct
        recyclerViewProduct = (RecyclerView) findViewById(R.id.loadProduct);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(this));
        loadingData();
        ButterKnife.bind(this);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        if(tokenManager.getCartList() != null){
            Gson gson = new Gson();
            cartAction = gson.fromJson(tokenManager.getCartList(), CartAction.class);
            countCartView.setText(Integer.toString(cartAction.getCartModel().size()));
            countCartView.setVisibility(View.VISIBLE);
        }
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                loadCategoryList();
                loadProductList();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 1000);
            }
        }, 2500);
    }
    private void loadingData(){
        LoadingCategoryAdapter loadingCategoryAdapter = new LoadingCategoryAdapter(dataCategory);
        recyclerViewCategory.setAdapter(loadingCategoryAdapter);
        LoadingProductAdapter loadingProductAdapter = new LoadingProductAdapter(dataProduct);
        recyclerViewProduct.setAdapter(loadingProductAdapter);
        progressDialog("Wait for getting data ...", true);
    }
    private void loadProductList(){
        callProduct = service.products();
        callProduct.enqueue(new Callback<ProductRep>(){
            @Override
            public void onResponse(Call<ProductRep> call, Response<ProductRep> response) {
                if(response.isSuccessful()) {
                    productAdapter = new ProductAdapter(HomeActivity.this, response.body(), tokenManager, countCartView);
                    runAnimationProduct(recyclerViewProduct, productAdapter, 2);
                }else{
                    Toast.makeText(HomeActivity.this, "Success but not exits data", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ProductRep> call, Throwable t) {
                Toast.makeText(HomeActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadCategoryList(){
        callCategory = service.categories();
        callCategory.enqueue(new Callback<CategoryRep>(){
            @Override
            public void onResponse(Call<CategoryRep> call, Response<CategoryRep> response) {
                if(response.isSuccessful()) {
                    categoryAdapter = new CategoryAdapter(HomeActivity.this, response.body());
                    runAnimationCategory(recyclerViewCategory, categoryAdapter, 2);
                }else{
                    Toast.makeText(HomeActivity.this, "Success but not exits data", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<CategoryRep> call, Throwable t) {
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
    private void progressDialog(String text, boolean isLoading){
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        TextView textLoad = (TextView) progressDialog.getWindow().findViewById(R.id.textLoad);
        textLoad.setText(text);
        if(!isLoading) {
            ProgressBar progressBar = (ProgressBar) progressDialog.getWindow().findViewById(R.id.progressLoading);
            progressBar.setVisibility(View.GONE);
            ImageView imageView = (ImageView) progressDialog.getWindow().findViewById(R.id.checkLoading);
            imageView.setVisibility(View.VISIBLE);
        }
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
    }
    @OnClick(R.id.SearchAction)
    void SearchAction(){
        if(isOpenSearch){
            reSearch.setVisibility(View.GONE);
            isOpenSearch = false;
        }else{
            reSearch.setVisibility(View.VISIBLE);
            isOpenSearch = true;
        }
    }
    @OnClick(R.id.LogoutAction)
    void LogoutAction(){
        tokenManager.deleteToken();
        tokenManager.deleteCart();
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
        finish();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.ShoppingAction)
    void ShoppingAction(){
        if(tokenManager.getCartList() != null){
            shoppingAction.getDrawable().setTint(getResources().getColor(R.color.colorSetAction));
            homeAction.getDrawable().setTint(getResources().getColor(R.color.colorAccent));
            progressDialog("Go to view cart ...", true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerViewCart=(RecyclerView)findViewById(R.id.loadCart);
                    recyclerViewCart.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                    Gson gson = new Gson();
                    cartAction = gson.fromJson(tokenManager.getCartList(), CartAction.class);
                    CartAdapter cartAdapter = new CartAdapter(HomeActivity.this, cartAction, tokenManager, countCartView);
                    recyclerViewCart.setAdapter(cartAdapter);
                    reCategory.setVisibility(View.GONE);
                    reProduct.setVisibility(View.GONE);
                    reCartView.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }, 2500);
        }else {
            Toast.makeText(HomeActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.HomeAction)
    void HomeAction(){
        home("Wait for getting data ...",true);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void home(String textLoading, boolean isLoading){
        shoppingAction.getDrawable().setTint(getResources().getColor(R.color.colorAccent));
        homeAction.getDrawable().setTint(getResources().getColor(R.color.colorSetAction));
        if(tokenManager.getCartList() == null){
            countCartView.setVisibility(View.GONE);
        }
        loadProductList();
        loadCategoryList();
        progressDialog(textLoading, isLoading);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                reCartView.setVisibility(View.GONE);
                reCategory.setVisibility(View.VISIBLE);
                reProduct.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }
        }, 2500);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.deleteAllCart)
    void deleteAllCart(){
        tokenManager.deleteCart();
        home("Wait for deleting cart ...",true);
    }
    @OnClick(R.id.continueCart)
    void continueCart() {
        showFormDialog();
    }
    private void confirm(){
        String name_theme = nameTheme.getEditText().getText().toString().trim();
        String name_teacher = nameTeacher.getEditText().getText().toString().trim();
        String name_student = nameStudent.getEditText().getText().toString().trim();
        Gson gson = new Gson();
        cartAction = gson.fromJson(tokenManager.getCartList(), CartAction.class);
        OrderModel orderModel = new OrderModel();
        orderModel.setNameTheme(name_theme);
        orderModel.setNameTeachers(name_teacher);
        orderModel.setNameStudents(name_student);
        List<ProductSelect> dataOrders = new ArrayList<>();
        for (CartModel cModel: cartAction.getCartModel()){
            ProductSelect productSelect = new ProductSelect();
            productSelect.setId(cModel.getProductModel().getIdProduct());
            productSelect.setCount(cModel.getCount());
            dataOrders.add(productSelect);
        }
        orderModel.setData(dataOrders);
        callOrderRep = service.storeOrder(orderModel);
        callOrderRep.enqueue(new Callback<OrderRep>(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(Call<OrderRep> call, Response<OrderRep> response) {
                if(response.isSuccessful()){
                    tokenManager.deleteCart();
                    home("Project created successfully\nWait for go to home ...",false);
                    alertDialog.dismiss();
                }else{
                    if (response.code() == 422) {
                        handleErrors(response.errorBody());
                    }
                    if (response.code() == 401) {
                        ApiError apiError = Utils.converErrors(response.errorBody());
                        Toast.makeText(HomeActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<OrderRep> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Error: "+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void formClose(){
        nameTheme.getEditText().setText("");
        nameTeacher.getEditText().setText("");
        nameStudent.getEditText().setText("");
        alertDialog.dismiss();
    }
    private void showFormDialog(){
        AlertDialog.Builder alert;
        if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.LOLLIPOP){
            alert = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        }else{
            alert = new AlertDialog.Builder(this);
        }
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.form_layout, null);
        nameTheme = view.findViewById(R.id.nameTheme);
        nameTeacher = view.findViewById(R.id.nameTeacher);
        nameStudent = view.findViewById(R.id.nameStudent);
        Button formConfirm = view.findViewById(R.id.confirm);
        Button formClose = view.findViewById(R.id.formClose);
        setupRules();
        alert.setView(view);
        alert.setCancelable(false);
        formConfirm.setOnClickListener((v) ->{
            confirm();
        });
        formClose.setOnClickListener((v) ->{
            formClose();
        });
        alertDialog = alert.create();
        alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogScale;
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        alertDialog.getWindow().setBackgroundDrawable(inset);
        alertDialog.show();
    }
    @OnClick(R.id.AccountAction)
    void AccountAction(){
        //alertError("Login Invalid", "1. Login Invalid\n2. Password");
    }
    private void alertError(String title, String message){
        AlertDialog.Builder alert;
        if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.LOLLIPOP){
            alert = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        }else{
            alert = new AlertDialog.Builder(this);
        }
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.error_layout, null);
        TextView errorTitle= (TextView) view.findViewById(R.id.errorTitle);
        TextView errorMessage= (TextView) view.findViewById(R.id.errorMessage);
        errorTitle.setText(title);
        errorMessage.setText(message);
        Button btnTryAgain = (Button) view.findViewById(R.id.btnTryAgain);
        alert.setView(view);
        alert.setCancelable(false);
        alertDialog = alert.create();
        btnTryAgain.setOnClickListener((v) -> {
            alertDialog.dismiss();
        });
        alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogScale;
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        alertDialog.getWindow().setBackgroundDrawable(inset);
        alertDialog.show();
    }
    private void handleErrors(ResponseBody response) {
        ApiError apiError = Utils.converErrors(response);
        String errMessage = "";
        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
            if (error.getKey().equals("name_theme")) {
                errMessage += "1. "+error.getValue().get(0)+"\n";
            }
            if (error.getKey().equals("name_teachers")) {
                errMessage += "2. "+error.getValue().get(0)+"\n";
            }
            if (error.getKey().equals("name_students")) {
                errMessage += "3. "+error.getValue().get(0)+"\n";
            }
        }
        alertError("Invalid Login", errMessage);
    }
    private void setupRules(){
        validator.addValidation(this, nameTheme.getRootView().getId(), RegexTemplate.NOT_EMPTY, R.string.err_theme);
        validator.addValidation(this, nameTheme.getRootView().getId(), RegexTemplate.NOT_EMPTY, R.string.err_teacher);
        validator.addValidation(this, nameTheme.getRootView().getId(), RegexTemplate.NOT_EMPTY, R.string.err_student);
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
        if(callOrderRep != null){
            callOrderRep.cancel();
            callOrderRep = null;
        }
    }
}
