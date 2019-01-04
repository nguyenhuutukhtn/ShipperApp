package com.androidev.maps.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.androidev.maps.Activity.ActivityLogin;
import com.androidev.maps.R;

import static com.androidev.maps.Fragment.FragmentConfirmed.MyPREFERENCES;

public class FragmentUserInfo extends Fragment {
    private TextView textViewHeader;
    private TextView textViewUserName;
    private ImageButton buttonBack;
    private String username,phoneNumber,fullname;
    private SharedPreferences sharedPreferences;
    private EditText editTextPhone,editTextFullnName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_user_info,container,false);
        mapping(view);
        getData(view);
        setContent(view);
        handleEvent(view);
        return view;
    }

    private void getData(View view) {
        sharedPreferences=getActivity().getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
        username=sharedPreferences.getString("username","Chưa có");
        phoneNumber=sharedPreferences.getString("Phone number","012");
        fullname=sharedPreferences.getString("Full name","abc");
    }

    private void handleEvent(View view) {
        handleButtonBackClickEvent(view);
    }

    private void handleButtonBackClickEvent(View view) {
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    private void setContent(View view) {
        textViewHeader.setText("Thông tin tài khoản");
        textViewUserName.setText(username);
        editTextPhone.setText(phoneNumber);
        editTextFullnName.setText(fullname);
    }

    private void mapping(View view) {
        textViewHeader=(TextView)view.findViewById(R.id.header_content);
        buttonBack=(ImageButton)view.findViewById(R.id.buttonBack_header);
        textViewUserName=(TextView)view.findViewById(R.id.txt_username_fragment_user_info);
        editTextFullnName=view.findViewById(R.id.edt_fullname_fragment_user_info);
        editTextPhone=view.findViewById(R.id.edt_phone_fragment_user_info);
    }
}
