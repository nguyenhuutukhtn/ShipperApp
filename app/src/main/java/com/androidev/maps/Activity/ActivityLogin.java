package com.androidev.maps.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.androidev.maps.ApiHelper.ApiCaller;
import com.androidev.maps.R;
import com.androidev.maps.Request.LoginRequest;
import com.androidev.maps.Response.MessageRespone;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class ActivityLogin extends AppCompatActivity {
    private EditText editTextEmail,editTextPassword;
    private Button buttonLogin;
    private String email,password;
    private SharedPreferences loginSharePreferences;
    public static final String LoginPREFERENCES="Login Prefs";
    private String ErrorResponse;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mapping();
        handleEvent();
    }

    private void getData() {
        email=editTextEmail.getText().toString();
        password=editTextPassword.getText().toString();
    }

    private void handleEvent() {
        handleButtonLoginClick();
    }

    private void handleButtonLoginClick() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                LoginRequest loginRequest=new LoginRequest(email,password);
                ApiCaller caller=new ApiCaller(ActivityLogin.this);
                caller.post(getString(R.string.API_URL) + "/sign_in", new HashMap<String, String>(), loginRequest, new ApiCaller.OnSuccess() {
                    @Override
                    public void onSucess(String response) {
                        Toast.makeText(ActivityLogin.this,response.toString(),Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(ActivityLogin.this,MainShipperActivity.class);
                        startActivity(intent);
                    }
                }, new ApiCaller.OnError() {
                    @Override
                    public void onError(VolleyError error) {
                        if (error == null || error.networkResponse == null) {
                            return;
                        }

                        String body=null;
                        //get status code here
                        final String statusCode = String.valueOf(error.networkResponse.statusCode);
                        //get response body and parse with appropriate encoding
                        try {
                            body = new String(error.networkResponse.data,"UTF-8");
                            Gson gson=new Gson();
                            MessageRespone messageRespone=gson.fromJson(body,MessageRespone.class);
                            Toast.makeText(ActivityLogin.this,messageRespone.getMessage(),Toast.LENGTH_LONG).show();
                        } catch (UnsupportedEncodingException e) {
                            // exception
                        }
                    }
                });
                saveToSharePreferences();
            }
        });
    }

    private void saveToSharePreferences() {
        SharedPreferences.Editor editor=getSharedPreferences(LoginPREFERENCES,MODE_PRIVATE).edit();
        editor.putString("username","Tú");
        editor.putInt("shipper id",2);
    }


    private void mapping() {
        editTextEmail=(EditText)findViewById(R.id.edt_email_input_activity_login);
        editTextPassword=(EditText)findViewById(R.id.edt_password_input_activity_login);
        buttonLogin=(Button)findViewById(R.id.btn_login_activity_login);
    }
}
