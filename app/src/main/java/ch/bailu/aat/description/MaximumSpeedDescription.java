package ch.bailu.aat.description;


import android.content.Context;

import ch.bailu.aat.R;
import ch.bailu.aat.gpx.GpxInformation;
public class MaximumSpeedDescription  extends SpeedDescription {

    public MaximumSpeedDescription(Context context) {
        super(context);
    }

    @Override
    public String getLabel() {
        return getString(R.string.maximum);
    }

    @Override
    public void onContentUpdated(GpxInformation info) {
        setCache(info.getMaximumSpeed());
    }

}

