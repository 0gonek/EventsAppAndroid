package ru.pds.eventsapp.Services;

import java.util.Observable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.pds.eventsapp.Models.LoginModel;

/**
 * Created by Alexey on 09.02.2018.
 */

public interface ApiService {

    @GET("/users/loginvk")
    Single<LoginModel> loginVK(
            @Query("integration_id") String integrationid,
            @Query("token") String token );

}
