package dz.univbechar.sgrelaboratory;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import dz.univbechar.sgrelaboratory.Api.AccessToken;
import dz.univbechar.sgrelaboratory.Api.ApiError;
import dz.univbechar.sgrelaboratory.Api.ApiService;
import dz.univbechar.sgrelaboratory.Api.RetrofitBuilder;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

public class MainActivity extends AppCompatActivity {

    private RelativeLayout reHeader, reBody, reError;
    @BindView(R.id.inEmail)
    TextInputLayout inEmail;
    @BindView(R.id.inPassword)
    TextInputLayout inPassword;
    private CheckBox checkBox;
    private ApiService service;
    private TokenManager tokenManager;
    private Call<AccessToken> call;
    private BroadcastReceiver wifiStateReceiver;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reHeader=(RelativeLayout)findViewById(R.id.Header);
        reBody=(RelativeLayout)findViewById(R.id.Body);
        reError=(RelativeLayout)findViewById(R.id.Error);
        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        wifiStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        switch (wifiStateExtra) {
                            case WifiManager.WIFI_STATE_ENABLED:
                                if(tokenManager.getToken().getAccessToken() != null){
                                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                    finish();
                                }else{
                                    reError.setVisibility(View.GONE);
                                    reHeader.setVisibility(View.VISIBLE);
                                    reBody.setVisibility(View.VISIBLE);
                                }
                                break;
                            case WifiManager.WIFI_STATE_DISABLED:
                                reError.setVisibility(View.VISIBLE);
                                reHeader.setVisibility(View.GONE);
                                reBody.setVisibility(View.GONE);
                                break;
                        }
                    }
                }, 2500);
            }
        };
    }
    @OnClick(R.id.forget)
    void forget() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                this, R.style.BottomSheetDialogTheme
        );
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.layout_card_forget, (LinearLayout)findViewById(R.id.layoutCardForget));
        TextInputLayout emailForget = (TextInputLayout) bottomSheetView.findViewById(R.id.inEmailForget);
        bottomSheetView.findViewById(R.id.submitEmail).setOnClickListener((v) -> {
            Toast.makeText(this, "Email is "+emailForget.getEditText().getText().toString().trim(), Toast.LENGTH_LONG).show();
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
    @OnClick(R.id.login_btn)
    void login(){
        String email = inEmail.getEditText().getText().toString().trim();
        String password = inPassword.getEditText().getText().toString().trim();
        call = service.login(email, password);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (response.isSuccessful()) {
                    tokenManager.saveToken(response.body());
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                } else {
                    if (response.code() == 422) {
                        handleErrors(response.errorBody());
                    }
                    if (response.code() == 401) {
                        ApiError apiError = Utils.converErrors(response.errorBody());
                        Toast.makeText(MainActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Toast.makeText(MainActivity.this, "onFailure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void handleErrors(ResponseBody response) {
        ApiError apiError = Utils.converErrors(response);
        String errMessage = "";
        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
            if (error.getKey().equals("email")) {
                errMessage += "1. "+error.getValue().get(0)+"\n";
            }
            if (error.getKey().equals("password")) {
                errMessage += "2. "+error.getValue().get(0)+"\n";
            }
        }
        alertError("Invalid Login", errMessage);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);
    }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifiStateReceiver);
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
