package ru.pds.eventsapp.ViewModels;

import android.app.ProgressDialog;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import ru.pds.eventsapp.Models.PojoChangeEvent;
import ru.pds.eventsapp.Models.PojoEvent;
import ru.pds.eventsapp.Models.PojoNewEvent;
import ru.pds.eventsapp.Singletones.AuthenticatorSingleton;
import ru.pds.eventsapp.Singletones.WalkerApi;
import ru.pds.eventsapp.Views.EventActivity;

/**
 * Created by Alexey on 21.02.2018.
 */

public class EventActivityVM extends ActivityViewModel<EventActivity> {

    public void fetchEvent() {
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

    public void createEvent(PojoNewEvent newEvent) {
        final ProgressDialog mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Идет сохранение...");
        mDialog.setCancelable(false);
        mDialog.show();
        WalkerApi.getInstance().newEvents(newEvent).subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long id) throws Exception {
                mDialog.dismiss();

                if (id != -1)
                    getActivity().eventCreated(id);
                else
                    getActivity().eventCreateFailed();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                mDialog.dismiss();

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
    public ObservableField<String> participants = new ObservableField<>();

    public PublishSubject<Object> configureFab = PublishSubject.create();
    public PublishSubject<String> updatePicture = PublishSubject.create();

    public void putData(PojoEvent dataEvent) {
        event.set(dataEvent);

        description.set(dataEvent.description == null || dataEvent.description.length() == 0 ? "Описание отсутствует" : dataEvent.description);
        name.set(dataEvent.name);
        groupName.set(dataEvent.groupName == null ? "Без группы" : dataEvent.groupName);

        if (event.get().date == null)
            event.get().date = 0L;
        Date date = new Date(event.get().date);
        when.set(new SimpleDateFormat("dd MMM, yyyy г., HH:mm").format(date));

        if (event.get().duration == null)
            event.get().duration = 0L;
        date = new Date(event.get().date + event.get().duration);
        until.set(new SimpleDateFormat("dd MMM, yyyy г., HH:mm").format(date));
        participants.set(event.get().participants + " участников");

        if(event.get().pathToThePicture!=null&&event.get().pathToThePicture.length()>0)
            updatePicture.onNext(WalkerApi.getInstance().imageUrl(event.get().pathToThePicture));

        configureFab.onNext(new Object());
    }

    public void acceptEvent() {
        WalkerApi.getInstance()
                .acceptEvent(event.get().id)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (!aBoolean)
                            return;
                        event.get().accepted = aBoolean;
                        event.get().participants += 1;
                        putData(event.get());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void rejectEvent() {
        WalkerApi.getInstance()
                .rejectEvent(event.get().id)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (!aBoolean)
                            return;
                        event.get().accepted = !aBoolean;
                        event.get().participants -= 1;
                        putData(event.get());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                });
    }

    byte[] changedPicture = null;

    public void changeEvent(PojoNewEvent newEvent) {

        PojoChangeEvent changeEvent = new PojoChangeEvent();
        changeEvent.date = newEvent.date;
        changeEvent.ownerId = AuthenticatorSingleton.getInstance().currentUser.serverID;
        changeEvent.description = newEvent.description;
        changeEvent.duration = newEvent.duration;
        changeEvent.latitude = newEvent.latitude;
        changeEvent.longitude = newEvent.longitude;
        changeEvent.picture = newEvent.picture;
        changeEvent.privacy = newEvent.privacy;
        changeEvent.id = event.get().id;

        final PojoEvent changedEvent = new PojoEvent();
        changedEvent.name = event.get().name;
        changedEvent.groupId = event.get().groupId;
        changedEvent.groupName = event.get().groupName;

        changedEvent.accepted = event.get().accepted;
        changedEvent.id = event.get().id;
        changedEvent.type = event.get().type;
        changedEvent.participants = event.get().participants;
        changedEvent.pathToThePicture = event.get().pathToThePicture;
        changedEvent.date = newEvent.date;
        changedEvent.description = newEvent.description;
        changedEvent.duration = newEvent.duration;
        changedEvent.latitude = newEvent.latitude;
        changedEvent.longitude = newEvent.longitude;
        changedEvent.privacy = newEvent.privacy;

        final ProgressDialog mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Идет сохранение...");
        mDialog.setCancelable(false);
        mDialog.show();

        WalkerApi.getInstance().changeEvent(changeEvent).subscribe(
                new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        mDialog.dismiss();

                        if(!aBoolean)
                            Toast.makeText(getActivity(),"Произошла ошибка",Toast.LENGTH_SHORT).show();
                        else{
                            changedEvent.pathToThePicture=null;
                            putData(changedEvent);
                        }

                        getActivity().exitEditMode(aBoolean);

                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        mDialog.dismiss();
                        getActivity().exitEditMode(false);
                        Toast.makeText(getActivity(),"Ошибка: "+throwable.getMessage(),Toast.LENGTH_SHORT);
                    }
                });

    }

    public EventActivityVM(EventActivity activity) {
        super(activity);
    }

}