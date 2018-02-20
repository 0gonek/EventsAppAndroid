package ru.pds.eventsapp.Views;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import ru.pds.eventsapp.R;
import ru.pds.eventsapp.BR;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stfalcon.androidmvvmhelper.mvvm.fragments.BindingFragment;

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
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.muted_map_style));
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    protected MapFragmentVM onCreateViewModel(FragmentMapBinding binding) {
        FragmentManager fm = getFragmentManager();
        
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);//getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return new MapFragmentVM(this);
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