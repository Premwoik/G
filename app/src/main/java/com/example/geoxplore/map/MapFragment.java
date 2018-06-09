package com.example.geoxplore.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.geoxplore.OpenBoxActivity;
import com.example.geoxplore.R;
import com.example.geoxplore.api.ApiUtils;
import com.example.geoxplore.api.model.Chest;
import com.example.geoxplore.api.model.HomeCords;
import com.example.geoxplore.api.model.OpenBoxResponseData;
import com.example.geoxplore.api.service.UserService;
import com.mapbox.androidsdk.plugins.building.BuildingPlugin;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;
import com.mapbox.services.android.telemetry.location.LocationEngineProvider;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class MapFragment extends SupportMapFragment implements LocationEngineListener, PermissionsListener {
    public static final String TAG = "map_fragment";
    public static final String RESET_HOME = "reset_home";

    //    private BuildingPlugin buildingPlugin;
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private MapboxMap mapboxMap;
    private final String HOME_MARKER_TITLE = "My Home";
    private LinearLayout textLayout;

    private IconFactory iconFactory;
    private Icon icon_home, icon_box, icon_open_box;
    private List<Chest> chests;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mapView = (MapView) view;
        RelativeLayout v = new RelativeLayout(getContext());
        textLayout = new LinearLayout(getContext());
        v.addView(mapView);
        v.addView(textLayout);


//        View view = inflater.inflate(R.layout.fragment_map, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        super.onMapReady(mapboxMap);
        this.mapboxMap = mapboxMap;

//        buildingPlugin = new BuildingPlugin(mapView, mapboxMap);
//        buildingPlugin.setVisibility(true);
        enableLocationPlugin();
        setInitialParams();
        addHomeMarkerAndLoadBoxes();
        setOnMarkerClickListener();
        setCameraPosition(locationPlugin.getLastKnownLocation());
    }

    private void setInitialParams() {
        mapboxMap.setMinZoomPreference(MapConfig.minZoom);
        mapboxMap.setMaxZoomPreference(MapConfig.maxZoom);
        mapboxMap.setZoom(MapConfig.defaultZoom);
        mapboxMap.setStyleUrl("mapbox://styles/belaab/cjg4bq8vq1ir42rnyljaz229s");

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.home2);
        Bitmap bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.box2);
        Bitmap bm3 = BitmapFactory.decodeResource(getResources(), R.drawable.small_open_box);
        iconFactory = IconFactory.getInstance(this.getContext());
        icon_home = iconFactory.fromBitmap(bm);
        icon_box = iconFactory.fromBitmap(bm2);
        icon_open_box = iconFactory.fromBitmap(bm3);
    }

    private void addHomeMarkerAndLoadBoxes() {
        if (getArguments().getBoolean(RESET_HOME)) {
            chooseNewUserHome();
        } else {
            ApiUtils.getService(UserService.class)
                    .getHome(getArguments().getString(Intent.EXTRA_USER))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn(x -> new HomeCords("", ""))
                    .subscribe(voidResponse -> {
                        if (voidResponse.isValid()) {
                            Double latitude = Double.valueOf(voidResponse.getLatitude());
                            Double longitude = Double.valueOf(voidResponse.getLongitude());
                            useSavedUserHome(new LatLng(latitude, longitude));
                        } else {
                            chooseNewUserHome();
                        }
                    });
        }
    }

    private void useSavedUserHome(LatLng cords) {
        mapboxMap.addMarker(new MarkerOptions().setPosition(cords).title(HOME_MARKER_TITLE).icon(icon_home));
//        mapboxMap.setCameraPosition(new CameraPosition.Builder().target(cords).build());
        loadBoxes();
    }

    private void chooseNewUserHome() {
        setHomeTextLayout();
        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                ApiUtils.getService(UserService.class)
                        .setHome(getArguments().getString(Intent.EXTRA_USER), new HomeCords(point))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(voidResponse -> {
                            if (voidResponse.code() == 200) {
                                mapboxMap.removeOnMapClickListener(this);
                                mapboxMap.addMarker(new MarkerOptions().setPosition(point).title(HOME_MARKER_TITLE).icon(icon_home));
                                textLayout.removeAllViews();
                                loadBoxes();
                            } else {
                                onMapClick(point);
                                Toast.makeText(getContext(), "error code: " + voidResponse.errorBody().string(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void setHomeTextLayout() {
        TextView b = new TextView(getContext());
        b.setText("CLICK ON THE MAP TO SET HOME POSITION");
        b.setTextSize(30);
        b.setPadding(0, 20, 0, 20);
        b.setGravity(Gravity.CENTER);
        b.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        b.setLayoutParams(params);
        textLayout.addView(b);
    }

    private void loadBoxes() {
        ApiUtils
                .getService(UserService.class)
                .getChests(getArguments().getString(Intent.EXTRA_USER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
//                    Toast.makeText(getContext(), "Boxes loaded", Toast.LENGTH_SHORT).show();
                    chests = new ArrayList<>(data);
                    for(Chest box: data){
                        if (box.isOpened()) {
                            mapboxMap.addMarker(new MarkerOptions().setPosition(box.getLang()).icon(icon_open_box));
                        } else {
                            mapboxMap.addMarker(new MarkerOptions().setPosition(box.getLang()).icon(icon_box));
                        }
                    }
                });
    }

    private void setOnMarkerClickListener() {
        mapboxMap.setOnMarkerClickListener(marker -> {
            if (marker.getTitle() == null || !marker.getTitle().equals(HOME_MARKER_TITLE)) {
                return handleBoxMarkerClick(marker);
            }
            handleHomeMarkerClick(marker);
            return false;
        });
    }

    private boolean handleBoxMarkerClick(Marker box) {
        if (checkIfBoxIsInRange(box, locationPlugin.getLastKnownLocation())) {
            Intent openBox = new Intent(this.getActivity(), OpenBoxActivity.class);
            Chest chest = getChest(box);
            if(!chest.isOpened()) {
                LatLng l = box.getPosition();
                mapboxMap.removeMarker(box);
                mapboxMap.addMarker(new MarkerOptions().setPosition(l).icon(icon_open_box));
                ApiUtils
                        .getService(UserService.class)
                        .openChest(getArguments().getString(Intent.EXTRA_USER), chest.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorReturn(x -> new OpenBoxResponseData(-1))
                        .subscribe(x -> {
                            if (x.isValid()) {
                                openBox.putExtra("EXP", x.getExpGained());
                                openBox.putExtra("VALUE", chest.getValue());
                                chest.setOpened(true);
                                startActivity(openBox);
                            } else {
                                Toast.makeText(getContext(), "Nie można otworzyć skrzynki", Toast.LENGTH_SHORT).show();
                            }
                        });
            }else {
                Toast.makeText(getContext(), "You've already opened that chest!", Toast.LENGTH_LONG).show();
            }
            return true;
        }
//        Toast.makeText(getContext(), "You are too far from box.", Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), "Podejdź bliżej!", Toast.LENGTH_SHORT).show();
        return false;
    }


    private Chest getChest(Marker box) {
        LatLng position = box.getPosition();
        for (Chest c : chests) {
            if (c.getLang().getLatitude() == position.getLatitude() && c.getLang().getLongitude() == position.getLongitude()) {
                return c;
            }
//            Toast.makeText(getContext(), "skrzynia: " + c.getId() + c.getLang(), Toast.LENGTH_LONG).show();
        }
        return null;
    }


    private boolean checkIfBoxIsInRange(Marker box, Location userLocation) {

        double boxLatitude = box.getPosition().getLatitude();
        double boxLongitude = box.getPosition().getLongitude();
        double userLatitude = userLocation.getLatitude();
        double userLongitude = userLocation.getLongitude();

        float[] result = new float[1];
        Location.distanceBetween(boxLatitude, boxLongitude, userLatitude, userLongitude, result);
        return result[0] < MapConfig.maxRangeFromBox;
    }

    private void handleHomeMarkerClick(Marker home) {

    }


    @SuppressWarnings({"MissingPermission"})
    private void enableLocationPlugin() {
        if (getContext() == null) {
            Timber.e("Context is null");
            return;
        }
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
            // Create a location engine instance
            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(mapView, mapboxMap, locationEngine);
            locationPlugin.setLocationLayerEnabled(LocationLayerMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void initializeLocationEngine() {
        locationEngine = new LocationEngineProvider(getContext()).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();
        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void setCameraPosition(Location location) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 16));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
            Toast.makeText(getContext(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
//            finish();
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            setCameraPosition(location);
            locationEngine.removeLocationEngineListener(this);
        }
    }


    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onStart() {
        if (locationPlugin != null) {
            locationPlugin.onStart();
        }
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    public void onStop() {
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStop();
        }
        super.onStop();
    }

//    // Container Activity must implement this interface
//    public interface OnResetHomeListener {
//        public void resetHome(int position);
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnResetHomeListener) {
//            mListener = (OnResetHomeListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

}
