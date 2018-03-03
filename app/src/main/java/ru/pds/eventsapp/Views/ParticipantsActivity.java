package ru.pds.eventsapp.Views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import ru.pds.eventsapp.Models.PojoGroupIdName;
import ru.pds.eventsapp.Models.PojoNameAndAvatar;
import ru.pds.eventsapp.Models.PojoUsersList;
import ru.pds.eventsapp.R;
import ru.pds.eventsapp.BR;

import com.squareup.picasso.Picasso;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

import java.util.ArrayList;
import java.util.Arrays;

import ru.pds.eventsapp.Singletones.WalkerApi;
import ru.pds.eventsapp.ViewModels.ParticipantsActivityVM;
import ru.pds.eventsapp.databinding.ActivityParticipantsBinding;


/**
 * Created by Alexey on 03.03.2018.
 */

class ParticipantsAdapter extends BaseAdapter {
    private ArrayList<PojoNameAndAvatar> mData;
    private Context mContext;

    public ParticipantsAdapter(ArrayList<PojoNameAndAvatar> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public PojoNameAndAvatar getItem(int index) {
        return mData.get(index);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.participant_listitem, viewGroup, false);
        }
        ((TextView) convertView.findViewById(R.id.partName)).setText(mData.get(i).name);
        Picasso.with(mContext).load(mData.get(i).avatar).fit().into((ImageView) convertView.findViewById(R.id.partAvatar));

        return convertView;
    }

}


public class ParticipantsActivity extends BindingActivity<ActivityParticipantsBinding, ParticipantsActivityVM> {

    @Override
    public ParticipantsActivityVM onCreate() {
        Boolean isGroup = getIntent().getBooleanExtra("isGroup", false);
        Long id = getIntent().getLongExtra("id", 0);

        Single<PojoUsersList> call;
        if (isGroup)
            call = WalkerApi.getInstance().getGroupParticipants(id);
        else
            call = WalkerApi.getInstance().getEventParticipants(id);

        call.subscribe(
                new Consumer<PojoUsersList>() {
                    @Override
                    public void accept(@NonNull PojoUsersList pojoUsersList) throws Exception {
                        getBinding().listView.setAdapter(
                                new ParticipantsAdapter(
                                        new ArrayList<>(Arrays.asList(pojoUsersList.pojoNameAndAvatars))
                                ,ParticipantsActivity.this)
                        );
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        finishActivity(0);
                    }
                });


        return new ParticipantsActivityVM(this);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_participants;
    }

}