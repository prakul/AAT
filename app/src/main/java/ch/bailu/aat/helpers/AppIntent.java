package ch.bailu.aat.helpers;

import android.content.Intent;

import org.osmdroid.util.BoundingBoxE6;

public class AppIntent {
    private static final String EXTRA_FILE="file";
    private static final String EXTRA_URL="source";

    
    
    public static void setUrl(Intent intent, String url) {
        intent.putExtra(EXTRA_URL, url);
    }

    public static boolean hasUrl(Intent intent, String url) {
        return intent.getStringExtra(EXTRA_URL).equals(url);
    }


    public static String getUrl(Intent intent) {
        return intent.getStringExtra(EXTRA_URL);
    }
    public static void setFile(Intent intent, String file) {
        intent.putExtra(EXTRA_FILE, file);
    }

    public static String getFile(Intent intent) {
        return intent.getStringExtra(EXTRA_FILE);
    }

    public static boolean hasFile(Intent intent, String file) {
        return intent.getStringExtra(EXTRA_FILE).equals(file);
    }






    public static void setBoundingBox(Intent intent, BoundingBoxE6 box) {
        intent.putExtra("N", box.getLatNorthE6());
        intent.putExtra("E", box.getLonEastE6());
        intent.putExtra("S", box.getLatSouthE6());
        intent.putExtra("W", box.getLonWestE6());

    }

    public static BoundingBoxE6 getBoundingBox(Intent intent) {
        return new BoundingBoxE6(
                intent.getIntExtra("N",0),
                intent.getIntExtra("E",0),
                intent.getIntExtra("S",0),
                intent.getIntExtra("W",0)
                );
    }

}
