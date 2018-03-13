package ru.pds.eventsapp.Views;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.res.ResourcesCompat;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import ru.pds.eventsapp.CustomViews.EnterSharedElementCallback;
import ru.pds.eventsapp.CustomViews.TextSizeTransition;
import ru.pds.eventsapp.Models.PojoEvent;
import ru.pds.eventsapp.Models.PojoNewEvent;
import ru.pds.eventsapp.R;
import ru.pds.eventsapp.BR;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.games.event.Event;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import ru.pds.eventsapp.Singletones.AuthenticatorSingleton;
import ru.pds.eventsapp.ViewModels.EventActivityVM;
import ru.pds.eventsapp.databinding.ActivityEventBinding;


/**
 * Created by Alexey on 21.02.2018.
 */

public class EventActivity extends BindingActivity<ActivityEventBinding, EventActivityVM> implements View.OnClickListener {

    Context _ctx;
    boolean editMode = false;
    boolean createMode = false;

    Bitmap imageBeforeEdit = null;

    public void eventCreated(Long id) {
        getViewModel().event.set(new PojoEvent());
        getViewModel().event.get().id = id;
        rippleEditOff();
        getViewModel().fetchEvent();
    }

    public void exitEditMode(Boolean exit) {
        editMode = !exit;
        if (exit)
            rippleEditOff();
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
                if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(EventActivity.this), 30);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
                }


            }
        });
    }

    byte[] newImageToBytes() {
        ImageView imageView = getBinding().eventPic;
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        final int maxSize = 480;
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();
        if (inWidth > inHeight) {
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] image = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if ((hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                || hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) && requestCode == 1000) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(EventActivity.this), 30);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
            return;
        }
        if (hasPermission(Manifest.permission.CAMERA) && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && requestCode == 1337)
            pickFromCamera();
        else if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && requestCode == 1338)
            pickFromGallery();
    }

    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
        } else {
            return true;
        }
    }

    void pickFromCamera() {
        final Activity activity = this;
        if (hasPermission(Manifest.permission.CAMERA))
            EasyImage.openCamera(activity, 0);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1337);
        }
    }

    void pickFromGallery() {
        final Activity activity = this;
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            EasyImage.openGallery(activity, 0);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1338);
        }
    }

    void pickImage() {
        if (!editMode)
            return;

        AlertDialog.Builder dialogB = new AlertDialog.Builder(this);
        dialogB.setTitle("Выбрать изображение");
        dialogB.setItems(new CharSequence[]{"Камера", "Галерея"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0: {
                        pickFromCamera();
                        break;
                    }
                    case 1: {
                        pickFromGallery();
                        break;
                    }
                }
            }
        });
        dialogB.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 30) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                double latitude = place.getLatLng().latitude;
                double longitude = place.getLatLng().longitude;
                getBinding().placeEdit.setText(latitude + ", " + longitude);
            }
        }
        final Activity activity = this;
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                Picasso.with(activity).load(imageFile).into(getBinding().eventPic);
            }
        });

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (createMode || !editMode)
            super.onBackPressed();
        else {
            editMode = false;
            getBinding().eventPic.setImageBitmap(imageBeforeEdit);
            rippleEditOff();
        }
    }

    void fillEditFields() {
        if(getViewModel().event.get().groupId==null||getViewModel().event.get().groupId<=0)
            getBinding().privacyCheckbox.setVisibility(View.INVISIBLE);

        if (editMode && !createMode) {
            getBinding().eventNameEdit.setText(getBinding().eventName.getText().toString());
            getBinding().groupNameEdit.setText(getBinding().groupName.getText().toString());
            getBinding().privacyCheckbox.setChecked(getViewModel().event.get().privacy);
            getBinding().descEdit.setText(getBinding().eventDesc.getText());
            getBinding().placeEdit.setText(getViewModel().event.get().latitude + ", " + getViewModel().event.get().longitude);

            String s = getBinding().dateWhen.getText().toString();
            getBinding().whenEdit.setText(s.substring(0, s.length() - 7));
            getBinding().whenTime.setText(s.substring(s.length() - 5, s.length()));

            s = getBinding().dateUntil.getText().toString();
            getBinding().untilEdit.setText(s.substring(0, s.length() - 7));
            getBinding().untilTime.setText(s.substring(s.length() - 5, s.length()));
        }
    }

    void rippleEditOn() {
        try {
            imageBeforeEdit = ((BitmapDrawable) getBinding().eventPic.getDrawable()).getBitmap();

            getBinding().fab.setImageResource(R.drawable.ic_check_white_24dp);
            getBinding().fab.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.colorCreate, null)));

            fillEditFields();

            int x = (int) getBinding().fab.getX();
            int y = (int) getBinding().fab.getY();
            int startRadius = 0;
            int endRadius = (int) Math.hypot(x, getBinding().layoutContent.getHeight() - y);

            Animator anim = ViewAnimationUtils.createCircularReveal(getBinding().editLayout, x, y, startRadius, endRadius);
            getBinding().editLayout.setVisibility(View.VISIBLE);

            anim.start();
        } catch (Exception e) {
        }
    }

    void rippleEditOff() {

        configureFab();
        imageBeforeEdit = null;

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

    void configureFab() {

        if (canEdit()) {
            getBinding().fab.setImageResource(R.drawable.ic_mode_edit_white_24dp);
            getBinding().fab.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.colorBackground, null)));
        } else {
            if (getViewModel().event.get().accepted == null || !getViewModel().event.get().accepted) {
                getBinding().fab.setImageResource(R.drawable.ic_check_white_24dp);
                getBinding().fab.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.colorCreate, null)));
            } else {
                getBinding().fab.setImageResource(R.drawable.ic_dialog_close_dark);
                getBinding().fab.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.colorRed, null)));
            }
        }

    }


    public String getMonth(int month) {
        return new DateFormatSymbols().getShortMonths()[month];
    }

    @Override
    public EventActivityVM onCreate() {
        _ctx = this;

        final EventActivityVM viewModel = new EventActivityVM(this);
        viewModel.configureFab.observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                configureFab();
            }
        });
        viewModel.updatePicture.observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String o) throws Exception {
                Picasso.with(EventActivity.this)
                        .load(o)
                        .placeholder(R.drawable.ic_loading_placeholder)
                        .error(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .into(getBinding().eventPic);
            }
        });


        getBinding().eventPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        getBinding().toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                onBackPressed();

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
                            PojoEvent init = new PojoEvent();

                            init.name = getIntent().getStringExtra("newEventName");
                            init.groupName = getIntent().getStringExtra("newGroupName");
                            init.groupId = getIntent().getLongExtra("newGroupId",0L);
                            init.accepted = false;
                            init.id = getIntent().getLongExtra("eventId", 0);
                            viewModel.putData(init);

                            getBinding().eventNameEdit.setText(init.name);
                            getBinding().groupNameEdit.setText(init.groupName);
                            rippleEditOn();
                        }
                    });

        } else {
            PojoEvent init = new PojoEvent();

            if (getIntent().getStringExtra("eventInfo") != null && getIntent().getStringExtra("eventInfo").length() != 0) {
                init = new GsonBuilder().create().fromJson(getIntent().getStringExtra("eventInfo"), PojoEvent.class);
                viewModel.putData(init);

            } else {
                init.name = getIntent().getStringExtra("eventName");
                init.groupId = getIntent().getLongExtra("eventGroupId",0L);
                init.description = getIntent().getStringExtra("eventDesc");
                init.accepted = getIntent().getBooleanExtra("eventAccepted", false);
                init.id = getIntent().getLongExtra("eventId", 0);
                viewModel.putData(init);
                viewModel.fetchEvent();
            }
        }
        configurePickers();
        getBinding().fab.setOnClickListener(this);

        createTransition();


        getBinding().exitButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent a = new Intent(EventActivity.this, ParticipantsActivity.class);
                        Bundle b = new Bundle();
                        b.putLong("id",getViewModel().event.get().id);
                        b.putBoolean("isGroup",false);
                        a.putExtras(b);
                        startActivity(a);
                    }
                }
        );

        return viewModel;
    }

    Long parseDate(String input) {
        try {
            return new SimpleDateFormat("dd MMM, yyyy г. HH:mm").parse(input).getTime();
        } catch (ParseException e) {
            return null;
        }
    }

    void clearErrors() {
        getBinding().whenEditConatiner.setError(null);
        getBinding().untilEditContainer.setError(null);
        getBinding().placeEditContainer.setError(null);
    }

    PojoNewEvent collectCreateFields() {

        clearErrors();

        PojoNewEvent newEvent = new PojoNewEvent();
        newEvent.name = getBinding().eventNameEdit.getText().toString();
        newEvent.date = parseDate(getBinding().whenEdit.getText() + " " + getBinding().whenTime.getText());
        newEvent.groupId = getViewModel().event.get().groupId;


        if (newEvent.date == null) {
            getBinding().whenEditConatiner.setError("Обязательное поле");
            return null;
        }

        newEvent.duration = parseDate(getBinding().untilEdit.getText() + " " + getBinding().untilTime.getText());

        if (newEvent.duration == null) {
            getBinding().untilEditContainer.setError("Обязательное поле");
            return null;
        }
        newEvent.duration -= newEvent.date;
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
        } catch (Exception e) {
            getBinding().placeEditContainer.setError("Обязательное поле");
            return null;
        }
        newEvent.picture = newImageToBytes();

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
            //getWindow().setAllowReturnTransitionOverlap(false);

            Slide explode = new Slide(Gravity.RIGHT);
            explode.setDuration(150);

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

            //setEnterSharedElementCallback(new EnterSharedElementCallback(this, Pair.create(16f, 22f), Pair.create(16f, 14f)));


        }

    }


    private boolean canEdit() {
       if (getViewModel()!=null&&getViewModel().event.get() != null && getViewModel().event.get().ownerId != null)
            return getViewModel().event.get().ownerId == AuthenticatorSingleton.getInstance().currentUser.serverID;
        else
            return false;
    }

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
        if (canEdit() && !editMode) {
            PojoNewEvent newEvent = collectCreateFields();

            if (newEvent != null) {
                getViewModel().changeEvent(newEvent);
                return;
            }
            editMode = true;
            return;
        }
        if (canEdit())
            if (editMode)
                rippleEditOn();
            else
                rippleEditOff();
        else {
            editMode = false;
            if (getViewModel().event.get().accepted != null && getViewModel().event.get().accepted)
                getViewModel().rejectEvent();
            else
                getViewModel().acceptEvent();
        }
    }

}