package ru.pds.eventsapp.Views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TabHost;
import android.widget.Toast;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static ProfileAuthorizedFragment newInstance() {
        return new ProfileAuthorizedFragment();
    }

    void configureScreenAndMenu() {
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
    }

    void configureViewPager() {
        getBinding().vp.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Fragment getItem(int position) {
                return ProfileEventsFragment.newInstance(position);
            }

        });
        getBinding().vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int pos = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0)
                    getBinding().addEventConatiner.setX(getBinding().vp.getWidth() - positionOffsetPixels - getBinding().addEventConatiner.getWidth());
            }

            @Override
            public void onPageSelected(int position) {
                pos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0)
                    if (pos == 0)
                        getBinding().addEventConatiner.setX(getBinding().vp.getWidth() - getBinding().addEventConatiner.getWidth());
                    else
                        getBinding().addEventConatiner.setX(getBinding().vp.getWidth());
            }
        });

    }

    void configureTab() {
        getBinding().ntsTop.setViewPager(getBinding().vp, 1);
        getBinding().addEventConatiner.post(new Runnable() {
            @Override
            public void run() {
                if (getBinding().ntsTop.getTabIndex() != 0)
                    getBinding().addEventConatiner.setX(getBinding().vp.getWidth());
                else
                    getBinding().addEventConatiner.setX(getBinding().vp.getWidth()-getBinding().addEventConatiner.getWidth());
            }
        });
    }

    void configureFab() {
        getBinding().addEventFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder popup = new AlertDialog.Builder(getActivity());
                final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.new_event_dialog, null);

                popup.setTitle("Новое мероприятие")
                        .setView(dialogView)
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("СОЗДАТЬ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent act = new Intent(getActivity(), EventActivity.class);
                                if (((EditText) dialogView.findViewById(R.id.name)).getText() == null || ((EditText) dialogView.findViewById(R.id.name)).getText().length() < 4) {
                                    Toast.makeText(getContext(), "Длина имени должна быть не менее 4 символов", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Bundle extras = new Bundle();
                                extras.putString("newEventName", ((EditText) dialogView.findViewById(R.id.name)).getText().toString());
                                extras.putString("newGroupName", ((EditText) dialogView.findViewById(R.id.group)).getText().toString());
                                extras.putBoolean("eventAccepted", true);
                                act.putExtras(extras);
                                startActivity(act);
                            }
                        });
                popup.create().show();
            }
        });

    }

    @Override
    protected ProfileAuthorizedFragmentVM onCreateViewModel(FragmentProfileAuthorizedBinding binding) {

        ProfileAuthorizedFragmentVM viewModel = new ProfileAuthorizedFragmentVM(this);

        configureScreenAndMenu();
        configureViewPager();
        configureFab();
        configureTab();

        viewModel.avatarListener = new Runnable() {
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


        return viewModel;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        getViewModel().updateUI();
        if (getBinding().ntsTop.getTabIndex() != 0)
            getBinding().addEventConatiner.setX(getBinding().vp.getWidth());
        else
            getBinding().addEventConatiner.setX(getBinding().vp.getWidth()-getBinding().addEventConatiner.getWidth());
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