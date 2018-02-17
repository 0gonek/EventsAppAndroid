package ru.pds.eventsapp.CustomViews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Alexey on 07.02.2018.
 */

public class FontTextView extends TextView {
    public FontTextView(Context context) {
        super(context);

        AssetManager am = context.getApplicationContext().getAssets();
        Typeface typeface = Typeface.createFromAsset(am, "fonts/proba.otf");
        setTypeface(typeface);

    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        AssetManager am = context.getApplicationContext().getAssets();
        Typeface typeface = Typeface.createFromAsset(am, "fonts/proba.otf");
        setTypeface(typeface);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        AssetManager am = context.getApplicationContext().getAssets();
        Typeface typeface = Typeface.createFromAsset(am, "fonts/proba.otf");
        setTypeface(typeface);
    }
    @TargetApi(21)
    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        AssetManager am = context.getApplicationContext().getAssets();
        Typeface typeface = Typeface.createFromAsset(am, "fonts/proba.otf");
        setTypeface(typeface);
    }
}
