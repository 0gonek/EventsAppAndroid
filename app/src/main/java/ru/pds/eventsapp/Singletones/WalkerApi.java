package ru.pds.eventsapp.Singletones;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.pds.eventsapp.Models.LoginModel;
import ru.pds.eventsapp.Models.PojoSmallEvents;
import ru.pds.eventsapp.Services.ApiService;

public class WalkerApi {

    private static final String API_URL = "http://192.168.1.79:8080/";
    private static WalkerApi ourInstance = new WalkerApi();

    public static WalkerApi getInstance() {
        return ourInstance;
    }

    public ApiService getApiService() {
        return new Retrofit.Builder()
                .baseUrl(API_URL)
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

    public Single<PojoSmallEvents> profileEvents(int type) {

        ApiService apiService = getApiService();
        Single<PojoSmallEvents> call = apiService.profileEvents(type,AuthenticatorSingleton.getInstance().currentUser.serverID,AuthenticatorSingleton.getInstance().accessToken);
        return call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }


}