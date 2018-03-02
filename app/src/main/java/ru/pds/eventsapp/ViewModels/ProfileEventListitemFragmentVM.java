package ru.pds.eventsapp.ViewModels;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.pds.eventsapp.Models.PojoSmallEvent;

/**
 * Created by Alexey on 19.02.2018.
 */
public class ProfileEventListitemFragmentVM {

    public PojoSmallEvent event;

    public String getName() {
        return event.name;
    }

    public String getDesc() {
        return event.description==null||event.description.length()==0?"Описание отсутсвует":event.description;
    }

    public String getDate() {
        if (event.date == null)
            event.date = 0L;
        Date date = new Date(event.date);
        return new SimpleDateFormat("dd MMM, yyyy г., HH:mm").format(date);
    }

    public ProfileEventListitemFragmentVM(PojoSmallEvent event) {
        this.event = event;
    }

}