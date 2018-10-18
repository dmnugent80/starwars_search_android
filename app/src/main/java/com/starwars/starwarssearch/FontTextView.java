package com.starwars.starwarssearch;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;


public class FontTextView extends AppCompatTextView {
    private static final String TAG = FontTextView.class.getSimpleName();
    public FontTextView(Context context) {
        this(context, null);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (isInEditMode())
            return;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FontText);

        if (ta != null) {
            String fontAsset = ta.getString(R.styleable.FontText_typefaceAsset);
            if (!TextUtils.isEmpty(fontAsset)) {
                Typeface tf = FontManager.getInstance().getFont(fontAsset);
                int style = Typeface.NORMAL;

                if (getTypeface() != null) {
                    style = getTypeface().getStyle();
                }

                if (tf != null) {
                    setTypeface(tf, style);
                } else {
                    Log.d(TAG, String.format("Could not create a font from asset: %s", fontAsset));
                }
            }
        }
    }
}
