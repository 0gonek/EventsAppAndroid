package ru.pds.eventsapp.Services;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import ru.pds.eventsapp.Models.*;

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

    @GET("/events/get")
    Single<PojoEvent> eventInfo(
            @Query("id") long id,
            @Query("event_id") long eventId,
            @Query("token") String token);

    @POST("/events/change_event")
    Single<Boolean> changeEvent(
            @Body PojoChangeEvent changeModel
    );

    @POST("/events/new")
    Single<Boolean> newEvent(
            @Body PojoNewEvent newModel
    );
}
