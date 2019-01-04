package com.androidev.maps.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.androidev.maps.Activity.MainShipperActivity;
import com.androidev.maps.ApiHelper.ApiCaller;
import com.androidev.maps.R;
import com.androidev.maps.Request.UpdateUserInfoRequest;
import com.androidev.maps.Response.MessageRespone;
import com.androidev.maps.Response.UpdateUserInfoResponse;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class FragmentUpdateUserInfo extends Fragment {
    public static final String TAG="Fragment user info";
    private ImageButton btnBack;
    private TextView txtHeader;
    private Button buttonSave;
    private ImageView imageViewAvatar;
    private TextView textViewEdit;
    private EditText editTextUserName,editTextFullName,editTextPhoneNumber;
    private String linkAvatar,userName,fullName,phoneNumber;
    private int shipperID;
    private int status;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_update_information,container,false);
        Mapping(view);
        getDataFromSharedPreferences(view);
        SetInfo();
        handleEvent(view);
        return view;
    }

    private void getDataFromSharedPreferences(View view) {
        sharedPreferences=getActivity().getSharedPreferences(FragmentConfirmed.MyPREFERENCES,Context.MODE_PRIVATE);
        linkAvatar=sharedPreferences.getString("avatar","https://firebasestorage.googleapis.com/v0/b/flashfood-ce894.appspot.com/o/profile_images%2Favatar.jpg?alt=media&token=cf8ec159-e88b-4faf-95e7-0120ac9eef1a&fbclid=IwAR3-k1DGBKytFntGCR7BE9JOaIkistzvLpAds9X-gIHifRQhBNXWxXxnVXk");
        userName=sharedPreferences.getString("username","tudz");
        fullName=sharedPreferences.getString("Full name","Nguyễn Hữu Tú");
        phoneNumber=sharedPreferences.getString("Phone number","0123456789");
        shipperID=sharedPreferences.getInt("Shipper id",3);

        //Default value
        status= sharedPreferences.getInt("status",0);
    }

    private void handleEvent(View view) {
        handleBackButtonClickEvent(view);
        handleUploadAvatarEvent(view);
        handleSaveButtonClickEvent(view);
    }

    private void handleUploadAvatarEvent(View view) {
        textViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void handleSaveButtonClickEvent(View view) {
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName=editTextUserName.getText().toString();
                fullName=editTextFullName.getText().toString();
                phoneNumber=editTextPhoneNumber.getText().toString();
                UpdateUserInfoRequest updateUserInfoRequest =new UpdateUserInfoRequest(shipperID,fullName,status,linkAvatar,userName,phoneNumber);
                ApiCaller caller=new ApiCaller(getContext());
                caller.put(getResources().getString(R.string.API_URL) + "/shipper", new HashMap<String, String>(), updateUserInfoRequest, new ApiCaller.OnSuccess() {
                    @Override
                    public void onSucess(String response) {
                        UpdateUserInfoResponse updateUserInfoResponse=new UpdateUserInfoResponse();
                        updateUserInfoResponse=new Gson().fromJson(response.toString(),UpdateUserInfoResponse.class);
                        saveResponseInfoToSharedPreferences(view,updateUserInfoResponse);
                        Toast.makeText(getContext(),"Cập nhật thông tin thành công",Toast.LENGTH_LONG).show();
                        getFragmentManager().popBackStack();
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
                            Toast.makeText(getContext(),messageRespone.getMessage(),Toast.LENGTH_LONG).show();
                        } catch (UnsupportedEncodingException e) {
                            // exception
                        }
                    }
                });
            }
        });
    }

    private void saveResponseInfoToSharedPreferences(View view, UpdateUserInfoResponse updateUserInfoResponse) {
        SharedPreferences.Editor editor=getActivity().getSharedPreferences(FragmentConfirmed.MyPREFERENCES,Context.MODE_PRIVATE).edit();
        editor.putInt("Shipper id",updateUserInfoResponse.getId());
        editor.putString("Full name",updateUserInfoResponse.getName());
        editor.putInt("status",updateUserInfoResponse.getStatus());
        editor.putString("avatar",updateUserInfoResponse.getAvatar());
        editor.putString("username",updateUserInfoResponse.getUsername());
        editor.putString("Phone number",updateUserInfoResponse.getPhone());
    }

    private void handleBackButtonClickEvent(View view) {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    private void SetInfo() {
        txtHeader.setText("Nguyen Huu Tu");
        Glide.with(getContext()).load(linkAvatar).into(imageViewAvatar);
        editTextUserName.setText(userName);
        editTextFullName.setText(fullName);
        editTextPhoneNumber.setText(phoneNumber);
    }

    private void Mapping(View view) {
        btnBack=(ImageButton)view.findViewById(R.id.buttonBack_fragment_user_update);
        txtHeader=(TextView)view.findViewById(R.id.txt_header_fragment_user_update);
        buttonSave=(Button)view.findViewById(R.id.btn_save_fragment_user_update);
        imageViewAvatar=(ImageView)view.findViewById(R.id.img_avatar_fragment_update_info);
        editTextUserName=(EditText)view.findViewById(R.id.edt_username_fragment_update_info);
        editTextFullName=(EditText)view.findViewById(R.id.edt_fullname_fragment_update_info);
        editTextPhoneNumber=(EditText)view.findViewById(R.id.edt_phone_number_fragment_update_info);
        textViewEdit=(TextView)view.findViewById(R.id.txt_edit_fragment_update_info);
    }
}
