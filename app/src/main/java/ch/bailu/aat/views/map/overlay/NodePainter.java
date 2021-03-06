package ch.bailu.aat.views.map.overlay;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;

import ch.bailu.aat.helpers.AppDensity;

public class NodePainter {
    private static final int STROKE_WIDTH=MapCanvas.EDGE_WIDTH;
    private static final int RADIUS=5;


    public static BitmapDrawable createNode(AppDensity res, Resources r) {
        final int color = Color.WHITE;


        int stroke_width=res.toDPi(STROKE_WIDTH);
        int radius = res.toDPi(RADIUS);
        int hsize = (radius+ stroke_width);
        int size = hsize * 2;


        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint stroke = MapCanvas.createEdgePaint();
        stroke.setAntiAlias(true);

        Paint fill = new Paint();
        fill.setAntiAlias(false);
        fill.setStyle(Style.FILL);
        fill.setColor(color);
        
        canvas.drawCircle(hsize,hsize, radius, fill);
        canvas.drawCircle(hsize, hsize, radius, stroke);

        
        return new BitmapDrawable(r, bitmap);
    }
}
