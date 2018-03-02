package ru.pds.eventsapp.Views;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LongSparseArray;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import ru.pds.eventsapp.Adapters.EventsSearchAdapter;
import ru.pds.eventsapp.Models.PojoEvent;
import ru.pds.eventsapp.Models.PojoEventForMap;
import ru.pds.eventsapp.Models.PojoEventsForMap;
import ru.pds.eventsapp.R;
import ru.pds.eventsapp.BR;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.stfalcon.androidmvvmhelper.mvvm.fragments.BindingFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;

import ru.pds.eventsapp.Singletones.WalkerApi;
import ru.pds.eventsapp.ViewModels.MapFragmentVM;
import ru.pds.eventsapp.databinding.FragmentMapBinding;


class EventInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View myContentsView;
    private final Activity _activity;

    public EventInfoWindowAdapter(Activity activity) {
        _activity = activity;
        myContentsView = activity.getLayoutInflater().inflate(R.layout.map_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        if (marker.getSnippet() == null || marker.getSnippet().length() == 0) {
            ((TextView) myContentsView.findViewById(R.id.eventName)).setText("Загрузка...");
            ((TextView) myContentsView.findViewById(R.id.eventGroup)).setText("");
            ((TextView) myContentsView.findViewById(R.id.eventDate)).setText("");
            Picasso.with(_activity)
                    .load(R.drawable.ic_loading_placeholder)
                    .into((ImageView) myContentsView.findViewById(R.id.eventPic));


            return myContentsView;
        } else {

            PojoEvent pojoEvent = new GsonBuilder().create().fromJson(marker.getSnippet(), PojoEvent.class);
            Bitmap avatar = new GsonBuilder().create().fromJson(marker.getId(),Bitmap.class);
            ((TextView) myContentsView.findViewById(R.id.eventName)).setText(pojoEvent.name);

            if(pojoEvent.groupName==null||pojoEvent.groupName.length()==0)
                (myContentsView.findViewById(R.id.eventGroup)).setVisibility(View.GONE);
            else
                (myContentsView.findViewById(R.id.eventGroup)).setVisibility(View.VISIBLE);

            ((TextView) myContentsView.findViewById(R.id.eventGroup)).setText(pojoEvent.groupName);
            ((TextView) myContentsView.findViewById(R.id.eventGroup)).setSingleLine(true);

            ((TextView) myContentsView.findViewById(R.id.eventDate)).setText(new SimpleDateFormat("dd MMM, yyyy г., HH:mm").format(new Date(pojoEvent.date)));
            ((ImageView) myContentsView.findViewById(R.id.eventPic)).setImageBitmap(avatar);
            return myContentsView;
        }
    }
}


public class MapFragment extends BindingFragment<MapFragmentVM, FragmentMapBinding> implements OnMapReadyCallback {

    public MapFragment() {
        // Required empty public constructor
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
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
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
                if(marker.getSnippet()!=null&&marker.getSnippet().length()!=0){
                    Intent intent = new Intent(getActivity(),EventActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("eventInfo",marker.getSnippet());
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                int id = Integer.parseInt(marker.getTitle());
                marker.showInfoWindow();
                WalkerApi.getInstance().getEvent(id).subscribe(new Consumer<PojoEvent>() {
                    @Override
                    public void accept(@NonNull PojoEvent pojoEvent) throws Exception {
                            marker.hideInfoWindow();
                            marker.setSnippet(new GsonBuilder().create().toJson(pojoEvent));
                            marker.showInfoWindow();
                            openedMarker = marker;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        marker.hideInfoWindow();
                        Toast.makeText(getActivity(),"Произошла ошибка",Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
        });
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getContext().getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public LongSparseArray<Marker> cache = new LongSparseArray<>();
    void redrawMarkers(PojoEventsForMap events) {
        LongSparseArray<Marker> newCache = new LongSparseArray<>();
            if (mMap != null) {
            for (PojoEventForMap event : events.pojoEvents) {
                if(cache.get(event.id,null)==null) {
                    newCache.append(
                            event.id,
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(event.latitude, event.longitude))
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_icon_trans4x",100,100)))
                                    .title(event.id+"")
                            )
                    );
                }else{
                    newCache.append(event.id,cache.get(event.id));
                    cache.remove(event.id);
                }
            }
            for(int i = 0; i < cache.size(); i++)
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

        if(openedMarker!=null)
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