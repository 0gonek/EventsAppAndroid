package ru.pds.eventsapp.ViewModels;

import android.app.ProgressDialog;
import android.databinding.ObservableField;
import android.widget.Toast;

import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import ru.pds.eventsapp.Models.PojoChangeEvent;
import ru.pds.eventsapp.Models.PojoChangeGroup;
import ru.pds.eventsapp.Models.PojoEvent;
import ru.pds.eventsapp.Models.PojoGroup;
import ru.pds.eventsapp.Models.PojoNewEvent;
import ru.pds.eventsapp.Models.PojoNewGroup;
import ru.pds.eventsapp.Singletones.WalkerApi;
import ru.pds.eventsapp.Views.GroupActivity;

/**
 * Created by Alexey on 03.03.2018.
 */

public class GroupActivityVM extends ActivityViewModel<GroupActivity> {

    public void fetchGroup() {
        WalkerApi.getInstance().getGroup(group.get().id).subscribe(new Consumer<PojoGroup>() {
            @Override
            public void accept(@NonNull PojoGroup pojoGroup) throws Exception {
                putData(pojoGroup);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
            }
        });
    }

    public void createGroup(PojoNewGroup newGroup) {
        final ProgressDialog mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Идет сохранение...");
        mDialog.setCancelable(false);
        mDialog.show();
        newGroup.privacy = false;
        newGroup.type = 0;
        WalkerApi.getInstance().newGroup(newGroup).subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long id) throws Exception {
                mDialog.dismiss();

                if (id != -1)
                    getActivity().groupCreated(id);
                else
                    getActivity().groupCreateFailed();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                mDialog.dismiss();

                getActivity().groupCreateFailed();
            }
        });
    }


    public ObservableField<PojoGroup> group = new ObservableField<>();
    public ObservableField<String> description = new ObservableField<>();
    public ObservableField<String> groupName = new ObservableField<>();
    public ObservableField<String> participants = new ObservableField<>();

    public PublishSubject<Object> configureFab = PublishSubject.create();
    public PublishSubject<String> updatePicture = PublishSubject.create();

    public void putData(PojoGroup dataGroup) {
        group.set(dataGroup);

        description.set(dataGroup.description == null || dataGroup.description.length() == 0 ? "Описание отсутствует" : dataGroup.description);
        groupName.set(dataGroup.name);

        participants.set(group.get().participants + " участников");

        if (group.get().picture != null && group.get().picture.length() > 0)
            updatePicture.onNext(WalkerApi.getInstance().imageUrl(group.get().picture));

        configureFab.onNext(new Object());
    }

    public void subscribe() {
        WalkerApi.getInstance()
                .subscribeGroup(group.get().id)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (!aBoolean)
                            return;
                        group.get().accepted = aBoolean;
                        group.get().participants += 1;
                        putData(group.get());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void unsubscribe() {
        WalkerApi.getInstance()
                .unsubscribeGroup(group.get().id)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (!aBoolean)
                            return;
                        group.get().accepted = !aBoolean;
                        group.get().participants -= 1;
                        putData(group.get());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                });
    }

    byte[] changedPicture = null;

    public void changeGroup(PojoNewGroup newGroup) {

        PojoChangeGroup changeGroup = new PojoChangeGroup();
        changeGroup.description = newGroup.description;
        changeGroup.picture = newGroup.picture;
        changeGroup.id = group.get().id;

        final PojoGroup changedGroup = new PojoGroup();
        changedGroup.name = group.get().name;
        changedGroup.ownerId = group.get().ownerId;
        changedGroup.accepted = group.get().accepted;
        changedGroup.id = group.get().id;
        changedGroup.type = group.get().type;
        changedGroup.participants = group.get().participants;
        changedGroup.picture = group.get().picture;
        changedGroup.description = newGroup.description;
        changedGroup.privacy = newGroup.privacy;

        final ProgressDialog mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Идет сохранение...");
        mDialog.setCancelable(false);
        mDialog.show();

        WalkerApi.getInstance().changeGroup(changeGroup).subscribe(
                new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        mDialog.dismiss();

                        if (!aBoolean)
                            Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_SHORT).show();
                        else {
                            putData(changedGroup);
                        }

                        getActivity().exitEditMode(aBoolean);

                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        mDialog.dismiss();
                        getActivity().exitEditMode(false);
                        Toast.makeText(getActivity(), "Ошибка: " + throwable.getMessage(), Toast.LENGTH_SHORT);
                    }
                });

    }


    public GroupActivityVM(GroupActivity activity) {
        super(activity);
    }

}