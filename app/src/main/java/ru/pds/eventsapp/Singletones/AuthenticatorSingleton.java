package ru.pds.eventsapp.Singletones;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import ru.pds.eventsapp.Helpers.RxBus;
import ru.pds.eventsapp.Models.LoginModel;

/**
 * Created by Alexey on 09.02.2018.
 */
public class AuthenticatorSingleton {
    private static AuthenticatorSingleton ourInstance = new AuthenticatorSingleton();
    public static AuthenticatorSingleton getInstance() {
        return ourInstance;
    }


    public LoginModel currentUser;

    public String accessToken;
    private String integrationID;
    private String integrationType;


    public void saveLogin(Context ctx, String id, String accessToken,String type, LoginModel loginModel){
        currentUser = loginModel;
        SharedPreferences sharedPref = ctx.getSharedPreferences("LoginData",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("access_token",accessToken);
        editor.putString("integration_id",id);
        editor.putString("integration_type",type);
        editor.commit();


        this.accessToken = accessToken;
        integrationID = id;
        integrationType = type;

    }
    public void deleteLogin(Context ctx){
        currentUser = null;
        SharedPreferences sharedPref = ctx.getSharedPreferences("LoginData",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("access_token","");
        editor.putString("integration_id","");
        editor.putString("integration_type","");
        editor.commit();
    }

    public void silentLogin(final Context ctx){
        final SharedPreferences sp = ctx.getSharedPreferences("LoginData",Context.MODE_PRIVATE);
        accessToken = sp.getString("access_token","");
        integrationID = sp.getString("integration_id","");
        integrationType = sp.getString("integration_type","");

        Consumer<LoginModel> callback =  new Consumer<LoginModel>() {
            @Override
            public void accept(@NonNull LoginModel loginModel) throws Exception {
                RxBus.instanceOf().logged(new Object());
                saveLogin(ctx, integrationID, accessToken,integrationType, loginModel);
            }
        };
        Consumer<Throwable> onError = new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                if(throwable.getClass()==java.io.EOFException.class)
                    deleteLogin(ctx);
            }
        };

        switch (integrationType){
            case("VK"):{
                WalkerApi.getInstance().loginVK(integrationID,accessToken).subscribe(callback,onError);
                break;
            }
            case("Google"):{
                new WalkerApi().loginVK(integrationID,accessToken).subscribe(callback,onError);
            }
            case("FB"):{
                new WalkerApi().loginVK(integrationID,accessToken).subscribe(callback,onError);
            }
            default:
                return;
        }

    }

}
