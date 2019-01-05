package com.androidev.maps.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidev.maps.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.optimization.v1.MapboxOptimization;
import com.mapbox.api.optimization.v1.models.OptimizationResponse;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.mapbox.core.constants.Constants.PRECISION_6;

public class ActivityMap extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private static final String TAG = "abc";
    public static final int RequestCode = 20;
    private MapView mapView;
    private MapboxMap mMap;
    private DirectionsRoute optimizedRoute;
    private MapboxOptimization optimizedClient;
    private Polyline optimizedPolyline;
    private List<Point> stops;
    private String searchedAddress;
    private String mapStyle;
    private Point origin;
    private Point detination;
    private Point currentPoint;
    private LatLng searchedLatLng;
    private LatLng currentLatLng;
    private TextView textViewPlaceInfo;
    private ImageView imageViewCloseLayoutPlaceInfo;
    private ImageView imageViewSatillineMap;
    private com.google.android.gms.location.places.ui.PlaceAutocompleteFragment autocompleteFragment;
    private Location currentLocation;
    private ImageButton buttonShowDirection;
    private ImageButton buttonBack;
    private ImageButton buttonGPS;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationManager locationManager;
    private static final String FIRST = "first";
    private static final String ANY = "any";
    private static final String TEAL_COLOR = "#23D2BE";
    private static final int POLYLINE_WIDTH = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(ActivityMap.this, getString(R.string.access_token));
        setContentView(R.layout.activity_map);
        mapping();
        setData();
        // Setup the MapView
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        mMap = mapboxMap;
        showCurrentLocation(mMap);
        handleSearchEvent();
        handleButtonShowDirectionClickEvent(mMap);
        handleSatellineImage(mMap);
        handleGPSbutton();
        handleSatellineImage(mMap);
        handleBackButton();
    }


    private void handleGPSbutton() {
        buttonGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showCurrentLocation(mMap);
                getPermissionLocation();
            }
        });
    }

    private void getPermissionLocation() {
        if (ContextCompat.checkSelfPermission(ActivityMap.this,
                ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(ActivityMap.this, new String[]{ACCESS_FINE_LOCATION}, RequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCurrentLocation(mMap);
            } else {
                Toast.makeText(ActivityMap.this, "không thể định vị vị trí khi không câp quyền", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleSatellineImage(MapboxMap mMap) {
        imageViewSatillineMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapStyle.equals("NORMAL")) {
                    mMap.setStyleUrl(Style.SATELLITE);
                    imageViewSatillineMap.setImageResource(R.drawable.normal_map);
                    mapStyle = "SATELLINE";
                } else {
                    mMap.setStyleUrl(Style.MAPBOX_STREETS);
                    imageViewSatillineMap.setImageResource(R.drawable.satelline_map);
                    mapStyle = "NORMAL";
                }
            }
        });
    }


    private void handleButtonShowDirectionClickEvent(MapboxMap mMap) {
        buttonShowDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchedLatLng == null) {
                    Toast.makeText(ActivityMap.this, "Bạn chưa chọn địa điểm", Toast.LENGTH_LONG).show();
                } else {
                    Icon icon = IconFactory.getInstance(ActivityMap.this).fromResource(R.drawable.scooter_front_view);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                            .title("Bạn đang ở đây"))
                            .setIcon(icon);
                    DrawRouteBetween2Point(currentLatLng, searchedLatLng);
                }
            }
        });
    }

    private void DrawRouteBetween2Point(LatLng origin, LatLng destination) {
        stops.clear();
        stops.add(Point.fromLngLat(origin.getLongitude(), origin.getLatitude()));
        stops.add(Point.fromLngLat(destination.getLongitude(), destination.getLatitude()));
        getOptimizedRoute(stops);
    }

    private void getOptimizedRoute(List<Point> coordinates) {
        optimizedClient = MapboxOptimization.builder()
                .source(FIRST)
                .destination(ANY)
                .coordinates(coordinates)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(Mapbox.getAccessToken())
                .build();

        optimizedClient.enqueueCall(new Callback<OptimizationResponse>() {
            @Override
            public void onResponse(Call<OptimizationResponse> call, Response<OptimizationResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d("DirectionsActivity", "cccc");
                    Toast.makeText(ActivityMap.this, "no success", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (response.body().trips().isEmpty()) {
                        Log.d("DirectionsActivity", "bbb" + " size = "
                                + response.body().trips().size());
                        Toast.makeText(ActivityMap.this, "successful_but_no_routes",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

// Get most optimized route from API response
                optimizedRoute = response.body().trips().get(0);
                drawOptimizedRoute(optimizedRoute);
            }

            @Override
            public void onFailure(Call<OptimizationResponse> call, Throwable throwable) {
                Log.d("DirectionsActivity", "Error: " + throwable.getMessage());
            }
        });
    }

    private void drawOptimizedRoute(DirectionsRoute route) {
        // Remove old polyline
        if (optimizedPolyline != null) {
            mMap.removePolyline(optimizedPolyline);
        }
// Draw points on MapView
        LatLng[] pointsToDraw = convertLineStringToLatLng(route);
        optimizedPolyline = mMap.addPolyline(new PolylineOptions()
                .add(pointsToDraw)
                .color(Color.parseColor(TEAL_COLOR))
                .width(POLYLINE_WIDTH));
    }

    private LatLng[] convertLineStringToLatLng(DirectionsRoute route) {
        // Convert LineString coordinates into LatLng[]
        LineString lineString = LineString.fromPolyline(route.geometry(), PRECISION_6);
        List<Point> coordinates = lineString.coordinates();
        LatLng[] points = new LatLng[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            points[i] = new LatLng(
                    coordinates.get(i).latitude(),
                    coordinates.get(i).longitude());
        }
        return points;
    }

    private void handleSearchEvent() {
        //filter by country
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("VN")
                .build();
        autocompleteFragment.setFilter(typeFilter);
        handlePlaceClickEvent();
    }

    private void handlePlaceClickEvent() {
        autocompleteFragment.setOnPlaceSelectedListener(new com.google.android.gms.location.places.ui.PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                searchedAddress = place.getAddress().toString();
                mMap.clear();
                searchedLatLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                showLocationWithPlace(place, mMap);
                showPlaceInfoOnScreen(place);
                com.google.android.gms.maps.model.LatLng newLatLng = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
                Toast.makeText(ActivityMap.this, status.getStatusMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPlaceInfoOnScreen(Place place) {
        String placeinfo = "";
        if (place.getName() != null)
            placeinfo = placeinfo + place.getName().toString() + "\n";
        if (place.getAddress() != null) {
            placeinfo = placeinfo + "Địa chỉ: ";
            placeinfo = placeinfo + place.getAddress().toString() + "\n";
        }
        if (place.getPhoneNumber() != null) {
            placeinfo = placeinfo + "SDT: ";
            placeinfo = placeinfo + place.getPhoneNumber().toString() + "\n";
        }
        if (place.getWebsiteUri() != null) {
            placeinfo = placeinfo + "Website: ";
            placeinfo = placeinfo + place.getWebsiteUri().toString() + "\n";
        }
        if (place.getAttributions() != null) {
            placeinfo = placeinfo + place.getAddress().toString() + "\n";
        }
        textViewPlaceInfo.setText(placeinfo);
        imageViewCloseLayoutPlaceInfo.setImageResource(R.drawable.close_button);
        imageViewCloseLayoutPlaceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewPlaceInfo.setText("");
                imageViewCloseLayoutPlaceInfo.setImageDrawable(null);
            }
        });
    }

    private void showLocationWithPlace(Place place, MapboxMap mMap) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude))
                .title("Vị trí cần tìm"));
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude)) // Sets the new camera position
                .zoom(12.5) // Sets the zoom
                .tilt(20) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 2000);
    }

    private void showCurrentLocation(MapboxMap mMap) {

        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // Creating a criteria object to retrieve provider
            if(ActivityCompat.checkSelfPermission(ActivityMap.this,ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                Criteria criteria = new Criteria();

                // Getting the name of the best provider
                String provider = locationManager.getBestProvider(criteria, true);

                // Getting Current Location
                Location location = locationManager.getLastKnownLocation(provider);

                if (location != null) {
                    onLocationChanged(location);
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, this);
            }
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    private Location getMyLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 5, this);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (myLocation == null) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                String provider = lm.getBestProvider(criteria, true);
                myLocation = lm.getLastKnownLocation(provider);
            }
            return myLocation;
        }
        return null;

    }


    private void mapping() {
        mapView = findViewById(R.id.mapView);
        autocompleteFragment=(com.google.android.gms.location.places.ui.PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_map);
        textViewPlaceInfo=findViewById(R.id.txt_place_info_fragment_map);
        imageViewCloseLayoutPlaceInfo=findViewById(R.id.img_close_layout_place_info_fragment_map);
        buttonShowDirection=findViewById(R.id.btn_show_direction_fragment_map);
        buttonBack=findViewById(R.id.buttonBack_fragment_map);
        imageViewSatillineMap=findViewById(R.id.img_satilline_map);
        buttonGPS=findViewById(R.id.gps_fixed_indicator);
    }
    private void setData() {
        stops=new ArrayList<>();
        mapStyle="NORMAL";
        imageViewSatillineMap=findViewById(R.id.img_satilline_map);
    }

    private void handleBackButton() {
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation=location;
        currentPoint = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
        currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        //add marker
        Icon icon = IconFactory.getInstance(ActivityMap.this).fromResource(R.drawable.scooter_front_view);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .title("Bạn đang ở đây"))
                .setIcon(icon);

        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())) // Sets the new camera position
                .zoom(12) // Sets the zoom
                .tilt(20) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 1500);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
