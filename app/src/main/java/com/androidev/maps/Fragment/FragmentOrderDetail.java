package com.androidev.maps.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.androidev.maps.Adapter.AdapterOrderDetail;
import com.androidev.maps.ApiHelper.ApiCaller;
import com.androidev.maps.Activity.MainShipperActivity;
import com.androidev.maps.Model.ListOrderDetailRespone;
import com.androidev.maps.Model.OrderInfoResponse;
import com.androidev.maps.R;
import com.androidev.maps.Request.UpdateOrderRequest;
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

import static com.androidev.maps.Fragment.FragmentConfirmed.MyPREFERENCES;

public class FragmentOrderDetail extends Fragment {
    private ImageButton btnBack;
    private TextView textViewHeader;
    private Button buttonConfirm;
    private TextView textViewDuration,textViewDistance,textViewTotal,textViewShipFee;
    private RecyclerView rcvOrderDetail;

    //internal data;
    private String storeAddress,customerAddress;
    private String total;
    private double distance,duration;
    private Point originPoint,destinationPoint;
    private DirectionsRoute currentRoute;
    private OrderInfoResponse orderInfoResponse;
    private String JsonResponse;
    private ListOrderDetailRespone listOrderDetailRespone;
    private String ErrorResponse;
    private int shipperID;
    private SharedPreferences sharedPreferences;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_order_detail,container,false);

        Mapping(view);
        getDataFromPreviousView(view);
        getDataFromSharedPreferences(view);
        try {
            setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
        showListOrderDetail(view);
        setEventClick(view);
        return view;
    }

    private void getDataFromSharedPreferences(View view) {
        sharedPreferences=getActivity().getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
    }

    private void showListOrderDetail(View view) {
        ApiCaller caller=new ApiCaller(getContext());
        Map<String,String> headers=new HashMap<String,String>();
        headers.put("order_id",getArguments().get("Order id").toString());
        caller.get(getResources().getString(R.string.API_URL)+"/order_detail/order_id", headers, new ApiCaller.OnSuccess() {
            @Override
            public void onSucess(String response) {

                JsonResponse = response.toString();
                Gson gson = new Gson();
                if (JsonResponse != null) {
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
        storeAddress=getArguments().get("Store address").toString();
        customerAddress=getArguments().get("Customer address").toString();
        shipperID=getArguments().getInt("Shipper id");
    }

    private void setContent(View view) throws IOException {
        setHeaderContent(view);
        ApiCaller caller=new ApiCaller(getContext());
        Map<String,String> headers=new HashMap<String,String>();
        headers.put("order_id",getArguments().get("Order id").toString());
        caller.get(getResources().getString(R.string.API_URL)+"/order", headers, new ApiCaller.OnSuccess() {
            @Override
            public void onSucess(String response) {

                JsonResponse = response.toString();
                Gson gson = new Gson();
                if (JsonResponse != null) {
                    orderInfoResponse = gson.fromJson(JsonResponse, OrderInfoResponse.class);
                    textViewTotal.setText("Tổng tiền: "+orderInfoResponse.getSum_price().toString()+"đ");
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
        setDistanceAndDuration(view);
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

    private void setEventClick(View view) {
        setBackButtonEvent(view);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragmentConfirmed=new FragmentConfirmed();
                Bundle bundle=new Bundle();
                bundle.putString("Store name",getArguments().get("Store name").toString());
                bundle.putInt("Order id",getArguments().getInt("Order id"));
                bundle.putString("Customer name",getArguments().get("Customer name").toString());
                bundle.putString("Store address",getArguments().get("Store address").toString());
                bundle.putString("Customer address",getArguments().get("Customer address").toString());
                bundle.putInt("Order postion",getArguments().getInt("Order position"));
                bundle.putInt("Shipper id",shipperID);
                MainShipperActivity.positionChose=getArguments().getInt("Order positon");


                fragmentConfirmed.setArguments(bundle);
                sendUpdateToServer(view,fragmentConfirmed,getArguments().getInt("Order id"),getArguments().getInt("Shipper id"));
            }
        });
    }

    private void sendUpdateToServer(View view,Fragment fragmentConfirmed, int order_id, int shipper_id) {
        ApiCaller caller=new ApiCaller(getContext());
        UpdateOrderRequest updateOrderRequest=new UpdateOrderRequest(order_id,shipper_id);
        caller.put(getResources().getString(R.string.API_URL)+"/pick_order", new HashMap<String, String>(), updateOrderRequest, new ApiCaller.OnSuccess() {
            @Override
            public void onSucess(String response) {
                SharedPreferences.Editor editor=getActivity().getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE).edit();
                editor.putString("Store name",getArguments().get("Store name").toString());
                editor.putString("Store address",getArguments().get("Store address").toString());
                editor.putString("Customer name",getArguments().get("Customer name").toString());
                editor.putString("Customer address",getArguments().get("Customer address").toString());
                editor.putInt("Order id",getArguments().getInt("Order id"));
                editor.putBoolean("Confirmed",true);
                editor.commit();
                Toast.makeText(getContext(), response.toString(), Toast.LENGTH_LONG).show();
                FragmentConfirmed.confirmed=true;
                FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.container_fragment_order_detail,fragmentConfirmed).addToBackStack("Fragment order detail").commit();
            }
        }, new ApiCaller.OnError() {
            @Override
            public void onError(VolleyError error) {
                FragmentConfirmed.confirmed=false;
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

    private void setHeaderContent(View view) {
        textViewHeader.setText("Thông tin đơn hàng");
    }

    private void setBackButtonEvent(View view) {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    private void Mapping(View view) {
        btnBack=view.findViewById(R.id.buttonBack_header);
        textViewHeader=view.findViewById(R.id.header_content);
        buttonConfirm=view.findViewById(R.id.btn_confirm_fragment_order_detail);
        textViewDuration=view.findViewById(R.id.txt_duration_fragment_order_detail);
        textViewDistance=view.findViewById(R.id.txt_distance_fragment_order_detail);
        textViewTotal=view.findViewById(R.id.txt_total_fragment_order_detail);
        textViewShipFee=view.findViewById(R.id.txt_ship_fee_fragment_order_detail);
        rcvOrderDetail=view.findViewById(R.id.rcv_order_detail);
    }
}
