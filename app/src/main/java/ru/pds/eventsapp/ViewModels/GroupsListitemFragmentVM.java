package ru.pds.eventsapp.ViewModels;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.pds.eventsapp.Models.PojoGroupIdName;
import ru.pds.eventsapp.Models.PojoSmallEvent;

/**
 * Created by Alexey on 19.02.2018.
 */
public class GroupsListitemFragmentVM {

    public PojoGroupIdName groupIdName;

    public String getName() {
        return groupIdName.name;
    }


    public GroupsListitemFragmentVM(PojoGroupIdName groupIdName) {
        this.groupIdName = groupIdName;
    }

}