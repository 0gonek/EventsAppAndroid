package ru.pds.eventsapp.Singletones;

import android.util.Log;

import com.google.android.gms.auth.api.Auth;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.pds.eventsapp.Models.LoginModel;
import ru.pds.eventsapp.Models.PojoChangeEvent;
import ru.pds.eventsapp.Models.PojoEvent;
import ru.pds.eventsapp.Models.PojoEventForMap;
import ru.pds.eventsapp.Models.PojoEventsForMap;
import ru.pds.eventsapp.Models.PojoNewEvent;
import ru.pds.eventsapp.Models.PojoSmallEvent;
import ru.pds.eventsapp.Models.PojoSmallEvents;
import ru.pds.eventsapp.Services.ApiService;

public class WalkerApi {

    private static final String API_URL = "http://walkerapp.ru:8080/";
    private static WalkerApi ourInstance = new WalkerApi();

    public static WalkerApi getInstance() {
        return ourInstance;
    }

    public ApiService getApiService() {
        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .client(new OkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiService.class);
    }


    public Single<LoginModel> loginVK(String id, String token) {

        ApiService loginService = getApiService();
        Single<LoginModel> call = loginService.loginVK(id, token);
        return call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Single<PojoSmallEvents> searchEvents(String part) {

        ApiService apiService = getApiService();
        Single<PojoSmallEvents> call = apiService.searchEvents(AuthenticatorSingleton.getInstance().currentUser.serverID, AuthenticatorSingleton.getInstance().accessToken,part);

        return call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<PojoSmallEvents> profileEvents(int type) {

        ApiService apiService = getApiService();
        Single<PojoSmallEvents> call = apiService.profileEvents(type, AuthenticatorSingleton.getInstance().currentUser.serverID, AuthenticatorSingleton.getInstance().accessToken);

        return call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<PojoEventsForMap> mapEvents(double minLat, double minLon, double maxLat, double maxLon) {

        ApiService apiService = getApiService();
        Single<PojoEventsForMap> call;

        if (AuthenticatorSingleton.getInstance().currentUser != null)
            call = apiService.mapEvents(minLat, maxLat, minLon, maxLon, AuthenticatorSingleton.getInstance().currentUser.serverID, AuthenticatorSingleton.getInstance().accessToken);
        else
            call = apiService.mapEventsPublic(minLat, maxLat, minLon, maxLon);

        return call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<PojoEvent> getEvent(long id){

        if(AuthenticatorSingleton.getInstance().currentUser==null)
            return null;

        return getApiService()
                .eventInfo(
                        AuthenticatorSingleton.getInstance().currentUser.serverID,
                        id,
                        AuthenticatorSingleton.getInstance().accessToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> changeEvent(PojoChangeEvent changes){

        if(AuthenticatorSingleton.getInstance().currentUser==null)
            return null;

        changes.token = AuthenticatorSingleton.getInstance().accessToken;
        changes.ownerId = AuthenticatorSingleton.getInstance().currentUser.serverID;

        return getApiService()
                .changeEvent(changes)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Long> newEvents(PojoNewEvent newEvent){

        if(AuthenticatorSingleton.getInstance().currentUser==null)
            return null;

        newEvent.token = AuthenticatorSingleton.getInstance().accessToken;
        newEvent.ownerId = AuthenticatorSingleton.getInstance().currentUser.serverID;

        return getApiService()
                .newEvent(newEvent)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Single<Boolean> acceptEvent(Long eventId){
        if(AuthenticatorSingleton.getInstance().currentUser==null)
            return null;
        return getApiService()
                .new_participant(AuthenticatorSingleton.getInstance().currentUser.serverID,eventId,AuthenticatorSingleton.getInstance().accessToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Single<Boolean> rejectEvent(Long eventId){
        if(AuthenticatorSingleton.getInstance().currentUser==null)
            return null;
        return getApiService()
                .new_participant(AuthenticatorSingleton.getInstance().currentUser.serverID,AuthenticatorSingleton.getInstance().currentUser.serverID,eventId,AuthenticatorSingleton.getInstance().accessToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public String imageUrl(String path){
        return API_URL+"events/get_picture?path="+path;
    }
}