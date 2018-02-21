package ru.pds.eventsapp.ViewModels;

import com.google.android.gms.maps.model.LatLngBounds;
import com.stfalcon.androidmvvmhelper.mvvm.fragments.FragmentViewModel;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import ru.pds.eventsapp.Models.PojoEventsForMap;
import ru.pds.eventsapp.Singletones.WalkerApi;
import ru.pds.eventsapp.Views.MapFragment;

/**
 * Created by Alexey on 15.10.2017.
 */
public class MapFragmentVM extends FragmentViewModel<MapFragment> {

    public void updateMarkers(LatLngBounds bounds) {
        WalkerApi.getInstance().mapEvents(bounds.southwest.latitude, bounds.southwest.longitude, bounds.northeast.latitude, bounds.northeast.longitude)
                .subscribe(
                        new Consumer<PojoEventsForMap>() {
                            @Override
                            public void accept(@NonNull PojoEventsForMap pojoEventsForMap) throws Exception {
                                drawMarkers.accept(pojoEventsForMap);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {

                            }
                        });
    }
    public Consumer<PojoEventsForMap> drawMarkers;
    public MapFragmentVM(MapFragment fragment) {
        super(fragment);
    }

}