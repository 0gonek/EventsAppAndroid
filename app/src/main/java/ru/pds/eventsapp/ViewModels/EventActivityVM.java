package ru.pds.eventsapp.ViewModels;

import android.databinding.Bindable;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import ru.pds.eventsapp.Models.PojoEvent;
import ru.pds.eventsapp.Models.PojoNewEvent;
import ru.pds.eventsapp.Singletones.WalkerApi;
import ru.pds.eventsapp.Views.EventActivity;

/**
 * Created by Alexey on 21.02.2018.
 */

public class EventActivityVM extends ActivityViewModel<EventActivity> {

    public void fetchEvent(){
        WalkerApi.getInstance().getEvent(event.get().id).subscribe(new Consumer<PojoEvent>() {
            @Override
            public void accept(@NonNull PojoEvent pojoEvent) throws Exception {
                putData(pojoEvent);

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
            }
        });
    }

    public void createEvent(PojoNewEvent newEvent){
        WalkerApi.getInstance().newEvents(newEvent).subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long id) throws Exception {
                if(id!=-1)
                    getActivity().eventCreated(id);
                else
                    getActivity().eventCreateFailed();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                getActivity().eventCreateFailed();
            }
        });
    }


    public ObservableField<PojoEvent> event = new ObservableField<>();
    public ObservableField<String> description = new ObservableField<>();
    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> groupName = new ObservableField<>();
    public ObservableField<String> when = new ObservableField<>();
    public ObservableField<String> until = new ObservableField<>();



    public void putData(PojoEvent dataEvent){
        event.set(dataEvent);

        description.set(dataEvent.description==null?"Нет описания":dataEvent.description);
        name.set(dataEvent.name);
        groupName.set(dataEvent.groupName==null?"Без группы":dataEvent.groupName);

        if (event.get().date == null)
            event.get().date = 0L;
        Date date = new Date(event.get().date);
        when.set( new SimpleDateFormat("dd MMM, yyyy г., HH:MM").format(date) );

        if (event.get().duration == null)
            event.get().duration = 0L;
        date = new Date(event.get().date + event.get().duration);
        until.set( new SimpleDateFormat("dd MMM, yyyy г., HH:MM").format(date) );

    }

    public EventActivityVM(EventActivity activity) {
        super(activity);
    }

}