package org.mitre.mobilememento;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wes
 */
public enum ScreenType {
    DESKTOP("Desktop", R.drawable.badge_desktop),
    TABLET("Tablet", R.drawable.badge_tablet),
    PHONE("Phone", R.drawable.badge_phone),
    UNKNOWN("Unknown", R.drawable.badge_unknown);

    public static final String DPI_KEY = "DPI_KEY";

    private final String name;
    private final int badgeDrawable;

    private ScreenType(String name, int resId) {
        this.name = name;
        this.badgeDrawable = resId;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() { return name; }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Drawable makeDrawable(Context context) {
        return context.getDrawable(badgeDrawable);
    }

    public static ArrayList<Map<String, ScreenType>> getBuckets() {
        ArrayList<Map<String, ScreenType>> buckets = new ArrayList<Map<String, ScreenType>>();
        for(ScreenType t : values()) {
            Map<String, ScreenType> map = new HashMap<String, ScreenType>();
            map.put(DPI_KEY, t);
            buckets.add(map);
        }

        return buckets;
    }
}
