package ru.pds.eventsapp.Views;

import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import ru.pds.eventsapp.R;
import ru.pds.eventsapp.BR;

import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

import ru.pds.eventsapp.ViewModels.BrowserActivityVM;
import ru.pds.eventsapp.databinding.ActivityBrowserBinding;
@Deprecated
public class BrowserActivity extends BindingActivity<ActivityBrowserBinding, BrowserActivityVM> {

    @Override
    public BrowserActivityVM onCreate() {

        getActionBar().setDisplayHomeAsUpEnabled(true);

        return new BrowserActivityVM(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_browser;
    }

}