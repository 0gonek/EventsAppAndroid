package ru.pds.eventsapp.Views;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LongSparseArray;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.pds.eventsapp.Adapters.EventsSearchAdapter;
import ru.pds.eventsapp.Models.PojoEvent;
import ru.pds.eventsapp.Models.PojoEventForMap;
import ru.pds.eventsapp.Models.PojoEventsForMap;
import ru.pds.eventsapp.R;
import ru.pds.eventsapp.BR;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stfalcon.androidmvvmhelper.mvvm.fragments.BindingFragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ru.pds.eventsapp.Singletones.AuthenticatorSingleton;
import ru.pds.eventsapp.Singletones.WalkerApi;
import ru.pds.eventsapp.ViewModels.MapFragmentVM;
import ru.pds.eventsapp.databinding.FragmentMapBinding;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


class EventInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    //private final View myContentsView;
    private final Activity _activity;

    public EventInfoWindowAdapter(Activity activity) {
        _activity = activity;
        ///myContentsView = activity.getLayoutInflater().inflate(R.layout.map_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        ContextThemeWrapper wrapper = new ContextThemeWrapper(_activity.getApplicationContext(), R.style.TransparentBackground);
        LayoutInflater inflater = (LayoutInflater) wrapper.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.map_info_window, null);

        if (marker.getSnippet() == null || marker.getSnippet().length() == 0) {
            ((TextView) layout.findViewById(R.id.eventName)).setText("Загрузка...");
            ((TextView) layout.findViewById(R.id.eventGroup)).setText("");
            ((TextView) layout.findViewById(R.id.eventDate)).setText("");
            ((ImageView) layout.findViewById(R.id.eventPic)).setImageResource(R.drawable.ic_loading_placeholder);

            return layout;
        } else {

            PojoEvent pojoEvent = new GsonBuilder().create().fromJson(marker.getSnippet(), PojoEvent.class);
            ((TextView) layout.findViewById(R.id.eventName)).setText(pojoEvent.name);

            if (pojoEvent.groupName == null || pojoEvent.groupName.length() == 0)
                (layout.findViewById(R.id.eventGroup)).setVisibility(View.GONE);
            else
                (layout.findViewById(R.id.eventGroup)).setVisibility(View.VISIBLE);

            ((TextView) layout.findViewById(R.id.eventGroup)).setText(pojoEvent.groupName);
            ((TextView) layout.findViewById(R.id.eventGroup)).setSingleLine(true);

            ((TextView) layout.findViewById(R.id.eventDate)).setText(new SimpleDateFormat("dd MMM, yyyy г., HH:mm").format(new Date(pojoEvent.date)));


            if (MapFragment.infoWindowBitmap != null)
                ((ImageView) layout.findViewById(R.id.eventPic)).setImageBitmap(MapFragment.infoWindowBitmap);
            return layout;
        }

    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}


public class MapFragment extends BindingFragment<MapFragmentVM, FragmentMapBinding> implements OnMapReadyCallback {

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if ((hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                || hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) && requestCode == 1000) {
            mMap.setMyLocationEnabled(true);
            return;
        }
    }

    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (PackageManager.PERMISSION_GRANTED == getActivity().checkSelfPermission(perm));
        } else {
            return true;
        }
    }


    public MapFragment() {
        // Required empty public constructor
    }

    public static Bitmap infoWindowBitmap = null;

    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private GoogleMap mMap;
    private Marker openedMarker = null;

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new EventInfoWindowAdapter(getActivity()));
        // Add a marker in Sydney and move the camera
        LatLng moscow = new LatLng(55.751244, 37.618423);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(moscow));
        if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) || hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            mMap.setMyLocationEnabled(true);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        }
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.muted_map_style));
        cache = new LongSparseArray<>();
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                getViewModel().updateMarkers(bounds);
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                if (AuthenticatorSingleton.getInstance().currentUser == null)
                    return;

                if (marker.getSnippet() != null && marker.getSnippet().length() != 0) {
                    Intent intent = new Intent(getActivity(), EventActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("eventInfo", marker.getSnippet());
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            }
        });
        final Bitmap error = BitmapFactory.decodeResource(getResources(), R.drawable.com_facebook_profile_picture_blank_square);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                int id = Integer.parseInt(marker.getTitle());
                marker.showInfoWindow();
                WalkerApi.getInstance().getEvent(id).subscribe(new Consumer<PojoEvent>() {
                    @Override
                    public void accept(@NonNull final PojoEvent pojoEvent) throws Exception {
                        new Single<Bitmap>() {
                            @Override
                            protected void subscribeActual(@NonNull SingleObserver<? super Bitmap> observer) {
                                Bitmap image = getBitmapFromURL(WalkerApi.getInstance().imageUrl(pojoEvent.pathToThePicture));
                                if (image == null)
                                    image = error;
                                observer.onSuccess(image);
                            }
                        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        new Consumer<Bitmap>() {
                                            @Override
                                            public void accept(@NonNull Bitmap bitmap) throws Exception {
                                                marker.hideInfoWindow();
                                                marker.setSnippet(new GsonBuilder().create().toJson(pojoEvent));
                                                infoWindowBitmap = bitmap;
                                                marker.showInfoWindow();
                                                openedMarker = marker;
                                            }
                                        },
                                        new Consumer<Throwable>() {
                                            @Override
                                            public void accept(@NonNull Throwable throwable) throws Exception {
                                                marker.hideInfoWindow();
                                                marker.setSnippet(new GsonBuilder().create().toJson(pojoEvent));
                                                infoWindowBitmap = error;
                                                marker.showInfoWindow();
                                                openedMarker = marker;
                                            }
                                        });

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        marker.hideInfoWindow();
                        Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
        });
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getContext().getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public LongSparseArray<Marker> cache = new LongSparseArray<>();

    void redrawMarkers(PojoEventsForMap events) {
        LongSparseArray<Marker> newCache = new LongSparseArray<>();
        if (mMap != null) {
            for (PojoEventForMap event : events.pojoEvents) {
                if (cache.get(event.id, null) == null) {
                    newCache.append(
                            event.id,
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(event.latitude, event.longitude))
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_icon_trans4x", 100, 100)))
                                    .title(event.id + "")
                            )
                    );
                } else {
                    newCache.append(event.id, cache.get(event.id));
                    cache.remove(event.id);
                }
            }
            for (int i = 0; i < cache.size(); i++)
                cache.valueAt(i).remove();

            cache = newCache;
        }

    }


    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    protected MapFragmentVM onCreateViewModel(FragmentMapBinding binding) {

        FragmentManager fm = getFragmentManager();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);//getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MapFragmentVM fragmentVM = new MapFragmentVM(this);


        fragmentVM.drawMarkers = new Consumer<PojoEventsForMap>() {
            @Override
            public void accept(@NonNull PojoEventsForMap pojoEventsForMap) throws Exception {
                redrawMarkers(pojoEventsForMap);
            }
        };

        if (AuthenticatorSingleton.getInstance().currentUser != null)
            getBinding().searchEditText.setAdapter(new EventsSearchAdapter(getContext()));

        return fragmentVM;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (openedMarker != null)
            openedMarker.hideInfoWindow();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getBinding().searchEditText.clearFocus();
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_map;
    }

}