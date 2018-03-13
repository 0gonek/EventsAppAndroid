package ru.pds.eventsapp.Views;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import ru.pds.eventsapp.BR;
import ru.pds.eventsapp.Models.PojoGroup;
import ru.pds.eventsapp.Models.PojoNewGroup;
import ru.pds.eventsapp.R;
import ru.pds.eventsapp.Singletones.AuthenticatorSingleton;
import ru.pds.eventsapp.ViewModels.GroupActivityVM;
import ru.pds.eventsapp.databinding.ActivityGroupBinding;


/**
 * Created by Alexey on 03.03.2018.
 */

public class GroupActivity extends BindingActivity<ActivityGroupBinding, GroupActivityVM> implements View.OnClickListener {


    Context _ctx;
    boolean editMode = false;
    boolean createMode = false;

    Bitmap imageBeforeEdit = null;

    public void groupCreated(Long id) {
        getViewModel().group.set(new PojoGroup());
        getViewModel().group.get().id = id;
        rippleEditOff();
        getViewModel().fetchGroup();
    }

    public void exitEditMode(Boolean exit) {
        editMode = !exit;
        if (exit)
            rippleEditOff();
    }

    public void groupCreateFailed() {
        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
    }

    byte[] newImageToBytes(){
        ImageView imageView = getBinding().groupPic;
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        final int maxSize = 480;
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();
        if(inWidth > inHeight){
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
        try { baos.close(); } catch (IOException e) { e.printStackTrace(); }
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

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

        final Activity activity = this;
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                Picasso.with(activity).load(imageFile).into(getBinding().groupPic);
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
            getBinding().groupPic.setImageBitmap(imageBeforeEdit);
            rippleEditOff();
        }
    }

    void fillEditFields() {
        if (editMode && !createMode) {
            getBinding().groupNameEdit.setText(getBinding().groupName.getText().toString());
            getBinding().descEdit.setText(getBinding().groupDesc.getText());
        }
    }

    void rippleEditOn() {
        try {
            imageBeforeEdit = ((BitmapDrawable)getBinding().groupPic.getDrawable()).getBitmap();
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
        }catch (Exception e){}
    }

    void rippleEditOff() {

        configureFab();
        imageBeforeEdit=null;

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
            if (getViewModel().group.get().accepted==null||!getViewModel().group.get().accepted) {
                getBinding().fab.setImageResource(R.drawable.ic_check_white_24dp);
                getBinding().fab.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.colorCreate, null)));
            } else {
                getBinding().fab.setImageResource(R.drawable.ic_dialog_close_dark);
                getBinding().fab.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.colorRed, null)));
            }
        }

    }

    PojoNewGroup collectCreateFields() {

        PojoNewGroup newGroup = new PojoNewGroup();

        newGroup.name = getBinding().groupNameEdit.getText().toString();
        newGroup.description = getBinding().descEdit.getText().toString();

        newGroup.picture = newImageToBytes();

        return newGroup;

    }

    private boolean canEdit() {

        if (getViewModel().group.get() != null && getViewModel().group.get().ownerId != null)
            return getViewModel().group.get().ownerId == AuthenticatorSingleton.getInstance().currentUser.serverID;
        else
            return false;
    }

    @Override
    public void onClick(View view) {
        editMode = !editMode;

        if (createMode) {

            PojoNewGroup newGroup = collectCreateFields();

            if (newGroup != null) {
                createMode = false;
                getViewModel().createGroup(newGroup);
                return;
            }
            return;
        }
        if (canEdit() && !editMode) {
            PojoNewGroup newGroup = collectCreateFields();

            if (newGroup != null) {
                getViewModel().changeGroup(newGroup);
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
            if (getViewModel().group.get().accepted!=null&getViewModel().group.get().accepted)
                getViewModel().unsubscribe();
            else
                getViewModel().subscribe();
        }


    }


    @Override
    public GroupActivityVM onCreate() {
        _ctx = this;

        GroupActivityVM viewModel = new GroupActivityVM(this);
        viewModel.configureFab.observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                configureFab();
            }
        });
        viewModel.updatePicture.observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String o) throws Exception {
                Picasso.with(GroupActivity.this)
                        .load(o)
                        .placeholder(R.drawable.ic_loading_placeholder)
                        .error(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .into(getBinding().groupPic);
            }
        });


        getBinding().groupPic.setOnClickListener(new View.OnClickListener() {
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


        if (getIntent().getStringExtra("newGroupName") != null) {
            createMode = true;
            editMode = true;

            getBinding().editLayout.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            getBinding().groupNameEdit.setText(getIntent().getStringExtra("newGroupName"));
                            rippleEditOn();
                        }
                    });

        } else {
            PojoGroup init = new PojoGroup();

            init.name = getIntent().getStringExtra("groupName");
            init.id = getIntent().getLongExtra("groupId", 0);
            init.accepted = false;
            viewModel.putData(init);
            viewModel.fetchGroup();

        }

        getBinding().fab.setOnClickListener(this);

        getBinding().exitButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent a = new Intent(GroupActivity.this, ParticipantsActivity.class);
                        Bundle b = new Bundle();
                        b.putLong("id",getViewModel().group.get().id);
                        b.putBoolean("isGroup",true);
                        a.putExtras(b);
                        startActivity(a);
                    }
                }
        );


        getBinding().createGroupEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder popup = new AlertDialog.Builder(GroupActivity.this);
                final View dialogView = LayoutInflater.from(GroupActivity.this).inflate(R.layout.new_event_dialog, null);

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
                                Intent act = new Intent(GroupActivity.this, EventActivity.class);
                                if (((EditText) dialogView.findViewById(R.id.name)).getText() == null || ((EditText) dialogView.findViewById(R.id.name)).getText().length() < 4) {
                                    Toast.makeText(GroupActivity.this, "Длина имени должна быть не менее 4 символов", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Bundle extras = new Bundle();
                                extras.putString("newEventName", ((EditText) dialogView.findViewById(R.id.name)).getText().toString());
                                extras.putLong("newGroupId", getViewModel().group.get().id);
                                extras.putString("newGroupName", getViewModel().group.get().name);
                                extras.putBoolean("eventAccepted", true);
                                act.putExtras(extras);
                                startActivity(act);
                            }
                        });
                popup.create().show();
            }
        });

        return viewModel;
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_group;
    }

}