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

    @GET("/events/get_between_public")
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
    @GET("/events/get_public")
    Single<PojoEvent> eventInfoPublic(
            @Query("event_id") long eventId);

    @POST("/events/change")
    Single<Boolean> changeEvent(
            @Body PojoChangeEvent changeModel
    );

    @POST("/events/new")
    Single<Long> newEvent(
            @Body PojoNewEvent newModel
    );
    @GET("/events/new_participant")
    Single<Boolean> new_participant(
            @Query("id") Long id,
            @Query("event_id") Long eventId,
            @Query("token") String token );

    @GET("/events/delete_participant")
    Single<Boolean> new_participant(
            @Query("id") Long id,
            @Query("participant_id") Long participant_id,
            @Query("event_id") Long eventId,
            @Query("token") String token );

    @GET("/events/search")
    Single<PojoSmallEvents> searchEvents(
            @Query("id") Long userId,
            @Query("token") String token,
            @Query("part") String part
    );

    @GET("/groups/search")
    Single<PojoGroupIdNames> searchGroups(
            @Query("id") Long userId,
            @Query("token") String token,
            @Query("key_word") String part,
            @Query("offset") Integer offset,
            @Query("quantity") Integer quantity
    );

    @GET("/groups/get")
    Single<PojoGroup> groupInfo(
            @Query("id") long id,
            @Query("group_id") long groupId,
            @Query("token") String token);

    @POST("/groups/change")
    Single<Boolean> changeGroup(
            @Body PojoChangeGroup changeModel
    );

    @POST("/groups/new")
    Single<Long> newGroup(
            @Body PojoNewGroup newModel
    );
    @GET("/groups/new_participant")
    Single<Boolean> subscribeGroup(
            @Query("id") Long id,
            @Query("group_id") Long groupId,
            @Query("token") String token );

    @GET("/groups/delete_participant")
    Single<Boolean> unsubscribeGroup(
            @Query("id") Long id,
            @Query("participant_id") Long participant_id,
            @Query("group_id") Long groupId,
            @Query("token") String token );

    @GET("/groups/get_own")
    Single<PojoGroupIdNames> getOwnGroups(
            @Query("id") Long id,
            @Query("token") String token );

    @GET("/groups/get_participants")
    Single<PojoUsersList> getGroupParticipants(
            @Query("id") Long id,
            @Query("token") String token,
            @Query("group_id") Long groupId);
    @GET("/events/get_participants")
    Single<PojoUsersList> getEventParticipants(
            @Query("id") Long id,
            @Query("token") String token,
            @Query("event_id") Long eventId);

}
