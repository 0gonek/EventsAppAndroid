package ru.pds.eventsapp.Views;

import ru.pds.eventsapp.R;
import ru.pds.eventsapp.BR;

import com.stfalcon.androidmvvmhelper.mvvm.fragments.BindingFragment;

import ru.pds.eventsapp.ViewModels.SettingsFragmentVM;
import ru.pds.eventsapp.databinding.FragmentSettingsBinding;


/**
 * Created by Alexey on 15.10.2017.
 */
public class SettingsFragment extends BindingFragment<SettingsFragmentVM, FragmentSettingsBinding> {

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    protected SettingsFragmentVM onCreateViewModel(FragmentSettingsBinding binding) {
        return new SettingsFragmentVM(this);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_settings;
    }

}