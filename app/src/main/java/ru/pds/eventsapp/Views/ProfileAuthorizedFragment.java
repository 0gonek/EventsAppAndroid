package ru.pds.eventsapp.Views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
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
import ru.pds.eventsapp.R;
import ru.pds.eventsapp.BR;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.stfalcon.androidmvvmhelper.mvvm.fragments.BindingFragment;

import java.io.InputStream;

import ru.pds.eventsapp.Singletones.AuthenticatorSingleton;
import ru.pds.eventsapp.ViewModels.ProfileAuthorizedFragmentVM;
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

        getBinding().ntsTop.setTitles("МОИ", "БУДУЩИЕ", "ПРОШЕДШИЕ");
        getBinding().ntsTop.setAnimationDuration(100);
        getBinding().ntsTop.setStripFactor(1f);
        getBinding().vp.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view.equals(object);
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                final View view;

                switch (position) {
                    case 0:
                        view = new View(getContext());
                        break;
                    case 1:
                        view = new View(getContext());
                        break;
                    case 2:
                        view = new View(getContext());
                        break;
                    default:
                        view = null;
                }

                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

        });
        getBinding().ntsTop.setViewPager(getBinding().vp, 2);


        Observable<Bitmap> loadImg = new Observable<Bitmap>() {
            @Override
            protected void subscribeActual(Observer<? super Bitmap> observer) {
                try {
                    InputStream in = new java.net.URL(AuthenticatorSingleton.getInstance().currentUser.avatar).openStream();
                    Bitmap avatar = BitmapFactory.decodeStream(in);

                    if(avatar!=null) {
                        observer.onNext(avatar);
                        observer.onComplete();
                    }

                } catch (Exception e) {
                    observer.onError(e);
                }
            }
        };
        loadImg.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                               @Override
                               public void accept(@NonNull Bitmap bitmap) throws Exception {
                                    getBinding().avatar.setImageBitmap(bitmap);
                                   getBinding().avatar.getLayoutParams().height = 400;
                                   getBinding().avatar.getLayoutParams().width = 400;
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {

                            }
                        });
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