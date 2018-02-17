package ru.pds.eventsapp.Views;

import ru.pds.eventsapp.R;
import ru.pds.eventsapp.BR;

import com.stfalcon.androidmvvmhelper.mvvm.fragments.BindingFragment;

import ru.pds.eventsapp.ViewModels.GroupsFragmentVM;
import ru.pds.eventsapp.databinding.FragmentGroupsBinding;


/**
 * Created by Alexey on 15.10.2017.
 */
public class GroupsFragment extends BindingFragment<GroupsFragmentVM, FragmentGroupsBinding> {

    public GroupsFragment() {
        // Required empty public constructor
    }

    public static GroupsFragment newInstance() {
        return new GroupsFragment();
    }

    @Override
    protected GroupsFragmentVM onCreateViewModel(FragmentGroupsBinding binding) {
        return new GroupsFragmentVM(this);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_groups;
    }

}