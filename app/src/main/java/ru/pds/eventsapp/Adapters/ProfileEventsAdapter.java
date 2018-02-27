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

import ru.pds.eventsapp.Models.PojoSmallEvent;
import ru.pds.eventsapp.R;
import ru.pds.eventsapp.ViewModels.ProfileEventListitemFragmentVM;
import ru.pds.eventsapp.Views.EventActivity;
import ru.pds.eventsapp.Views.NavigationActivity;
import ru.pds.eventsapp.databinding.ProfileEventListitemBinding;

/**
 * Created by Alexey on 19.02.2018.
 */

public class ProfileEventsAdapter extends RecyclerView.Adapter<ProfileEventsAdapter.EventViewHolder> {

    private PojoSmallEvent[] events;

    public ProfileEventsAdapter(PojoSmallEvent[] events) {
        this.events = events;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ProfileEventListitemBinding binding = ProfileEventListitemBinding.inflate(inflater, parent, false);
        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        PojoSmallEvent user = events[position];
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return events.length;
    }


    public class EventViewHolder extends RecyclerView.ViewHolder{

        private ProfileEventListitemBinding mBinding;

        private Activity getActivity() {
            Context context = itemView.getContext();
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity)context;
                }
                context = ((ContextWrapper)context).getBaseContext();
            }
            return null;
        }

        public void bind(@NonNull final PojoSmallEvent event){
            mBinding.setViewModel(new ProfileEventListitemFragmentVM(event));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent eventIntent = new Intent(getActivity(), EventActivity.class);

                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            getActivity(),
                            Pair.create(view.findViewById(R.id.eventName),"eventName"),
                            Pair.create(view.findViewById(R.id.eventDesc),"eventDesc"));

                    Bundle b = new Bundle();

                    b.putString("eventName",event.name);
                    b.putString("eventDesc",event.description);
                    b.putLong("eventId",event.id);
                    b.putBoolean("eventAccepted",true);

                    eventIntent.putExtras(b);
                    getActivity().startActivity(eventIntent, optionsCompat.toBundle());
                }
            });

            mBinding.executePendingBindings();
        }

        public EventViewHolder(ProfileEventListitemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

        }
    }

}
