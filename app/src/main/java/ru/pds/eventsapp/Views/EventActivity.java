package ru.pds.eventsapp.Views;

import android.Manifest;
import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import ru.pds.eventsapp.CustomViews.EnterSharedElementCallback;
import ru.pds.eventsapp.CustomViews.TextSizeTransition;
import ru.pds.eventsapp.Models.PojoEvent;
import ru.pds.eventsapp.Models.PojoNewEvent;
import ru.pds.eventsapp.R;
import ru.pds.eventsapp.BR;

import com.alium.nibo.models.NiboSelectedPlace;
import com.alium.nibo.placepicker.NiboPlacePickerActivity;
import com.alium.nibo.utils.NiboConstants;
import com.alium.nibo.utils.NiboStyle;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.pds.eventsapp.ViewModels.EventActivityVM;
import ru.pds.eventsapp.databinding.ActivityEventBinding;


/**
 * Created by Alexey on 21.02.2018.
 */

public class EventActivity extends BindingActivity<ActivityEventBinding, EventActivityVM> {


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void eventCreated(Long id) {
        Toast.makeText(this, "Success "+id, Toast.LENGTH_SHORT).show();

        rippleEditOff();
        getViewModel().event.get().id = id;
        getViewModel().fetchEvent();
    }

    public void eventCreateFailed() {
        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
    }

    void configurePickers() {
        getBinding().whenEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar date = Calendar.getInstance();

                DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        getBinding().whenEdit.setText(i2 + " " + getMonth(i1) + ", " + i + " г.");
                        getBinding().whenTime.clearFocus();
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(view.getContext(), callback, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        getBinding().untilEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar date = Calendar.getInstance();

                DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        getBinding().untilEdit.setText(i2 + " " + getMonth(i1) + ", " + i + " г.");
                        getBinding().untilEdit.clearFocus();
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(view.getContext(), callback, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
        getBinding().whenTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar date = Calendar.getInstance();

                TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        getBinding().whenTime.setText((i < 10 ? "0" : "") + i + ":" + (i1 < 10 ? "0" : "") + i1);
                        getBinding().whenTime.clearFocus();
                    }
                };
                TimePickerDialog dialog = new TimePickerDialog(view.getContext(), callback, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true);
                dialog.show();
            }
        });


        getBinding().untilTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar date = Calendar.getInstance();

                TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        getBinding().untilTime.setText((i < 10 ? "0" : "") + i + ":" + (i1 < 10 ? "0" : "") + i1);
                        getBinding().untilTime.clearFocus();
                    }
                };
                TimePickerDialog dialog = new TimePickerDialog(view.getContext(), callback, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true);
                dialog.show();
            }
        });

        getBinding().placeEdit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)){
                    Intent intent = new Intent(view.getContext(), NiboPlacePickerActivity.class);
                    NiboPlacePickerActivity.NiboPlacePickerBuilder config = new NiboPlacePickerActivity.NiboPlacePickerBuilder()
                            .setSearchBarTitle("Выбор места")
                            .setConfirmButtonTitle("Ок?");

                    NiboPlacePickerActivity.setBuilder(config);
                    startActivityForResult(intent, 1);
                }else{
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1337);
                }


            }
        });
    }
    Context _ctx;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            ||hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)){
            Intent intent = new Intent(_ctx, NiboPlacePickerActivity.class);
            NiboPlacePickerActivity.NiboPlacePickerBuilder config = new NiboPlacePickerActivity.NiboPlacePickerBuilder()
                    .setSearchBarTitle("Выбор места")
                    .setConfirmButtonTitle("Ок?");

            NiboPlacePickerActivity.setBuilder(config);
            startActivityForResult(intent, 1);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                NiboSelectedPlace selectedPlace = data.getParcelableExtra(NiboConstants.SELECTED_PLACE_RESULT);

                double latitude = selectedPlace.getLatLng().latitude;
                double longitude = selectedPlace.getLatLng().longitude;
                getBinding().placeEdit.setText(latitude+", "+longitude);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void rippleEditOn() {

        getBinding().fab.setImageResource(R.drawable.ic_check_white_24dp);
        getBinding().fab.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.colorCreate, null)));


        int x = (int) getBinding().fab.getX();
        int y = (int) getBinding().fab.getY();
        int startRadius = 0;
        int endRadius = (int) Math.hypot(x, getBinding().layoutContent.getHeight() - y);

        Animator anim = ViewAnimationUtils.createCircularReveal(getBinding().editLayout, x, y, startRadius, endRadius);
        getBinding().editLayout.setVisibility(View.VISIBLE);

        anim.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void rippleEditOff() {

        getBinding().fab.setImageResource(R.drawable.ic_mode_edit_white_24dp);
        getBinding().fab.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.colorBackground, null)));


        int x = (int) getBinding().fab.getX();
        int y = (int) getBinding().fab.getY();
        int startRadius = 0;
        int endRadius = (int) Math.hypot(x, getBinding().layoutContent.getHeight() - y);


        Animator anim = ViewAnimationUtils.createCircularReveal(getBinding().editLayout, x, y, endRadius, startRadius);

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                getBinding().editLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        anim.start();
    }

    boolean editMode = false;
    boolean createMode = false;


    public String getMonth(int month) {
        return new DateFormatSymbols().getShortMonths()[month];
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public EventActivityVM onCreate() {
        _ctx = this;

        EventActivityVM viewModel = new EventActivityVM(this);

        getBinding().toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if (createMode || !editMode)
                    onBackPressed();
                else {
                    editMode = false;
                    rippleEditOff();
                }

            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getBinding().descEdit.clearFocus();


        if (getIntent().getStringExtra("newEventName") != null) {
            createMode = true;
            editMode = true;

            getBinding().editLayout.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            getBinding().eventNameEdit.setText(getIntent().getStringExtra("newEventName"));
                            getBinding().groupNameEdit.setText(getIntent().getStringExtra("newEventGroup"));
                            rippleEditOn();
                        }
                    });

        } else {


            PojoEvent init = new PojoEvent();

            init.name = getIntent().getStringExtra("eventName");
            init.groupId = 0L;
            init.groupName = getIntent().getStringExtra("eventGroup");
            init.isAccepted = getIntent().getBooleanExtra("eventAccepted", false);
            init.id = getIntent().getLongExtra("eventId", 0);

            viewModel.putInitialData(init);
            viewModel.fetchEvent();

        }

        configurePickers();


        getBinding().fab.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                editMode = !editMode;


                if (createMode) {

                    PojoNewEvent newEvent = collectCreateFields();

                    if (newEvent != null) {
                        createMode = false;
                        getViewModel().createEvent(newEvent);
                        return;
                    }
                    return;
                }
                if (editMode)
                    rippleEditOn();
                else
                    rippleEditOff();

            }
        });


        createTransition();

        return viewModel;
    }

    Long parseDate(String input) {
        try {
            return new SimpleDateFormat("dd MMM, yyyy г. HH:MM").parse(input).getTime();
        } catch (ParseException e) {
            return null;
        }
    }
    void clearErrors(){
        getBinding().whenEditConatiner.setError(null);
        getBinding().untilEditContainer.setError(null);
        getBinding().placeEditContainer.setError(null);
    }
    PojoNewEvent collectCreateFields() {

        clearErrors();

        PojoNewEvent newEvent = new PojoNewEvent();
        newEvent.name = getBinding().eventNameEdit.getText().toString();
        newEvent.date = parseDate(getBinding().whenEdit.getText() + " " + getBinding().whenTime.getText());
        newEvent.groupId=null;



        if (newEvent.date == null) {
            getBinding().whenEditConatiner.setError("Обязательное поле");
            return null;
        }

        newEvent.duration = parseDate(getBinding().untilEdit.getText() + " " + getBinding().untilTime.getText());

        if (newEvent.duration == null) {
            getBinding().untilEditContainer.setError("Обязательное поле");
            return null;
        }
        newEvent.duration -=  newEvent.date;
        if (newEvent.date < Calendar.getInstance().getTime().getTime()) {
            getBinding().whenEditConatiner.setError("Событие должно быть не ранее текущего времени");
            return null;
        }
        if (newEvent.duration < 0) {
            getBinding().untilEditContainer.setError("Окончание должно быть позже начала");
            return null;
        }

        newEvent.description = getBinding().descEdit.getText().toString();
        newEvent.privacy = getBinding().privacyCheckbox.isChecked();

        try {
            newEvent.latitude = Double.parseDouble(getBinding().placeEdit.getText().toString().split(", ")[0]);
            newEvent.longitude = Double.parseDouble(getBinding().placeEdit.getText().toString().split(", ")[1]);
        }catch (Exception e){
            getBinding().placeEditContainer.setError("Обязательное поле");
            return null;
        }
        newEvent.picture = null;

        return newEvent;

    }


    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_event;
    }


    private void createTransition() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setAllowEnterTransitionOverlap(false);
            getWindow().setAllowReturnTransitionOverlap(false);

            Slide explode = new Slide(Gravity.RIGHT);
            explode.setDuration(200);

            explode.excludeTarget(R.id.bottom_navigation, true);
            explode.excludeChildren(R.id.bottom_navigation, true);
            explode.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setExitTransition(explode);
            getWindow().setEnterTransition(explode);


            TransitionSet solyanka = new TransitionSet();
            solyanka.setOrdering(TransitionSet.ORDERING_TOGETHER);
            Transition resize = new TextSizeTransition();
            resize.addTarget(R.id.eventName);

            Transition resize2 = new TextSizeTransition();
            resize2.addTarget(R.id.eventDesc);

            Transition changeBounds = new ChangeBounds();
            changeBounds.addTarget(R.id.eventName);
            resize.addTarget(R.id.eventDesc);

            solyanka.addTransition(resize);
            solyanka.addTransition(resize2);
            solyanka.addTransition(changeBounds);

            setEnterSharedElementCallback(new EnterSharedElementCallback(this, Pair.create(16f, 22f), Pair.create(16f, 14f)));


        }

    }


}