package ru.pds.eventsapp.Views;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LongSparseArray;
import android.view.View;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import ru.pds.eventsapp.Models.PojoEventForMap;
import ru.pds.eventsapp.Models.PojoEventsForMap;
import ru.pds.eventsapp.R;
import ru.pds.eventsapp.BR;

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
import com.stfalcon.androidmvvmhelper.mvvm.fragments.BindingFragment;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;

import ru.pds.eventsapp.ViewModels.MapFragmentVM;
import ru.pds.eventsapp.databinding.FragmentMapBinding;


/**
 * Created by Alexey on 15.10.2017.
 */
public class MapFragment extends BindingFragment<MapFragmentVM, FragmentMapBinding> implements OnMapReadyCallback {

    public MapFragment() {
        // Required empty public constructor
    }

    private GoogleMap mMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.muted_map_style));
        cache = new LongSparseArray<>();
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                getViewModel().updateMarkers(bounds);
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
                                    .title(event.id + "")
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_icon_trans4x",100,100)))
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

        return fragmentVM;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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