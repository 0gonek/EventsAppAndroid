package ru.pds.eventsapp.ViewModels;

import android.content.Intent;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import ru.pds.eventsapp.Singletones.AuthenticatorSingleton;
import ru.pds.eventsapp.Views.NavigationActivity;

public class NavigationActivityVM extends ActivityViewModel<NavigationActivity> {

    public NavigationActivityVM(NavigationActivity activity) {
        super(activity);

        AuthenticatorSingleton.getInstance().silentLogin(getActivity());

    }

}