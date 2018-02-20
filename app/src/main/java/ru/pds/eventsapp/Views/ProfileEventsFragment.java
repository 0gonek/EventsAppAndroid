package ru.pds.eventsapp.Views;

/**
 * Created by Alexey on 18.02.2018.
 */

import android.support.v7.widget.LinearLayoutManager;

import com.stfalcon.androidmvvmhelper.mvvm.fragments.BindingFragment;

import ru.pds.eventsapp.BR;
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
    public static ProfileEventsFragment newInstance(int type) {
        ProfileEventsFragment fragment = new  ProfileEventsFragment();
        fragment._type = type;
        return fragment;
    }

    @Override
    protected ProfileEventsFragmentVM onCreateViewModel(FragmentProfileEventsBinding binding) {

        getBinding().eventsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));


        return new ProfileEventsFragmentVM(this,_type);
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