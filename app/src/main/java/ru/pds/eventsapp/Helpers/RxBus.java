package ru.pds.eventsapp.Helpers;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Alexey on 20.02.2018.
 */
public class RxBus {

    private static RxBus instance;

    private PublishSubject<Object> subject = PublishSubject.create();

    public static RxBus instanceOf() {
        if (instance == null) {
            instance = new RxBus();
        }
        return instance;
    }

    public void logged(Object object) {
        subject.onNext(object);
    }

    public Observable<Object> onLogged() {
        return subject;
    }
}