package com.androidev.maps.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidev.maps.R;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.optimization.v1.MapboxOptimization;
import com.mapbox.api.optimization.v1.models.OptimizationResponse;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;

public class FragmentMap extends Fragment {

    private MapView mapView;
    private MapboxMap mMap;
    private DirectionsRoute optimizedRoute;
    private MapboxOptimization optimizedClient;
    private Polyline optimizedPolyline;
    private List<Point> stops;
    private Point origin;
    private Point detination;

    private static final String FIRST = "first";
    private static final String ANY = "any";
    private static final String TEAL_COLOR = "#23D2BE";
    private static final int POLYLINE_WIDTH = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), getResources().getString(R.string.access_token));
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        stops = new ArrayList<>();

// Add the origin Point to the list
        addFirstStopToStopsList(view);

        // Setup the MapView
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mMap = mapboxMap;
// Add origin and destination to the mapboxMap
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(origin.latitude(), origin.longitude()))
                        .title("origin"));
                Toast.makeText(getContext(), "hahaha", Toast.LENGTH_SHORT).show();
                detination=FragmentConfirmed.destinationPoint;
                addDestinationMarker(detination);
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(detination.latitude(),detination.longitude())) // Sets the new camera position
                        .zoom(12) // Sets the zoom
                        .bearing(180) // Rotate the camera
                        .tilt(30) // Set the camera tilt
                        .build(); // Creates a CameraPosition from the builder

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 3000);
                addPointToStopsList(detination);
                getOptimizedRoute(stops);
            }
        });

        return view;
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

    private void addFirstStopToStopsList(View view) {
        // Set first stop
        origin = FragmentConfirmed.originPoint;
        stops.add(origin);
    }

}
