package ru.pds.eventsapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.pds.eventsapp.Models.PojoGroupIdName;
import ru.pds.eventsapp.Models.PojoSmallEvent;
import ru.pds.eventsapp.R;
import ru.pds.eventsapp.ViewModels.GroupsListitemFragmentVM;
import ru.pds.eventsapp.ViewModels.ProfileEventListitemFragmentVM;
import ru.pds.eventsapp.Views.EventActivity;
import ru.pds.eventsapp.Views.GroupActivity;
import ru.pds.eventsapp.Views.NavigationActivity;
import ru.pds.eventsapp.databinding.GroupsListitemBinding;
import ru.pds.eventsapp.databinding.ProfileEventListitemBinding;

/**
 * Created by Alexey on 19.02.2018.
 */

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupViewHolder> {

    private PojoGroupIdName[] groups;

    public GroupsAdapter(PojoGroupIdName[] groups) {
        this.groups = groups;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        GroupsListitemBinding binding = GroupsListitemBinding.inflate(inflater, parent, false);
        return new GroupViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        holder.bind(groups[position]);
    }

    @Override
    public int getItemCount() {
        return groups.length;
    }


    public class GroupViewHolder extends RecyclerView.ViewHolder {

        private GroupsListitemBinding mBinding;

        private Activity getActivity() {
            Context context = itemView.getContext();
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
            return null;
        }

        public void bind(@NonNull final PojoGroupIdName groupIdName) {
            mBinding.setViewModel(new GroupsListitemFragmentVM(groupIdName));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent groupIntent = new Intent(getActivity(), GroupActivity.class);

                    Bundle b = new Bundle();

                    b.putString("groupName", groupIdName.name);
                    b.putLong("groupId", groupIdName.id);
                    b.putBoolean("eventAccepted", true);

                    groupIntent.putExtras(b);
                    getActivity().startActivity(groupIntent);
                }
            });

            mBinding.executePendingBindings();
        }

        public GroupViewHolder(GroupsListitemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

        }
    }

}
