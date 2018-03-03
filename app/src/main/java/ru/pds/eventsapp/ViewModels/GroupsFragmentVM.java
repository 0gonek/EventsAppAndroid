package ru.pds.eventsapp.ViewModels;

import android.widget.Toast;

import com.stfalcon.androidmvvmhelper.mvvm.fragments.FragmentViewModel;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import ru.pds.eventsapp.Models.PojoGroupIdNames;
import ru.pds.eventsapp.Singletones.WalkerApi;
import ru.pds.eventsapp.Views.GroupsFragment;

/**
 * Created by Alexey on 15.10.2017.
 */
public class GroupsFragmentVM extends FragmentViewModel<GroupsFragment> {


    public void loadGroups() {
        WalkerApi.getInstance().getOwnGroups().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<PojoGroupIdNames>() {
                            @Override
                            public void accept(@NonNull PojoGroupIdNames pojoGroupIdNames) throws Exception {
                                updateAdapter.onNext(pojoGroupIdNames);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                Toast.makeText(getActivity(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    public PublishSubject<PojoGroupIdNames> updateAdapter = PublishSubject.create();

    public GroupsFragmentVM(GroupsFragment fragment) {
        super(fragment);
    }

}