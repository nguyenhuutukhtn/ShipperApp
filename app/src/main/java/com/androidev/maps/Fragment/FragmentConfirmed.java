package com.androidev.maps.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.androidev.maps.Adapter.AdapterOrderDetail;
import com.androidev.maps.ApiHelper.ApiCaller;
import com.androidev.maps.Activity.MainShipperActivity;
import com.androidev.maps.Model.ListOrderDetailRespone;
import com.androidev.maps.Model.OrderInfoResponse;
import com.androidev.maps.Model.StoreInfoRespose;
import com.androidev.maps.R;
import com.androidev.maps.Request.ConfirmOrderRequest;
import com.androidev.maps.Request.RejectOrderRequest;
import com.androidev.maps.Response.MessageRespone;
import com.google.gson.Gson;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class FragmentConfirmed extends Fragment {
    private ImageButton buttonBack;
    private TextView textViewHeader;
    private TextView textViewDuration,textViewDistance,textViewTotal,textViewShipFee;
    private TextView textViewStoreName,textViewStoreAddress,textViewStorePhoneNumber;
    private TextView textViewCustomerName,textViewCustomerAddress,textViewCustomerPhoneNumber;
    private ImageView imageViewHeader;
    private RecyclerView rcvOrderDetail;
    private Button buttonComplete,buttonCancel;

    //internal data;
    private String storeAddress,customerAddress;
    private String storeName,customerName;
    private String storePhoneNumber,customerPhoneNumber;
    private String total,shipFee;
    private int orderID,shipperID;
    private double distance,duration;
    public static Point originPoint,destinationPoint;
    private DirectionsRoute currentRoute;
    private OrderInfoResponse orderInfoResponse;
    private StoreInfoRespose storeInfoRespose;

    private String JsonResponse;
    private String JsonListOrderInSharedPreferences;
    public static ListOrderDetailRespone listOrderDetailRespone;
    private SharedPreferences sharedPreferences;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static boolean confirmed;
    private String ErrorResponse;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_confirm,container,false);
        Mapping(view);
        getDataFromPreviousView(view);
        getDataFromSharePreferences(view);
        try {
            setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //showListOrderDetail(view);
        setEventClick(view);
        return view;
    }

    private void getDataFromSharePreferences(View view) {
        sharedPreferences=getActivity().getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
        JsonListOrderInSharedPreferences=sharedPreferences.getString("List order detail fragment confirmed",null);
        if (JsonListOrderInSharedPreferences==null)
            showListOrderDetail(view);
        else {
            listOrderDetailRespone.getOrder_details().clear();
            Gson gson = new Gson();
            listOrderDetailRespone = gson.fromJson(JsonListOrderInSharedPreferences,ListOrderDetailRespone.class);
            AdapterOrderDetail adapter=new AdapterOrderDetail(listOrderDetailRespone.getOrder_details());
            rcvOrderDetail.setLayoutManager(new LinearLayoutManager(getContext()));
            rcvOrderDetail.setAdapter(adapter);
        }
    }

    private void showListOrderDetail(View view) {



        ApiCaller caller=new ApiCaller(getContext());
        Map<String,String> headers=new HashMap<String,String>();
        headers.put("order_id",orderID+"");
        caller.get(getResources().getString(R.string.API_URL)+"/order_detail/order_id", headers, new ApiCaller.OnSuccess() {
            @Override
            public void onSucess(String response) {

                JsonResponse = response.toString();
                Gson gson = new Gson();
                if (JsonResponse != null) {
                    SharedPreferences.Editor editor=getActivity().getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE).edit();
                    editor.putString("List order detail fragment confirmed",JsonResponse);
                    listOrderDetailRespone = gson.fromJson(JsonResponse,ListOrderDetailRespone.class);
                    AdapterOrderDetail adapter=new AdapterOrderDetail(listOrderDetailRespone.getOrder_details());
                    rcvOrderDetail.setLayoutManager(new LinearLayoutManager(getContext()));
                    rcvOrderDetail.setAdapter(adapter);
                }
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
                    Toast.makeText(getContext(),messageRespone.getMessage(),Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e) {
                    // exception
                }
            }
        });
    }

    private void getDataFromPreviousView(View view) {
        sharedPreferences=getActivity().getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
        storeName=sharedPreferences.getString("Store name",null);
        storeAddress=sharedPreferences.getString("Store address",null);
        customerName=sharedPreferences.getString("Customer name",null);
        customerAddress=sharedPreferences.getString("Customer address",null);
        orderID=sharedPreferences.getInt("Order id",-1);

        if (storeName==null&&getArguments()!=null) {
            storeName=getArguments().get("Store name").toString();
        }
        if (storeAddress==null&&getArguments()!=null){
            storeAddress=getArguments().get("Store address").toString();
        }
        if (customerName==null&&getArguments()!=null){
            customerName=getArguments().get("Customer address").toString();
        }
        if (customerAddress==null&&getArguments()!=null){
            customerAddress=getArguments().get("Customer address").toString();
        }
        if (orderID==-1&&getArguments()!=null){
            orderID=getArguments().getInt("Order id");
        }

       /* storeName=getArguments().get("Store name").toString();
        storeAddress=getArguments().get("Store address").toString();
        customerName=getArguments().get("Customer address").toString();
        customerAddress=getArguments().get("Customer address").toString();*/
    }

    private void setEventClick(View view) {
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=getActivity().getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE).edit();
                editor.putString("Store name",storeName);
                editor.putString("Store address",storeAddress);
                editor.putString("Customer name",customerName);
                editor.putString("Customer address",customerAddress);
                editor.putInt("Order id",orderID);
                editor.commit();
                getFragmentManager().popBackStack("Main activity",FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        imageViewHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragmentMap=new FragmentMap();
                FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.container_fragment_confirmed,fragmentMap).commit();
            }
        });
        buttonComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiCaller caller=new ApiCaller(getContext());
                ConfirmOrderRequest confirmOrderRequest=new ConfirmOrderRequest(orderID,getArguments().getInt("Shipper id"));
                //Toast.makeText(getContext(),orderID+"",Toast.LENGTH_LONG).show();
                caller.post(getResources().getString(R.string.API_URL)+"/confirm_order", new HashMap<String, String>(), confirmOrderRequest, new ApiCaller.OnSuccess() {
                    @Override
                    public void onSucess(String response) {
                        //Toast.makeText(getContext(), response.toString(), Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor=getActivity().getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE).edit();
                        editor.remove("Store name");
                        editor.remove("Store address");
                        editor.remove("Customer name");
                        editor.remove("Customer address");
                        editor.remove("Order id");
                        editor.remove("Confirmed");
                        editor.commit();
                        FragmentConfirmed.confirmed=false;
                        sharedPreferences=getActivity().getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
                        MainShipperActivity.listOrder.remove(sharedPreferences.getInt("Position",0));
                        MainShipperActivity.adapter.notifyDataSetChanged();
                        getFragmentManager().popBackStack("Main activity",FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
                            Toast.makeText(getContext(),messageRespone.getMessage(),Toast.LENGTH_LONG).show();
                        } catch (UnsupportedEncodingException e) {
                            // exception
                        }
                    }
                });
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiCaller caller=new ApiCaller(getContext());
                RejectOrderRequest rejectOrderRequest=new RejectOrderRequest(orderID,getArguments().getInt("Shipper id"));
                caller.put(getResources().getString(R.string.API_URL)+"/reject_order", new HashMap<String, String>(), rejectOrderRequest, new ApiCaller.OnSuccess() {
                    @Override
                    public void onSucess(String response) {
                        //Toast.makeText(getContext(), response.toString(), Toast.LENGTH_LONG).show();
                        getFragmentManager().popBackStack("Main activity",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        SharedPreferences.Editor editor=getActivity().getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE).edit();
                        editor.remove("Store name");
                        editor.remove("Store address");
                        editor.remove("Customer name");
                        editor.remove("Customer address");
                        editor.remove("Order id");
                        editor.remove("Confirmed");
                        FragmentConfirmed.confirmed=false;
                        editor.commit();
                        getFragmentManager().popBackStack("Main activity",FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
                            Toast.makeText(getContext(),messageRespone.getMessage(),Toast.LENGTH_LONG).show();
                        } catch (UnsupportedEncodingException e) {
                            // exception
                        }
                    }
                });
            }
        });
    }

    private void setContent(View view) throws IOException {
        textViewHeader.setText("Thông tin giao hàng");
        textViewStoreName.setText(storeName);
        textViewStoreAddress.setText(storeAddress);
        textViewCustomerName.setText(customerName);
        textViewCustomerAddress.setText(customerAddress);
        imageViewHeader.setImageResource(R.drawable.split_call_arrows);

        ApiCaller caller=new ApiCaller(getContext());
        Map<String,String> headers=new HashMap<String,String>();
        headers.put("order_id",orderID+"");
        caller.get(getResources().getString(R.string.API_URL)+"/order", headers, new ApiCaller.OnSuccess() {
            @Override
            public void onSucess(String response) {

                Gson gson = new Gson();
                orderInfoResponse = gson.fromJson(response.toString(), OrderInfoResponse.class);
                textViewTotal.setText("Tổng tiền: "+orderInfoResponse.getSum_price().toString()+"đ");
                textViewCustomerPhoneNumber.setText(orderInfoResponse.getPhone().toString());
                setStorePhoneNumber(view,orderInfoResponse.getStore_id());

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
                    Toast.makeText(getContext(),messageRespone.getMessage(),Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e) {
                    // exception
                }
            }
        });
        setDistanceAndDuration(view);
    }

    private void setStorePhoneNumber(View view, int store_id) {
        ApiCaller caller=new ApiCaller(getContext());
        Map<String,String> headers=new HashMap<String,String>();
        headers.put("store_id",store_id+"");
        caller.get(getResources().getString(R.string.API_URL)+"/store", headers, new ApiCaller.OnSuccess() {
            @Override
            public void onSucess(String response) {
                Gson gson = new Gson();
                storeInfoRespose = gson.fromJson(response.toString(), StoreInfoRespose.class);
                textViewStorePhoneNumber.setText(storeInfoRespose.getPhone().toString());

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
                    Toast.makeText(getContext(),messageRespone.getMessage(),Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e) {
                    // exception
                }


            }
        });
    }

    private void setDistanceAndDuration(View view) throws IOException {
        originPoint=getPointFromAddress(getContext(),storeAddress);
        destinationPoint=getPointFromAddress(getContext(),customerAddress);

        com.mapbox.api.directions.v5.MapboxDirections client;

        client= com.mapbox.api.directions.v5.MapboxDirections.builder()
                .origin(originPoint)
                .destination(destinationPoint)
                .overview(com.mapbox.api.directions.v5.DirectionsCriteria.OVERVIEW_FULL)
                .profile(com.mapbox.api.directions.v5.DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getContext().getResources().getString(R.string.access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                System.out.println(call.request().url().toString());

                // You can get the generic HTTP info about the response
                Timber.d("Response code: " + response.code());
                if (response.body() == null) {
                    Timber.e("No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Timber.e("No routes found");
                    return;
                }

                // Get the directions route
                currentRoute = response.body().routes().get(0);
                distance=currentRoute.distance();
                duration=currentRoute.duration();
                textViewDistance.setText(new DecimalFormat("#.#").format(distance/1000)+ "km");
                setTextViewDuration(view);
                textViewShipFee.setText("$ Phí ship:"+(Math.round(distance*6))+"đ");
            }
            private void setTextViewDuration(View view) {
                if (duration<=60&&duration>0){
                    textViewDuration.setText("1 phút");
                } else {
                    textViewDuration.setText(new DecimalFormat("#.#").format(duration/60)+"phút");
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public com.mapbox.geojson.Point getPointFromAddress(Context context, String strAddress) throws IOException {
        Geocoder coder = new Geocoder(context);
        List<Address> address= coder.getFromLocationName(strAddress, 5);
        LatLng p1 = null;
        com.mapbox.geojson.Point point = null;

        if (address.size()>0) {
            Address location = address.get(0);
            point= com.mapbox.geojson.Point.fromLngLat(location.getLongitude(),location.getLatitude());
        }
        return point;
    }

    private void Mapping(View view) {
        buttonBack=(ImageButton)view.findViewById(R.id.buttonBack_header);
        textViewHeader=(TextView)view.findViewById(R.id.header_content);
        textViewDuration=(TextView)view.findViewById(R.id.txt_duration_fragment_confirmed);
        textViewDistance=(TextView)view.findViewById(R.id.txt_distance_fragment_confirmed);
        textViewShipFee=(TextView)view.findViewById(R.id.txt_ship_fee_fragment_confirmed);
        textViewTotal=(TextView)view.findViewById(R.id.txt_total_fragment_confirmed);
        textViewStoreName=(TextView)view.findViewById(R.id.txt_store_name_fragment_confirm);
        textViewStoreAddress=(TextView)view.findViewById(R.id.txt_store_address_fragment_confirm);
        textViewStorePhoneNumber=(TextView)view.findViewById(R.id.txt_store_phone_number_fragment_confirmed);
        textViewCustomerName=(TextView)view.findViewById(R.id.txt_customer_name_fragment_confirm);
        textViewCustomerAddress=(TextView)view.findViewById(R.id.txt_customer_address_fragment_confirm);
        textViewCustomerPhoneNumber=(TextView)view.findViewById(R.id.txt_customer_phone_number_fragment_confirm);
        rcvOrderDetail=view.findViewById(R.id.rcv_order_detail_confirmed);
        imageViewHeader=view.findViewById(R.id.header_imgView);
        buttonComplete=view.findViewById(R.id.btn_complete_fragment_confirmed);
        buttonCancel=view.findViewById(R.id.btn_cancel_fragment_confirm);
    }
}
