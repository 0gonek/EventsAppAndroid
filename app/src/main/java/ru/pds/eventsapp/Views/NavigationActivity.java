package ru.pds.eventsapp.Views;

import android.graphics.Typeface;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableStringBuilder;
import android.text.style.TypefaceSpan;
import android.view.MenuItem;

import ru.pds.eventsapp.Helpers.BottomNavigationViewHelper;
import ru.pds.eventsapp.Singletones.AuthenticatorSingleton;
import ru.pds.eventsapp.Singletones.FragmentsStore;
import ru.pds.eventsapp.ViewModels.NavigationActivityVM;
import ru.pds.eventsapp.R;
import ru.pds.eventsapp.BR;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

import org.jetbrains.annotations.NotNull;

import ru.pds.eventsapp.databinding.ActivityNavigationBinding;

public class NavigationActivity extends BindingActivity<ActivityNavigationBinding, NavigationActivityVM> {

    Fragment currentFragment = null;
    String oldFragmentTag = null;

    void changeFragment(Fragment newFragment,String tag){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.setCustomAnimations(R.anim.anim_main_fragment_in, R.anim.anim_main_fragment_out);

        if(oldFragmentTag!=null&&fragmentManager.findFragmentByTag(oldFragmentTag) != null)
            ft.hide(fragmentManager.findFragmentByTag(oldFragmentTag));

        if(fragmentManager.findFragmentByTag(tag) != null)
            ft.show(fragmentManager.findFragmentByTag(tag));
        else
            ft.add(R.id.mainFragment, newFragment,tag);

        currentFragment = newFragment;
        oldFragmentTag = tag;

        ft.commit();

    }
    @Override
    public NavigationActivityVM onCreate() {



        BottomNavigationView bottomNavigationView = getBinding().bottomNavigation;
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);


        changeFragment(FragmentsStore.getInstance().getMapFragment(),"map");

        getBinding().bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_map:
                                changeFragment(FragmentsStore.getInstance().getMapFragment(),"map");
                                return true;
                            case R.id.action_groups:
                                changeFragment(FragmentsStore.getInstance().getGroupsFragment(),"groups");
                                return true;
                            case R.id.action_profile:
                                if(AuthenticatorSingleton.getInstance().currentUser!=null)
                                    changeFragment(FragmentsStore.getInstance().getAuthorizedFragment(),"profile_auth");
                                else
                                    changeFragment(FragmentsStore.getInstance().getProfileFragment(),"profile");
                                return true;
                            case R.id.action_settings:
                                changeFragment(FragmentsStore.getInstance().getSettingsFragment(),"settings");
                                return true;
                        }
                        return true;
                    }
                });

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