package com.androidev.maps.ApiHelper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ApiCaller {
    private Context context;

    public ApiCaller(Context context) {
        this.context = context;
    }

    public interface OnSuccess {
        void onSucess(String response);
    }
    private OnSuccess onSuccess;

    public interface OnError {
        void onError(VolleyError error);
    }
    private OnError onError;

    public ApiCaller() {

    }

    public void get(String url, final Map<String, String>  headers, final OnSuccess onSuccess, final OnError onError){
        RequestQueue requestQueue=Volley.newRequestQueue(context);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("abc", response);

                if (onSuccess!=null){
                    onSuccess.onSucess(response);
                }
                //Toast.makeText(MainShipperActivity.this,response,Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
                if (error instanceof NetworkError) {
                    Toast.makeText(context, "Oops. Network error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(context, "Oops. Auth Failure error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "Oops. No connection error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(context, "Oops. Timeout error!", Toast.LENGTH_LONG).show();
                }
                if (onError!=null){
                    onError.onError(error);
                }
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params=headers;
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);

            }
        };
        requestQueue.add(stringRequest);
    }
    public void post(String url, final Map<String, String>  headers, Object object,final OnSuccess onSuccess, final OnError onError){
        Gson gson=new Gson();
        final String string_Json=gson.toJson(object);

        RequestQueue requestQueue=Volley.newRequestQueue(context);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(ActivityMainShipper.this,response.toString(),Toast.LENGTH_LONG).show();
                Log.i("abc", response);
                //Toast.makeText(MainShipperActivity.this,response,Toast.LENGTH_LONG).show();
                if (onSuccess!=null){
                    onSuccess.onSucess(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
                if (error instanceof NetworkError) {
                    Toast.makeText(context, "Oops. Network error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(context, "Oops. Auth Failure error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "Oops. No connection error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(context, "Oops. Timeout error!", Toast.LENGTH_LONG).show();
                }
                if (onError!=null){
                    onError.onError(error);
                }
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params=headers;
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return string_Json == null ? null : string_Json.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", string_Json, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);

            }
        };
        requestQueue.add(stringRequest);
    }
    public void put(String url, final Map<String, String>  headers, Object object,final OnSuccess onSuccess, final OnError onError){
        Gson gson=new Gson();
        final String string_Json=gson.toJson(object);

        RequestQueue requestQueue=Volley.newRequestQueue(context);
        StringRequest stringRequest=new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(ActivityMainShipper.this,response.toString(),Toast.LENGTH_LONG).show();
                Log.i("abc", response);
                //Toast.makeText(MainShipperActivity.this,response,Toast.LENGTH_LONG).show();
                if (onSuccess!=null){
                    onSuccess.onSucess(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
                if (error instanceof NetworkError) {
                    Toast.makeText(context, "Oops. Network error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(context, "Oops. Auth Failure error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "Oops. No connection error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(context, "Oops. Timeout error!", Toast.LENGTH_LONG).show();
                }
                if (onError!=null){
                    onError.onError(error);
                }
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params=headers;
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return string_Json == null ? null : string_Json.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", string_Json, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);

            }
        };
        requestQueue.add(stringRequest);
    }
    public void delete(String url, final Map<String, String>  headers, Object object,final OnSuccess onSuccess, final OnError onError){
        Gson gson=new Gson();
        final String string_Json=gson.toJson(object);

        RequestQueue requestQueue=Volley.newRequestQueue(context);
        StringRequest stringRequest=new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(ActivityMainShipper.this,response.toString(),Toast.LENGTH_LONG).show();
                Log.i("abc", response);
                //Toast.makeText(MainShipperActivity.this,response,Toast.LENGTH_LONG).show();
                if (onSuccess!=null){
                    onSuccess.onSucess(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
                if (error instanceof NetworkError) {
                    Toast.makeText(context, "Oops. Network error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(context, "Oops. Auth Failure error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "Oops. No connection error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(context, "Oops. Timeout error!", Toast.LENGTH_LONG).show();
                }
                if (onError!=null){
                    onError.onError(error);
                }
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params=headers;
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return string_Json == null ? null : string_Json.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", string_Json, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);

            }
        };
        requestQueue.add(stringRequest);
    }

}
