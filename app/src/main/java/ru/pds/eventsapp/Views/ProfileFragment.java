package ru.pds.eventsapp.Views;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ru.pds.eventsapp.R;
import ru.pds.eventsapp.BR;

import com.stfalcon.androidmvvmhelper.mvvm.fragments.BindingFragment;

import ru.pds.eventsapp.ViewModels.ProfileFragmentVM;
import ru.pds.eventsapp.databinding.FragmentProfileBinding;


/**
 * Created by Alexey on 15.10.2017.
 */
public class ProfileFragment extends BindingFragment<ProfileFragmentVM, FragmentProfileBinding> {

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    protected ProfileFragmentVM onCreateViewModel(FragmentProfileBinding binding) {
        return new ProfileFragmentVM(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getViewModel().vkClient.onActivityResult(requestCode, resultCode, data);
        getViewModel().googleClient.onActivityResult(requestCode,resultCode,data);
        getViewModel().facebookClient.onActivityResult(requestCode,resultCode,data);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tx = getBinding().textView2;
        Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(),  "fonts/kaushan.ttf");
        tx.setTypeface(custom_font);

    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_profile;
    }

}