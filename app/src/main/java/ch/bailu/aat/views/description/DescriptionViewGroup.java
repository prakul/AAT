package ch.bailu.aat.views.description;

import android.content.Context;

import ch.bailu.aat.description.ContentDescription;
import ch.bailu.aat.gpx.GpxInformation;

public abstract class DescriptionViewGroup extends TrackDescriptionView {

    private ContentDescription[] data;
    private TrackDescriptionView[] view;

    
    public DescriptionViewGroup(Context context, String key, int filter) {
        super(context,key, filter);
    }
    
    public void init(ContentDescription[]d, TrackDescriptionView[] v) {
        data=d; view=v;
    }
    
    public void init(TrackDescriptionView[] v) {
        view=v;
    }
    
    protected int getDescriptionCount() {
        return view.length;
    }
    
    protected ContentDescription getDescription(int index) {
        return data[index];
    }
    
    protected TrackDescriptionView getDescriptionView(int index) {
        return view[index];
    }
    
    @Override
    protected void onMeasure(int w, int h) {
        for (TrackDescriptionView child: view) {
            child.measure(w, h);
        }
        setMeasuredDimension(MeasureSpec.getSize(w),MeasureSpec.getSize(h));
    }
    
    
    @Override
    public void onContentUpdated(GpxInformation info) {
        if (filter.pass(info)) {
            for (TrackDescriptionView aView : view) aView.onContentUpdated(info);
        }   
    }
}
