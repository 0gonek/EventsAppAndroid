package ru.pds.eventsapp.ViewModels;

import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.stfalcon.androidmvvmhelper.mvvm.fragments.FragmentViewModel;

import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.pds.eventsapp.Adapters.ProfileEventsAdapter;
import ru.pds.eventsapp.Models.PojoSmallEvent;
import ru.pds.eventsapp.Models.PojoSmallEvents;
import ru.pds.eventsapp.Singletones.WalkerApi;
import ru.pds.eventsapp.Views.NavigationActivity;
import ru.pds.eventsapp.Views.ProfileEventsFragment;

/**
 * Created by Alexey on 18.02.2018.
 */
public class ProfileEventsFragmentVM extends FragmentViewModel<ProfileEventsFragment> {

    public int type;

    public ProfileEventsFragmentVM(ProfileEventsFragment fragment, int type) {
        super(fragment);
        this.type = type;
        //loadEvents();
    }

    public void loadEvents() {

        WalkerApi.getInstance().profileEvents(type).subscribe(new Consumer<PojoSmallEvents>() {
            @Override
            public void accept(@NonNull PojoSmallEvents pojoSmallEvents) throws Exception {
                getFragment().getBinding().eventsRecycler.setAdapter(new ProfileEventsAdapter(pojoSmallEvents.pojoEvents));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
            }
        });
    }

}