package ru.pds.eventsapp.Views;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TabHost;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.blurry.Blurry;
import ru.pds.eventsapp.Helpers.RxBus;
import ru.pds.eventsapp.R;
import ru.pds.eventsapp.BR;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stfalcon.androidmvvmhelper.mvvm.fragments.BindingFragment;
import com.stfalcon.androidmvvmhelper.mvvm.fragments.FragmentViewModel;

import java.io.InputStream;

import ru.pds.eventsapp.Singletones.AuthenticatorSingleton;
import ru.pds.eventsapp.ViewModels.ProfileAuthorizedFragmentVM;
import ru.pds.eventsapp.ViewModels.ProfileEventsFragmentVM;
import ru.pds.eventsapp.databinding.FragmentProfileAuthorizedBinding;


/**
 * Created by Alexey on 15.02.2018.
 */
public class ProfileAuthorizedFragment extends BindingFragment<ProfileAuthorizedFragmentVM, FragmentProfileAuthorizedBinding> {
    public ProfileAuthorizedFragment() {
        // Required empty public constructor
    }

    public static ProfileAuthorizedFragment newInstance() {
        return new ProfileAuthorizedFragment();
    }

    @Override
    protected ProfileAuthorizedFragmentVM onCreateViewModel(FragmentProfileAuthorizedBinding binding) {
        return new ProfileAuthorizedFragmentVM(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getBinding().scrollView.setFillViewport(true);
        getBinding().scrollView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        getBinding().ntsTop.setTitles("МОИ", "БУДУЩИЕ", "ПРОШЕДШИЕ");
        getBinding().ntsTop.setAnimationDuration(100);
        getBinding().ntsTop.setStripFactor(1f);


        getBinding().menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);

                // This activity implements OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_refresh:
                                getViewModel().refresh();
                                return true;
                            case R.id.action_change_name:
                                getViewModel().changeName();
                                return true;
                            case R.id.action_logout:
                                getViewModel().logout();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.inflate(R.menu.profile_menu);
                popup.show();
            }
        });


        getBinding().vp.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Fragment getItem(int position) {
                return ProfileEventsFragment.newInstance(position);
            }

        });

        getBinding().ntsTop.setViewPager(getBinding().vp, 2);

        /*getBinding().appBarImage.post(new Runnable() {
            @Override
            public void run() {
                Blurry.with(getContext())
                        .radius(10)
                        .sampling(8)
                        .color(Color.argb(20, 0, 0, 0))
                        .async()
                        .capture(getBinding().appBarImage)
                        .into(getBinding().appBarImage);
            }
        });*/

        getViewModel().avatarListener = new Runnable() {
            @Override
            public void run() {
                Picasso.with(getContext())
                        .load(AuthenticatorSingleton.getInstance().currentUser.avatar)
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                        .centerInside()
                        .fit()
                        .into(getBinding().avatar);


                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        Blurry.with(getContext())
                                .radius(4)
                                .sampling(1)
                                .color(Color.argb(20, 0, 0, 0))
                                .async()
                                .from(bitmap)
                                .into(getBinding().appBarImage);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Log.d("123", "123");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                Picasso.with(getContext())
                        .load(AuthenticatorSingleton.getInstance().currentUser.avatar)
                        .into(target);

            }
        };

    }

    @Override
    public void onResume() {
        super.onResume();
        getViewModel().updateUI();
    }

    public void updateAvatar(Bitmap img) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (96 * scale + 0.5f);
        getBinding().avatar.setImageBitmap(img);
        getBinding().avatar.getLayoutParams().height = pixels;
        getBinding().avatar.getLayoutParams().width = pixels;
        Blurry.with(getContext())
                .radius(10)
                .sampling(8)
                .color(Color.argb(20, 0, 0, 0))
                .async()
                .from(img)
                .into(getBinding().appBarImage);

    }


    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_profile_authorized;
    }

}