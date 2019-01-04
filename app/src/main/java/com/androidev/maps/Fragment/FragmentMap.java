package com.androidev.maps.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidev.maps.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
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
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;
import static com.mapbox.core.constants.Constants.PRECISION_6;

public class FragmentMap extends Fragment {

    private MapView mapView;
    private MapboxMap mMap;
    private DirectionsRoute optimizedRoute;
    private MapboxOptimization optimizedClient;
    private Polyline optimizedPolyline;
    private List<Point> stops;
    private String searchedAddress;
    private Point origin;
    private Point detination;
    private Point currentPoint;
    private LatLng searchedLatLng;
    private LatLng currentLatLng;
    private Spinner spinnerChooseLocation;
    private TextView textViewPlaceInfo;
    private ImageView imageViewCloseLayoutPlaceInfo;
    private com.google.android.gms.location.places.ui.PlaceAutocompleteFragment autocompleteFragment;
    private Location currentLocation;
    private ImageButton buttonShowDirection;
    private ImageButton buttonBack;
    private static final String FIRST = "first";
    private static final String ANY = "any";
    private static final String TEAL_COLOR = "#23D2BE";
    private static final int POLYLINE_WIDTH = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), getResources().getString(R.string.access_token));
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapping(view);
        setData();
        // Setup the MapView
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mMap = mapboxMap;
                showCurrentLocation(mMap);
                handleSearchEvent();
                handleButtonShowDirectionClickEvent(mMap);
                handleBackButton();
                //handleSpinnerLocationEvent();
            }
        });
        return view;
    }

    private void handleSpinnerLocationEvent() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.location_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerChooseLocation.setAdapter(adapter);
        spinnerChooseLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem=parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Cửa hàng")){
                    showStoreLocation();
                }
            }
        });
    }

    private void showStoreLocation() {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(origin.latitude(),origin.longitude()))
                .title("Vị trí cửa hàng"));
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(origin.latitude(),origin.longitude())) // Sets the new camera position
                .zoom(12) // Sets the zoom
                .tilt(20) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 1500);
    }

    private void setData() {
        stops=new ArrayList<>();
    }

    private void handleBackButton() {
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
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
                searchedAddress=place.getAddress().toString();
                mMap.clear();
                searchedLatLng=new LatLng(place.getLatLng().latitude,place.getLatLng().longitude);
                showLocationWithPlace(place,mMap);
                showPlaceInfoOnScreen(place);
                com.google.android.gms.maps.model.LatLng newLatLng = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
                Toast.makeText(getContext(),status.getStatusMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPlaceInfoOnScreen(Place place) {
        String placeinfo="";
        if (place.getName()!=null)
            placeinfo=placeinfo+place.getName().toString()+"\n";
        if (place.getAddress()!=null){
            placeinfo=placeinfo+"Địa chỉ: ";
            placeinfo=placeinfo+place.getAddress().toString()+"\n";}
        if (place.getPhoneNumber()!=null) {
            placeinfo=placeinfo+"SDT: ";
            placeinfo=placeinfo+place.getPhoneNumber().toString()+"\n";
        }
        if (place.getWebsiteUri()!=null){
            placeinfo=placeinfo+"Website: ";
            placeinfo=placeinfo+place.getWebsiteUri().toString()+"\n";
        }
        if (place.getAttributions()!=null){
            placeinfo=placeinfo+place.getAddress().toString()+"\n";
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

    private void handleButtonShowDirectionClickEvent(MapboxMap mMap){
        buttonShowDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchedLatLng==null){
                    Toast.makeText(getContext(),"Bạn chưa chọn địa điểm",Toast.LENGTH_LONG).show();
                } else {
                    Icon icon = IconFactory.getInstance(getContext()).fromResource(R.drawable.scooter_front_view);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()))
                            .title("Bạn đang ở đây"))
                            .setIcon(icon);
                    DrawRouteBetween2Point(currentLatLng,searchedLatLng);
                }
            }
        });

    }

    private void DrawRouteBetween2Point(LatLng origin, LatLng destination) {
        stops.clear();
        stops.add(Point.fromLngLat(origin.getLongitude(),origin.getLatitude()));
        stops.add(Point.fromLngLat(destination.getLongitude(),destination.getLatitude()));
        getOptimizedRoute(stops);
    }

    private void showLocationWithPlace(Place place, MapboxMap mMap) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(place.getLatLng().latitude,place.getLatLng().longitude))
                .title("Vị trí cần tìm"));
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(place.getLatLng().latitude,place.getLatLng().longitude)) // Sets the new camera position
                .zoom(12.5) // Sets the zoom
                .tilt(20) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 2000);
    }


    private void mapping(View view) {
        mapView = view.findViewById(R.id.mapView);
        autocompleteFragment=(com.google.android.gms.location.places.ui.PlaceAutocompleteFragment)getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_map);
        textViewPlaceInfo=view.findViewById(R.id.txt_place_info_fragment_map);
        imageViewCloseLayoutPlaceInfo=view.findViewById(R.id.img_close_layout_place_info_fragment_map);
        buttonShowDirection=view.findViewById(R.id.btn_show_direction_fragment_map);
        buttonBack=view.findViewById(R.id.buttonBack_fragment_map);
        spinnerChooseLocation=view.findViewById(R.id.spinner_choose_location_fragment_map);
    }

    private void showCurrentLocation(MapboxMap mapboxMap) {
        currentLocation=getMyLocation();
        currentPoint=Point.fromLngLat(currentLocation.getLongitude(),currentLocation.getLatitude());
        currentLatLng=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        //add marker
        Icon icon = IconFactory.getInstance(getContext()).fromResource(R.drawable.scooter_front_view);
        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()))
                .title("Bạn đang ở đây"))
                .setIcon(icon);

        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())) // Sets the new camera position
                .zoom(12) // Sets the zoom
                .tilt(20) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 1500);
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
                    Toast.makeText(getContext(), "no success", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (response.body().trips().isEmpty()) {
                        Log.d("DirectionsActivity", "bbb" + " size = "
                                + response.body().trips().size());
                        Toast.makeText(getContext(), "successful_but_no_routes",
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

    private void addPointToStopsList(Point point) {
        stops.add(Point.fromLngLat(point.longitude(), point.latitude()));
    }

    private void addDestinationMarker(Point point) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(point.latitude(), point.longitude()))
                .title("Destination"));
    }

    private void addFirstStopToStopsList() {
        // Set first stop
        stops = new ArrayList<>();
        origin = FragmentConfirmed.originPoint;
        stops.add(origin);
    }

    @SuppressLint("MissingPermission")
    private Location getMyLocation() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            String provider = lm.getBestProvider(criteria, true);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                myLocation = lm.getLastKnownLocation(provider);
            }
        }
        return myLocation;
    }

}
