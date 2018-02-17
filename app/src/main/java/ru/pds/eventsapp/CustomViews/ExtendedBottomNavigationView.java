package ru.pds.eventsapp.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.internal.BaselineLayout;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Field;

import ru.pds.eventsapp.R;

/**
 * Created by Alexey on 07.02.2018.
 */

public final class ExtendedBottomNavigationView extends BottomNavigationView {
    private final Context context;
    private Typeface fontFace = null;

    public ExtendedBottomNavigationView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom){
        super.onLayout(changed, left, top, right, bottom);
        final ViewGroup bottomMenu = (ViewGroup)getChildAt(0);
        final int bottomMenuChildCount = bottomMenu.getChildCount();
        BottomNavigationItemView item;
        View itemTitle;

        if(fontFace == null)
            fontFace = Typeface.createFromAsset(context.getAssets(), "fonts/proba.otf");

        for(int i=0; i<bottomMenuChildCount; i++){
            item = (BottomNavigationItemView)bottomMenu.getChildAt(i);
            //item.setChecked(true);
            itemTitle = item.getChildAt(1);
            ((TextView)((BaselineLayout) itemTitle).getChildAt(0)).setTypeface(fontFace, Typeface.NORMAL);
            ((TextView)((BaselineLayout) itemTitle).getChildAt(1)).setTypeface(fontFace, Typeface.NORMAL);
        }
    }
}
