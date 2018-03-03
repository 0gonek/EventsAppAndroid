package ru.pds.eventsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import ru.pds.eventsapp.Models.PojoGroupIdName;
import ru.pds.eventsapp.Models.PojoGroupIdNames;
import ru.pds.eventsapp.Models.PojoSmallEvent;
import ru.pds.eventsapp.Singletones.WalkerApi;
import ru.pds.eventsapp.Views.EventActivity;
import ru.pds.eventsapp.Views.GroupActivity;

/**
 * Created by Alexey on 02.03.2018.
 */

public class GroupsSearchAdapter extends BaseAdapter implements Filterable {
    private ArrayList<PojoGroupIdName> mData;
    private Context mContext;
    public GroupsSearchAdapter(Context context) {
        super();
        mContext = context;
        mData = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public PojoGroupIdName getItem(int index) {
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
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        }
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(mData.get(i).name);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, GroupActivity.class);
                Bundle extras = new Bundle();
                extras.putLong("groupId",mData.get(i).id);
                extras.putString("groupName",mData.get(i).name);
                intent.putExtras(extras);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }
    private long lastPress = 0;
    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();

                if(System.currentTimeMillis() - lastPress > 500) {
                    lastPress = System.currentTimeMillis();

                    if (constraint != null && constraint.toString().length() > 3) {
                        mData = new ArrayList<>(Arrays.asList(WalkerApi.getInstance().searchGroups(constraint.toString()).blockingGet().pojoGroupIdNames));
                        filterResults.values = mData;
                        filterResults.count = mData.size();
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                if(results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return myFilter;
    }
}
