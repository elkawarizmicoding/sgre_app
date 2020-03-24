package com.khwarizmicoding.sgrelaboratory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.CheckBox;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.khwarizmicoding.sgrelaboratory.Api.AccessToken;
import com.khwarizmicoding.sgrelaboratory.Api.ApiError;
import com.khwarizmicoding.sgrelaboratory.Api.ApiService;
import com.khwarizmicoding.sgrelaboratory.Api.RetrofitBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.inEmail)
    TextInputLayout inEmail;
    @BindView(R.id.inPassword)
    TextInputLayout inPassword;

    private CheckBox checkBox;
    private ApiService service;
    private TokenManager tokenManager;
    private AwesomeValidation validator;
    private Call<AccessToken> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        setupRules();

        if(tokenManager.getToken().getAccessToken() != null){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
        // @wadiemendja
        //setting the ckeckBox enabled default
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setChecked(true);
    }
    @OnClick(R.id.forget)
    void goToForget() {
        Intent mainIntent = new Intent(LoginActivity.this, ForgotPassword.class);
        LoginActivity.this.startActivity(mainIntent);
    }
    @OnClick(R.id.login_btn)
    void login(){
        String email = inEmail.getEditText().getText().toString().trim();
        String password = inPassword.getEditText().getText().toString().trim();
        inEmail.setError(null);
        inPassword.setError(null);

        validator.clear();
        if (validator.validate()){
            call = service.login(email, password);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    if (response.isSuccessful()) {
                        tokenManager.saveToken(response.body());
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        if (response.code() == 422) {
                            handleErrors(response.errorBody());
                        }
                        if (response.code() == 401) {
                            ApiError apiError = Utils.converErrors(response.errorBody());
                            Toast.makeText(LoginActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "onFailure: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }
    private void handleErrors(ResponseBody response) {
        ApiError apiError = Utils.converErrors(response);
        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
            if (error.getKey().equals("email")) {
                inEmail.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("password")) {
                inPassword.setError(error.getValue().get(0));
            }
        }

    }
    public void setupRules() {
        validator.addValidation(this, R.id.inEmail, Patterns.EMAIL_ADDRESS, R.string.err_email);
        validator.addValidation(this, R.id.inPassword, RegexTemplate.NOT_EMPTY, R.string.err_password);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
    }
}
