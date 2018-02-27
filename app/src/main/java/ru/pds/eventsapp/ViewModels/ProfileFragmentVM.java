package ru.pds.eventsapp.ViewModels;

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.stfalcon.androidmvvmhelper.mvvm.fragments.FragmentViewModel;
import com.stfalcon.socialauthhelper.fb.FacebookClient;
import com.stfalcon.socialauthhelper.fb.model.FacebookProfile;
import com.stfalcon.socialauthhelper.gplus.GooglePlusClient;
import com.stfalcon.socialauthhelper.gplus.model.GooglePlusProfile;
import com.stfalcon.socialauthhelper.vk.VkClient;
import com.stfalcon.socialauthhelper.vk.data.model.profile.VkProfile;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import ru.pds.eventsapp.Helpers.RxBus;
import ru.pds.eventsapp.Models.LoginModel;
import ru.pds.eventsapp.R;
import ru.pds.eventsapp.Singletones.AuthenticatorSingleton;
import ru.pds.eventsapp.Singletones.FragmentsStore;
import ru.pds.eventsapp.Singletones.WalkerApi;
import ru.pds.eventsapp.Views.ProfileFragment;

public class ProfileFragmentVM extends FragmentViewModel<ProfileFragment> {

    public VkClient vkClient;
    public GooglePlusClient googleClient;
    public FacebookClient facebookClient;

    private void onLogin(final String integrationType, Single<LoginModel> event, final String id, final String token) {
        event.subscribe(new Consumer<LoginModel>() {
                            @Override
                            public void accept(@NonNull LoginModel loginModel) throws Exception {
                                Toast.makeText(getFragment().getContext(), "Success", Toast.LENGTH_SHORT).show();
                                AuthenticatorSingleton.getInstance().saveLogin(getFragment().getContext(), id, token, integrationType, loginModel);
                                RxBus.instanceOf().logged(new Object());

                            }
                        },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                        Snackbar error = Snackbar.make(getFragment().getBinding().content, R.string.failed_to_login, Snackbar.LENGTH_SHORT);

                        if (throwable.getClass() != java.io.EOFException.class)
                            error.setText("Нет подключения к серверу!");

                        error.show();
                    }
                });
        //AuthenticatorSingleton.getInstance().saveLogin(getFragment().getContext(), "123", "123", "VK", new LoginModel());

    }

    public void loginVK(View view) {
        vkClient.getProfile(new VkClient.DataLoadedListener<VkProfile>() {
            @Override
            public void onDataLoaded(final VkProfile vkProfile) {
                Toast.makeText(getFragment().getContext(), "Trying to login...", Toast.LENGTH_SHORT).show();

                String token = vkClient.getAccessToken();
                String id = vkProfile.getId() + "";

                onLogin("VK", WalkerApi.getInstance().loginVK(id, token), id, token);
            }
        });
    }

    public void loginGoogle(View view) {
        googleClient.getProfile(new GooglePlusClient.GooglePlusResultCallback() {
            @Override
            public void onProfileLoaded(final GooglePlusProfile googlePlusProfile) {
                Toast.makeText(getFragment().getContext(), "Trying to login...", Toast.LENGTH_SHORT).show();

                String token = googlePlusProfile.getToken();
                String id = googlePlusProfile.getId() + "";

                onLogin("Google", WalkerApi.getInstance().loginVK(id, token), id, token);
            }
        });
    }

    public void loginFacebook(View view) {
        facebookClient.getProfile(new FacebookClient.FbResultCallback() {
            @Override
            public void onProfileLoaded(FacebookProfile facebookProfile) {
                Toast.makeText(getFragment().getContext(), "Trying to login...", Toast.LENGTH_SHORT).show();

                String token = facebookClient.getToken();
                String id = facebookProfile.getId() + "";

                onLogin("FB", WalkerApi.getInstance().loginVK(id, token), id, token);
            }
        });
    }


    public ProfileFragmentVM(ProfileFragment fragment) {
        super(fragment);
        vkClient = new VkClient(this.getFragment(), "https://oauth.vk.com/authorize", "6219519"); //vk application clientId
        googleClient = new GooglePlusClient(this.getActivity(), "AIzaSyBXI8Etbj0XaXcWVd01clBZXiXet-zfKWQ");
        facebookClient = new FacebookClient(this.getActivity());
    }

}