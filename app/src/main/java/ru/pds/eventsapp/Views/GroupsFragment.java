package ru.pds.eventsapp.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import ru.pds.eventsapp.Adapters.EventsSearchAdapter;
import ru.pds.eventsapp.Adapters.GroupsAdapter;
import ru.pds.eventsapp.Adapters.GroupsSearchAdapter;
import ru.pds.eventsapp.Models.PojoGroupIdName;
import ru.pds.eventsapp.Models.PojoGroupIdNames;
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
        GroupsFragmentVM vm = new GroupsFragmentVM(this);
        getBinding().groupsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));


        getBinding().floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder popup = new AlertDialog.Builder(getActivity());
                final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.new_group_dialog, null);

                popup.setTitle("Новая группа")
                        .setView(dialogView)
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("СОЗДАТЬ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent act = new Intent(getActivity(), GroupActivity.class);
                                if (((EditText) dialogView.findViewById(R.id.group)).getText() == null || ((EditText) dialogView.findViewById(R.id.group)).getText().length() < 4) {
                                    Toast.makeText(getContext(), "Длина группы должна быть не менее 4 символов", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Bundle extras = new Bundle();
                                extras.putString("newGroupName", ((EditText) dialogView.findViewById(R.id.group)).getText().toString());
                                extras.putBoolean("eventAccepted", true);
                                act.putExtras(extras);
                                startActivity(act);
                            }
                        });
                popup.create().show();
            }
        });

        getBinding().searchEditText.setAdapter(new GroupsSearchAdapter(getContext()));


        vm.updateAdapter.subscribe(new Consumer<PojoGroupIdNames>() {
            @Override
            public void accept(@NonNull PojoGroupIdNames pojoGroupIdNames) throws Exception {

                if(pojoGroupIdNames!=null&&pojoGroupIdNames.pojoGroupIdNames!=null&&pojoGroupIdNames.pojoGroupIdNames.length>0){
                    getBinding().error.setVisibility(View.GONE);
                    getBinding().groupsRecycler.setAdapter(new GroupsAdapter(pojoGroupIdNames.pojoGroupIdNames));
                }else{
                    getBinding().error.setVisibility(View.VISIBLE);
                    getBinding().error.setText("Вы не подписаны ни на одну группу");

                }
            }
        });
        return vm;
    }

    @Override
    public void onResume() {
        super.onResume();
        getViewModel().loadGroups();
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