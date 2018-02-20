package ru.pds.eventsapp.Adapters;

import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.tool.Binding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.pds.eventsapp.Models.PojoSmallEvent;
import ru.pds.eventsapp.R;
import ru.pds.eventsapp.ViewModels.ProfileEventListitemFragmentVM;
import ru.pds.eventsapp.databinding.ProfileEventListitemBinding;

/**
 * Created by Alexey on 19.02.2018.
 */

public class ProfileEventsAdapter extends RecyclerView.Adapter<ProfileEventsAdapter.EventsViewHolder> {

    private PojoSmallEvent[] events;

    public ProfileEventsAdapter(PojoSmallEvent[] events) {
        this.events = events;
    }

    @Override
    public EventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ProfileEventListitemBinding binding = ProfileEventListitemBinding.inflate(inflater, parent, false);
        return new EventsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(EventsViewHolder holder, int position) {
        PojoSmallEvent user = events[position];
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return events.length;
    }


    public class EventsViewHolder extends RecyclerView.ViewHolder{

        private ProfileEventListitemBinding mBinding;

        public void bind(@NonNull PojoSmallEvent event){
            mBinding.setViewModel(new ProfileEventListitemFragmentVM(event));
            mBinding.executePendingBindings();
        }

        public EventsViewHolder(ProfileEventListitemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

        }
    }

}
