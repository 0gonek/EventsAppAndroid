package ru.pds.eventsapp.ViewModels;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.pds.eventsapp.Models.PojoSmallEvent;

/**
 * Created by Alexey on 19.02.2018.
 */
public class ProfileEventListitemFragmentVM {

    public PojoSmallEvent event;

    public String getName(){
        return event.name;
    }
    public String getDesc(){
        return event.description;
    }

    public String getDate(){
        Date date = new Date(event.date);
        return new SimpleDateFormat("dd MMM., yyyy г., HH:MM").format(date);
    }

    public ProfileEventListitemFragmentVM(PojoSmallEvent event) {
        this.event = event;
    }

}