package ru.pds.eventsapp.ViewModels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.squareup.picasso.Picasso;
import com.stfalcon.androidmvvmhelper.mvvm.fragments.FragmentViewModel;

import org.reactivestreams.Publisher;

import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import jp.wasabeef.blurry.Blurry;
import ru.pds.eventsapp.Helpers.RxBus;
import ru.pds.eventsapp.Singletones.AuthenticatorSingleton;
import ru.pds.eventsapp.Views.ProfileAuthorizedFragment;
import ru.pds.eventsapp.Views.ProfileEventsFragment;
import ru.pds.eventsapp.Views.ProfileFragment;

/**
 * Created by Alexey on 15.02.2018.
 */
public class ProfileAuthorizedFragmentVM extends FragmentViewModel<ProfileAuthorizedFragment> {


    public ObservableField<String> name = new ObservableField<>();


    public void refresh(){

    }
    public void changeName(){

    }
    public void logout(){
        AuthenticatorSingleton.getInstance().deleteLogin(getFragment().getContext());
        RxBus.instanceOf().logged(new Object());
    }
    public PublishSubject<Object> avatar = PublishSubject.create();
    public void updateUI(){

        name.set(AuthenticatorSingleton.getInstance().currentUser.name);
        avatar.onNext(new Object());
    }

    public Runnable avatarListener;
    public ProfileEventsFragment[] tabs;

    public ProfileAuthorizedFragmentVM(ProfileAuthorizedFragment fragment) {
        super(fragment);

        tabs = new ProfileEventsFragment[3];
        for (int i = 0; i < 3; i++) {
            tabs[i]=ProfileEventsFragment.newInstance(i);
        }

        //updateUI();
    }

}