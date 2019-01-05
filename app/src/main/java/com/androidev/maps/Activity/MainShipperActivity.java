package com.androidev.maps.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.androidev.maps.Adapter.AdapterMainShipper;
import com.androidev.maps.ApiHelper.ApiCaller;
import com.androidev.maps.Fragment.FragmentConfirmed;
import com.androidev.maps.Fragment.FragmentOrderDetail;
import com.androidev.maps.Fragment.FragmentUpdateUserInfo;
import com.androidev.maps.Fragment.FragmentUserInfo;
import com.androidev.maps.Model.OrderMainShipper;
import com.androidev.maps.R;
import com.androidev.maps.Request.UpdateOrderRequest;
import com.androidev.maps.Response.ListOrderRespone;

import com.androidev.maps.Response.MessageRespone;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.androidev.maps.Fragment.FragmentConfirmed.MyPREFERENCES;

public class MainShipperActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private RecyclerView rcvOrder;
    private NavigationView navigationView;
    private android.support.v7.widget.Toolbar toolbar;
    private Button buttonShowOrderInfo;
    private String username;
    private String avatar;
    private String name;
    private CircleImageView imageViewAvatar;
    private TextView textViewUsername;
    public static AdapterMainShipper adapter;
    //Data
    public static ArrayList<OrderMainShipper> listOrder;
    public static int positionChose=-1;
    private SharedPreferences sharedPreferences;
    private String ErrorResponse;
    private int shipperID;
    private String orderID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mainshipper);
        Mapping();
        getDataFromReferences();
        setInfo();
        setActionBarInfo();
        setShowOrderInfoClick();
        handleNavigationEvent();
    }


    private void handleNavigationEvent() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //Set item as selected to persit hightlight
                menuItem.setChecked(true);
                //close drawer wthen item is tapped
                mDrawer.closeDrawers();
                //add fragment
                String title=menuItem.getTitle().toString();
                if (title.equals("My profile")){
                    Fragment fragmentUserInfo=new FragmentUserInfo();
                    FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.add(R.id.drawer_layout,fragmentUserInfo).addToBackStack("Activity main shipper").commit();
                } else if(title.equals("Information Update")){
                    Fragment fragmentUpdateUserInfo=new FragmentUpdateUserInfo();
                    FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.add(R.id.drawer_layout,fragmentUpdateUserInfo).addToBackStack("Activity main shipper").commit();
                } else if(title.equals("Setting")){

                } else if(title.equals("Log out")){
                    Intent intent=new Intent(MainShipperActivity.this,ActivityLogin.class);
                    startActivity(intent);
                }

                return true;
            }
        });
    }

    private void setActionBarInfo() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Danh sách đơn hàng");
        toolbar.setTitle("Danh sách đơn hàng");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_icon);
    }

    private void setInfo() {
        //Set text and image
        textViewUsername.setText(name);
        Glide.with(MainShipperActivity.this).load(avatar).into(imageViewAvatar);
        //Show list order
        ShowListOrder();
    }

    private void getDataFromReferences() {
        sharedPreferences=getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
        FragmentConfirmed.confirmed=sharedPreferences.getBoolean("Confirmed",false);
        name=sharedPreferences.getString("Full name","Cười cl");
        shipperID=sharedPreferences.getInt("Shipper id",4);
        username=sharedPreferences.getString("username","Cười cl");
        avatar=sharedPreferences.getString("avatar","https://firebasestorage.googleapis.com/v0/b/flashfood-ce894.appspot.com/o/profile_images%2Favatar.jpg?alt=media&token=cf8ec159-e88b-4faf-95e7-0120ac9eef1a&fbclid=IwAR3-k1DGBKytFntGCR7BE9JOaIkistzvLpAds9X-gIHifRQhBNXWxXxnVXk");
    }

    private void setShowOrderInfoClick() {
        buttonShowOrderInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FragmentConfirmed.confirmed!=true){
                    Toast.makeText(MainShipperActivity.this,"Bạn chưa nhận order nào",Toast.LENGTH_LONG).show();
                } else {
                    Fragment fragmentConfirmed=new FragmentConfirmed();

                    FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.add(R.id.drawer_layout,fragmentConfirmed).addToBackStack("Main activity").commit();
                }
            }
        });
    }

    private void setEventOrderList(AdapterMainShipper adapter) {
        adapter.setOnItemClickListener(new AdapterMainShipper.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (FragmentConfirmed.confirmed==true){
                    Toast.makeText(MainShipperActivity.this,"Bạn chưa giao xong đơn hàng trước",Toast.LENGTH_LONG).show();
                } else {
                    Fragment fragmentOrderDetail = new FragmentOrderDetail();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putInt("Order id", listOrder.get(position).getOrder_id());
                    bundle.putString("Store name", listOrder.get(position).getStore_name());
                    bundle.putString("Customer name", listOrder.get(position).getCustomer_name());
                    bundle.putString("Store address", listOrder.get(position).getFrom());
                    bundle.putString("Customer address", listOrder.get(position).getTo());
                    bundle.putInt("Shipper id",shipperID);
                    bundle.putInt("Order position",position);

                    fragmentOrderDetail.setArguments(bundle);
                    fragmentTransaction.add(R.id.drawer_layout, fragmentOrderDetail).addToBackStack("Main activity").commit();

                }
            }
        });
    }

    private void sendUpdateToServer(Fragment fragmentConfirmed, Integer order_id, int shipper_id,int position) {
        ApiCaller caller=new ApiCaller(MainShipperActivity.this);
        UpdateOrderRequest updateOrderRequest=new UpdateOrderRequest(order_id,shipper_id);
        caller.put(getString(R.string.API_URL)+"/pick_order", new HashMap<String, String>(), updateOrderRequest, new ApiCaller.OnSuccess() {
            @Override
            public void onSucess(String response) {
                FragmentConfirmed.confirmed = true;
                MainShipperActivity.listOrder.remove(position);
                MainShipperActivity.adapter.notifyDataSetChanged();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.drawer_layout, fragmentConfirmed).addToBackStack("Main activity").commit();
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
                    Toast.makeText(MainShipperActivity.this,messageRespone.getMessage(),Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e) {
                    // exception
                }
            }
        });
    }

    private void ShowListOrder() {

        ApiCaller caller=new ApiCaller(MainShipperActivity.this);
        Map<String,String> header=new HashMap<>();
        header.put("Content-Type", "application/json");
        caller.get(getString(R.string.API_URL)+"/order/all", new HashMap<String, String>(), new ApiCaller.OnSuccess() {
            @Override
            public void onSucess(String response) {

                convertJsonToObject(response.toString());
                adapter=new AdapterMainShipper(listOrder);
                rcvOrder.setLayoutManager(new LinearLayoutManager(MainShipperActivity.this));
                rcvOrder.setAdapter(adapter);
                setEventOrderList(adapter);
                setOnConfirmButtonClick(adapter);
            }
        }, new ApiCaller.OnError() {
            @Override
            public void onError(VolleyError error) {
                if (error == null || error.networkResponse == null) {
                    return;
                }

                String body;
                //get status code here
                final String statusCode = String.valueOf(error.networkResponse.statusCode);
                //get response body and parse with appropriate encoding
                try {
                    body = new String(error.networkResponse.data,"UTF-8");
                    Gson gson=new Gson();
                    MessageRespone messageRespone=gson.fromJson(body,MessageRespone.class);
                    Toast.makeText(MainShipperActivity.this,messageRespone.getMessage(),Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e) {
                    // exception
                }
            }
        });

    }

    private void setOnConfirmButtonClick(AdapterMainShipper adapter) {
        adapter.setConfirmClickListener(new AdapterMainShipper.OnConfirmClickListener() {
            @Override
            public void onConfirmClick(int position) {
                if (FragmentConfirmed.confirmed==true){
                    Toast.makeText(MainShipperActivity.this,"Bạn chưa giao xong đơn hàng trước",Toast.LENGTH_LONG).show();
                } else {
                    Fragment fragmentConfirmed = new FragmentConfirmed();
                    Bundle bundle = new Bundle();
                    bundle.putInt("Order id", listOrder.get(position).getOrder_id());
                    bundle.putString("Store name", listOrder.get(position).getStore_name());
                    bundle.putString("Customer name", listOrder.get(position).getCustomer_name());
                    bundle.putString("Store address", listOrder.get(position).getFrom());
                    bundle.putString("Customer address", listOrder.get(position).getTo());
                    bundle.putInt("Position",position);
                    bundle.putInt("Shipper id",shipperID);
                    SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).edit();
                    editor.putString("Store name", listOrder.get(position).getStore_name());
                    editor.putString("Store address", listOrder.get(position).getFrom());
                    editor.putString("Customer name", listOrder.get(position).getCustomer_name());
                    editor.putString("Customer address", listOrder.get(position).getTo());
                    editor.putInt("Order id", listOrder.get(position).getOrder_id());
                    editor.putBoolean("Confirmed", FragmentConfirmed.confirmed);
                    editor.putInt("Position",position);
                    editor.commit();
                    fragmentConfirmed.setArguments(bundle);

                    sendUpdateToServer(fragmentConfirmed,listOrder.get(position).getOrder_id(), shipperID,position);

                }
            }
        });
    }

    private void convertJsonToObject(String jsonResponse) {
        ListOrderRespone listOrderRespone=new ListOrderRespone();
        Gson gson=new Gson();
        listOrderRespone=gson.fromJson(jsonResponse,ListOrderRespone.class);
        listOrder=listOrderRespone.getFree_orders();
    }

    private void Mapping() {
        mDrawer=(DrawerLayout)findViewById(R.id.drawer_layout);
        nvDrawer=(NavigationView)findViewById(R.id.nvView);
        navigationView=(NavigationView)findViewById(R.id.nvView);
        toolbar= findViewById(R.id.toolbar);
        rcvOrder=(RecyclerView) findViewById(R.id.rcv_main_shipper);
        buttonShowOrderInfo=(Button)findViewById(R.id.btn_show_order_info_main);

        View headerView=navigationView.getHeaderView(0);
        imageViewAvatar=(CircleImageView)headerView.findViewById(R.id.user_avartar_navigation_header);
        textViewUsername=(TextView)headerView.findViewById(R.id.txt_username_navigation_header);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
    private void refreshListOrder() {
        ApiCaller caller=new ApiCaller(MainShipperActivity.this);
        Map<String,String> header=new HashMap<>();
        header.put("Content-Type", "application/json");
        caller.get(getString(R.string.API_URL)+"/order/all", new HashMap<String, String>(), new ApiCaller.OnSuccess() {
            @Override
            public void onSucess(String response) {

                ListOrderRespone listOrderRespone=new ListOrderRespone();
                Gson gson=new Gson();
                listOrderRespone=gson.fromJson(response,ListOrderRespone.class);
                MainShipperActivity.listOrder=listOrderRespone.getFree_orders();
                MainShipperActivity.adapter.notifyDataSetChanged();
                /*for (int i=0;i<MainShipperActivity.listOrder.size();i++){
                    Toast.makeText(getContext(),MainShipperActivity.listOrder.get(i).getOrder_id()+"",Toast.LENGTH_SHORT).show();
                }*/

            }
        }, new ApiCaller.OnError() {
            @Override
            public void onError(VolleyError error) {
                if (error == null || error.networkResponse == null) {
                    return;
                }

                String body;
                //get status code here
                final String statusCode = String.valueOf(error.networkResponse.statusCode);
                //get response body and parse with appropriate encoding
                try {
                    body = new String(error.networkResponse.data,"UTF-8");
                    Gson gson=new Gson();
                    MessageRespone messageRespone=gson.fromJson(body,MessageRespone.class);
                    Toast.makeText(MainShipperActivity.this,messageRespone.getMessage(),Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e) {
                    // exception
                }
            }
        });
    }

}
