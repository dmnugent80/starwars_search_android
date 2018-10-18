package com.starwars.starwarssearch;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

public class FontManager {
    private static final String TAG = FontManager.class.getSimpleName();
    private static FontManager sInstance;
    private AssetManager mAssetManager;
    private Map<String, Typeface> mFonts;

    private FontManager(AssetManager assetManager) {
        mAssetManager = assetManager;
        mFonts = new HashMap<>();
    }

    public static void init(AssetManager mgr) {
        sInstance = new FontManager(mgr);
    }

    public static FontManager getInstance() {
        if (sInstance == null) {
            AssetManager assetManager = App.getContext().getAssets();
            init(assetManager);
        }
        return sInstance;
    }

    public Typeface getFont(String asset) {

        if (mFonts.containsKey(asset)) {
            return mFonts.get(asset);
        }

        Typeface font = null;

        try {
            font = Typeface.createFromAsset(mAssetManager, asset);
            mFonts.put(asset, font);
        } catch (Exception e) {

        }

        if (font == null) {
            try {
                String fixedAsset = fixAssetFilename(asset);
                font = Typeface.createFromAsset(mAssetManager, fixedAsset);
                mFonts.put(asset, font);
                mFonts.put(fixedAsset, font);
            } catch (Exception e) {
            }
        }

        return font;
    }

    private String fixAssetFilename(String asset) {
        // Empty font filename?
        // Just return it. We can't help.
        if (asset.isEmpty())
            return asset;

        // Make sure that the font ends in '.ttf'
        if (!asset.endsWith(".ttf")) {
            asset = String.format("%s.ttf", asset);
        }

        return asset;
    }
}
