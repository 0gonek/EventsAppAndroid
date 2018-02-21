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
import ru.pds.eventsapp.Models.PojoEventsForMap;
import ru.pds.eventsapp.Models.PojoSmallEvents;

/**
 * Created by Alexey on 09.02.2018.
 */

public interface ApiService {

    @GET("/users/loginvk")
    Single<LoginModel> loginVK(
            @Query("integration_id") String integrationid,
            @Query("token") String token );

    @GET("/events/get_profile_events")
    Single<PojoSmallEvents> profileEvents(
            @Query("type") int type,
            @Query("id") long userID,
            @Query("token") String token );
    @GET("/events/get_between")
    Single<PojoEventsForMap> mapEvents(
            @Query("min_lat") double minLat,
            @Query("max_lat") double maxLat,
            @Query("min_lon") double minLon,
            @Query("max_lon") double maxLon,
            @Query("id") long userID,
            @Query("token") String token );
    @GET("/events/get_between")
    Single<PojoEventsForMap> mapEventsPublic(
            @Query("min_lat") double minLat,
            @Query("max_lat") double maxLat,
            @Query("min_lon") double minLon,
            @Query("max_lon") double maxLon);
}
