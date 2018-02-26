package ru.pds.eventsapp.Views;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
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

import ru.pds.eventsapp.CustomViews.EnterSharedElementCallback;
import ru.pds.eventsapp.CustomViews.TextSizeTransition;
import ru.pds.eventsapp.R;
import ru.pds.eventsapp.BR;

import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import ru.pds.eventsapp.ViewModels.EventActivityVM;
import ru.pds.eventsapp.databinding.ActivityEventBinding;


/**
 * Created by Alexey on 21.02.2018.
 */

public class EventActivity extends BindingActivity<ActivityEventBinding, EventActivityVM> {


    boolean editMode = false;

    public String getMonth(int month) {
        return new DateFormatSymbols().getShortMonths()[month];
    }

    @Override
    public EventActivityVM onCreate() {

        final EventActivity lel = this;
        getBinding().toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getBinding().eventName.setText(getIntent().getStringExtra("eventName"));
        getBinding().eventDesc.setText(getIntent().getStringExtra("eventDesc"));
        getBinding().eventNameEdit.setText(getIntent().getStringExtra("eventName"));
        getBinding().descEdit.setText(getIntent().getStringExtra("eventDesc"));

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getBinding().descEdit.clearFocus();

        getBinding().whenEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar date = Calendar.getInstance();

                DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        getBinding().whenEdit.setText(i2+" "+getMonth(i1)+", "+i+" г.");
                        getBinding().whenTime.clearFocus();
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(view.getContext(),callback,date.get(Calendar.YEAR),date.get(Calendar.MONTH),date.get(Calendar.DAY_OF_MONTH));
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
                        getBinding().untilEdit.setText(i2+" "+getMonth(i1)+"., "+i+" г.");
                        getBinding().untilEdit  .clearFocus();
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(view.getContext(),callback,date.get(Calendar.YEAR),date.get(Calendar.MONTH),date.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
        getBinding().whenTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar date = Calendar.getInstance();

                TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        getBinding().whenTime.setText((i<10?"0":"")+i+":"+(i1<10?"0":"")+i1);
                        getBinding().whenTime.clearFocus();
                    }
                };
                TimePickerDialog dialog = new TimePickerDialog(view.getContext(),callback,date.get(Calendar.HOUR_OF_DAY),date.get(Calendar.MINUTE),true);
                dialog.show();
            }
        });


        getBinding().untilTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar date = Calendar.getInstance();

                TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        getBinding().untilTime.setText((i<10?"0":"")+i+":"+(i1<10?"0":"")+i1);
                        getBinding().untilTime.clearFocus();
                    }
                };
                TimePickerDialog dialog = new TimePickerDialog(view.getContext(),callback,date.get(Calendar.HOUR_OF_DAY),date.get(Calendar.MINUTE),true);
                dialog.show();
            }
        });

        getBinding().fab.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                editMode = !editMode;
                //view.setBackgroundColor(editMode ? Color.BLUE : Color.GREEN);
                view.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(),editMode?R.color.colorReturn:R.color.colorBackground,null)));
                getBinding().fab.setImageResource(editMode?R.drawable.ic_undo_white_24dp:R.drawable.ic_mode_edit_white_24dp);


                int x = (int)getBinding().fab.getX();
                int y = (int)getBinding().fab.getY();
                int startRadius = 0;
                int endRadius = (int) Math.hypot(x, getBinding().layoutContent.getHeight()-y);


                if(editMode) {

                    Animator anim = ViewAnimationUtils.createCircularReveal(getBinding().editLayout, x, y, startRadius, endRadius);
                    getBinding().editLayout.setVisibility(View.VISIBLE);

                    anim.start();

                }else{

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

            }
        });


        createTransition();

        return new EventActivityVM(this);
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