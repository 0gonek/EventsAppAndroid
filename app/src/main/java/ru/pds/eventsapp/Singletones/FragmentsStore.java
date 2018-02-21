package ru.pds.eventsapp.Singletones;

import com.google.android.gms.maps.SupportMapFragment;

import ru.pds.eventsapp.Views.GroupsFragment;
import ru.pds.eventsapp.Views.MapFragment;
import ru.pds.eventsapp.Views.ProfileAuthorizedFragment;
import ru.pds.eventsapp.Views.ProfileFragment;
import ru.pds.eventsapp.Views.SettingsFragment;

/**
 * Created by Alexey on 15.10.2017.
 */
public class FragmentsStore {
    private static FragmentsStore ourInstance = new FragmentsStore();

    public static FragmentsStore getInstance() {
        return ourInstance;
    }

    private MapFragment mapFragment;
    private ProfileFragment profileFragment;
    private GroupsFragment groupsFragment;
    private SettingsFragment settingsFragment;
    private ProfileAuthorizedFragment authorizedFragment;
    private boolean isLogged = false;
    private FragmentsStore() {
        mapFragment = new MapFragment();
        profileFragment = new ProfileFragment();
        groupsFragment = new GroupsFragment();
        settingsFragment = new SettingsFragment();
    }

    public MapFragment getMapFragment() {
        return mapFragment;
    }

    public ProfileFragment getProfileFragment() {
        if(isLogged)
            authorizedFragment = null;
        
        return profileFragment;
    }

    public ProfileAuthorizedFragment getAuthorizedFragment() {

        if (authorizedFragment == null) authorizedFragment = new ProfileAuthorizedFragment();
        return authorizedFragment;
    }

    public GroupsFragment getGroupsFragment() {
        return groupsFragment;
    }

    public SettingsFragment getSettingsFragment() {
        return settingsFragment;
    }
}
