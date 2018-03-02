package ru.pds.eventsapp.Views;

/**
 * Created by Alexey on 18.02.2018.
 */

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.stfalcon.androidmvvmhelper.mvvm.fragments.BindingFragment;

import ru.pds.eventsapp.Adapters.ProfileEventsAdapter;
import ru.pds.eventsapp.BR;
import ru.pds.eventsapp.Models.PojoSmallEvent;
import ru.pds.eventsapp.R;
import ru.pds.eventsapp.ViewModels.ProfileEventsFragmentVM;
import ru.pds.eventsapp.databinding.FragmentProfileEventsBinding;

/**
 * Created by Alexey on 18.02.2018.
 */
public class ProfileEventsFragment extends BindingFragment<ProfileEventsFragmentVM, FragmentProfileEventsBinding> {

    public ProfileEventsFragment() {
        // Required empty public constructor
    }
    int _type;
    static ProfileEventsFragment[] fragments = new ProfileEventsFragment[3];
    public static ProfileEventsFragment newInstance(int type) {
        if(fragments[type]==null) {
            fragments[type] = new ProfileEventsFragment();
            fragments[type]._type = type;
        }
        return fragments[type];
    }

    @Override
    protected ProfileEventsFragmentVM onCreateViewModel(FragmentProfileEventsBinding binding) {

        getBinding().eventsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().eventsRecycler.setAdapter(new ProfileEventsAdapter(new PojoSmallEvent[]{}));

        return new ProfileEventsFragmentVM(this,_type);
    }

    @Override
    public void onResume() {
        super.onResume();
        getViewModel().loadEvents();
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_profile_events;
    }

}