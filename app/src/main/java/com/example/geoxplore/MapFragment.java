package com.example.geoxplore;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.geoxplore.api.ApiUtils;
import com.example.geoxplore.api.model.Chest;
import com.example.geoxplore.api.model.HomeCords;
import com.example.geoxplore.api.service.UserService;
import com.example.geoxplore.utils.SavedData;
import com.mapbox.androidsdk.plugins.building.BuildingPlugin;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;
import com.mapbox.services.android.telemetry.location.LocationEngineProvider;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class MapFragment extends SupportMapFragment implements LocationEngineListener, PermissionsListener {
    public static final String TAG = "map_fragment";

    private BuildingPlugin buildingPlugin;
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private MapboxMap mapboxMap;
    private final String HOME_MARKER_TITLE = "My Home";

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
//        View view = inflater.inflate(R.layout.fragment_map, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        super.onMapReady(mapboxMap);
        this.mapboxMap = mapboxMap;

        buildingPlugin = new BuildingPlugin(mapView, mapboxMap);
        buildingPlugin.setVisibility(true);
        enableLocationPlugin();

        //TODO 1.POLECAM ZACZAC OD TEGO (TO CO JA PISALEM)
        setInitialParams();
        addHomeMarkerAndLoadBoxes();
        setOnMarkerClickListener();
        // XD XD 05:00
    }
    private void setInitialParams() {
        mapboxMap.setCameraPosition(new CameraPosition.Builder().target(new LatLng(50.06688579999999, 19.91361919999997)).build());
        mapboxMap.setMinZoomPreference(15);
        mapboxMap.setMaxZoomPreference(19);
        mapboxMap.setZoom(17);
    }

    private void addHomeMarkerAndLoadBoxes() {
        LatLng cords = SavedData.getUserHome(getContext());
        if (cords != null) {
            useSavedUserHome(cords);
        } else {
            chooseNewUserHome();
        }
    }

    private void useSavedUserHome(LatLng cords) {
        mapboxMap.addMarker(new MarkerOptions().setPosition(cords).title(HOME_MARKER_TITLE));
        mapboxMap.setCameraPosition(new CameraPosition.Builder().target(cords).build());
        loadBoxes();
    }

    private void chooseNewUserHome() {
        Toast.makeText(getContext(), "Kliknij na mapę, aby ustawić dom", Toast.LENGTH_SHORT).show();
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
                                SavedData.setUserHome(getContext(), point);
                                mapboxMap.addMarker(new MarkerOptions().setPosition(point).title(HOME_MARKER_TITLE));
                                Toast.makeText(getContext(), R.string.map_home_set_msg, Toast.LENGTH_SHORT).show();
                                loadBoxes();
                            } else {
                                Toast.makeText(getContext(), "error code: " + voidResponse.errorBody().string(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void loadBoxes() {
        ApiUtils
                .getService(UserService.class)
                .getChests(getArguments().getString(Intent.EXTRA_USER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    Toast.makeText(getContext(), "Boxes loaded", Toast.LENGTH_SHORT).show();
                    for (Chest chest : data) {
                        // TODO ustawic inna ikone
                        mapboxMap.addMarker(new MarkerOptions().setPosition(chest.getLang()));
                    }

                });

    }

    private void setOnMarkerClickListener() {
        mapboxMap.setOnMarkerClickListener(marker -> {
            if (marker.getTitle() == null || !marker.getTitle().equals(HOME_MARKER_TITLE)) {
                Intent openBox = new Intent(this.getActivity(), OpenBoxActivity.class);
                startActivity(openBox);
                marker.remove();
                return true;
            }
            return false;
        });
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


}
