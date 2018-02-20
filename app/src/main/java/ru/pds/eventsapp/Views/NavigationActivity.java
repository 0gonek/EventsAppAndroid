package ru.pds.eventsapp.Views;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.pds.eventsapp.Helpers.BottomNavigationViewHelper;
import ru.pds.eventsapp.Helpers.RxBus;
import ru.pds.eventsapp.Singletones.AuthenticatorSingleton;
import ru.pds.eventsapp.Singletones.FragmentsStore;
import ru.pds.eventsapp.ViewModels.NavigationActivityVM;
import ru.pds.eventsapp.R;
import ru.pds.eventsapp.BR;

import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;
import com.stfalcon.androidmvvmhelper.mvvm.fragments.BindingFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import ru.pds.eventsapp.ViewModels.ProfileEventsFragmentVM;
import ru.pds.eventsapp.databinding.ActivityNavigationBinding;
import ru.pds.eventsapp.databinding.FragmentProfileEventsBinding;

public class NavigationActivity extends BindingActivity<ActivityNavigationBinding, NavigationActivityVM> {

    Fragment currentFragment = null;
    String oldFragmentTag = null;

    void changeFragment(Fragment newFragment, String tag) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.setCustomAnimations(R.anim.anim_main_fragment_in, R.anim.anim_main_fragment_out);

        if (oldFragmentTag != null && fragmentManager.findFragmentByTag(oldFragmentTag) != null)
            ft.hide(fragmentManager.findFragmentByTag(oldFragmentTag));

        if (fragmentManager.findFragmentByTag(tag) != null)
            ft.show(fragmentManager.findFragmentByTag(tag));
        else
            ft.add(R.id.mainFragment, newFragment, tag);

        currentFragment = newFragment;
        oldFragmentTag = tag;

        ft.commit();

    }

    @Override
    public NavigationActivityVM onCreate() {


        BottomNavigationView bottomNavigationView = getBinding().bottomNavigation;
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);


        changeFragment(FragmentsStore.getInstance().getMapFragment(), "map");

        getBinding().bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_map:
                                changeFragment(FragmentsStore.getInstance().getMapFragment(), "map");
                                return true;
                            case R.id.action_groups:
                                changeFragment(FragmentsStore.getInstance().getGroupsFragment(), "groups");
                                return true;
                            case R.id.action_profile:
                                if (AuthenticatorSingleton.getInstance().currentUser != null)
                                    changeFragment(FragmentsStore.getInstance().getAuthorizedFragment(), "profile_auth");
                                else
                                    changeFragment(FragmentsStore.getInstance().getProfileFragment(), "profile");
                                return true;
                            case R.id.action_settings:
                                changeFragment(FragmentsStore.getInstance().getSettingsFragment(), "settings");
                                return true;
                        }
                        return true;
                    }
                });


        RxBus.instanceOf().onLogged().subscribe(
                new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(currentFragment.getTag().equals("profile")&&AuthenticatorSingleton.getInstance().currentUser != null)
                                    changeFragment(FragmentsStore.getInstance().getAuthorizedFragment(), "profile_auth");
                                if(currentFragment.getTag().equals("profile_auth")&&AuthenticatorSingleton.getInstance().currentUser == null)
                                    changeFragment(FragmentsStore.getInstance().getProfileFragment(), "profile");
                            }
                        });

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                }
        );

        return new NavigationActivityVM(this);

    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_navigation;
    }

}