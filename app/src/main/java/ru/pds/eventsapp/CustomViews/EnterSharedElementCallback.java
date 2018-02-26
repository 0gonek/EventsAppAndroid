package ru.pds.eventsapp.CustomViews;


import android.annotation.TargetApi;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.pds.eventsapp.R;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class EnterSharedElementCallback extends SharedElementCallback {
    private static final String TAG = "EnterSharedElementCallback";

    private final List<Pair<Float,Float>> mTextSize = new ArrayList<>();

    public EnterSharedElementCallback(Context context, Pair<Float,Float> ...sizeFirstAndSecond) {
        Resources res = context.getResources();

        for (Pair<Float, Float> size : sizeFirstAndSecond)
            mTextSize.add(Pair.create(
                    size.first ,
                    size.second ));

    }

    @Override
    public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {

        for (int i = 0; i < sharedElements.size(); i++) {
            TextView textView = (TextView) sharedElements.get(i);

            // Setup the TextView's start values.
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize.get(i).first);
        }
    }

    @Override
    public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {


        for (int i = 0; i < sharedElements.size(); i++) {
            TextView textView = (TextView) sharedElements.get(i);

            // Record the TextView's old width/height.
            int oldWidth = textView.getMeasuredWidth();
            int oldHeight = textView.getMeasuredHeight();

            // Setup the TextView's end values.
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize.get(i).second);

            // Re-measure the TextView (since the text size has changed).
            int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            textView.measure(widthSpec, heightSpec);

            // Record the TextView's new width/height.
            int newWidth = textView.getMeasuredWidth();
            int newHeight = textView.getMeasuredHeight();

            // Layout the TextView in the center of its container, accounting for its new width/height.
            int widthDiff = newWidth - oldWidth;
            int heightDiff = newHeight - oldHeight;
            textView.layout(0, textView.getTop() - heightDiff ,
                    textView.getRight() + widthDiff / 2, textView.getBottom() + heightDiff / 2);
        }



    }
}