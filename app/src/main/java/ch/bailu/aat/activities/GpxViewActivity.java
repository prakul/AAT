package ch.bailu.aat.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.File;

import ch.bailu.aat.R;
import ch.bailu.aat.description.AverageSpeedDescription;
import ch.bailu.aat.description.CaloriesDescription;
import ch.bailu.aat.description.ContentDescription;
import ch.bailu.aat.description.DateDescription;
import ch.bailu.aat.description.DistanceDescription;
import ch.bailu.aat.description.EndDateDescription;
import ch.bailu.aat.description.MaximumSpeedDescription;
import ch.bailu.aat.description.NameDescription;
import ch.bailu.aat.description.PathDescription;
import ch.bailu.aat.description.PauseDescription;
import ch.bailu.aat.description.TimeDescription;
import ch.bailu.aat.description.TrackSizeDescription;
import ch.bailu.aat.dispatcher.CurrentLocationSource;
import ch.bailu.aat.dispatcher.CustomFileSource;
import ch.bailu.aat.dispatcher.OnContentUpdatedInterface;
import ch.bailu.aat.dispatcher.OverlaySource;
import ch.bailu.aat.dispatcher.TrackerSource;
import ch.bailu.aat.gpx.GpxInformation;
import ch.bailu.aat.gpx.InfoID;
import ch.bailu.aat.helpers.AppLayout;
import ch.bailu.aat.helpers.FileAction;
import ch.bailu.aat.helpers.ToolTip;
import ch.bailu.aat.menus.FileMenu;
import ch.bailu.aat.views.BusyButton;
import ch.bailu.aat.views.ContentView;
import ch.bailu.aat.views.ControlBar;
import ch.bailu.aat.views.MainControlBar;
import ch.bailu.aat.views.description.MultiView;
import ch.bailu.aat.views.description.TrackDescriptionView;
import ch.bailu.aat.views.description.VerticalView;
import ch.bailu.aat.views.graph.DistanceAltitudeGraphView;
import ch.bailu.aat.views.graph.DistanceSpeedGraphView;
import ch.bailu.aat.views.map.OsmInteractiveView;
import ch.bailu.aat.views.map.overlay.CurrentLocationOverlay;
import ch.bailu.aat.views.map.overlay.OsmOverlay;
import ch.bailu.aat.views.map.overlay.control.InformationBarOverlay;
import ch.bailu.aat.views.map.overlay.control.NavigationBarOverlay;
import ch.bailu.aat.views.map.overlay.gpx.GpxDynOverlay;
import ch.bailu.aat.views.map.overlay.gpx.GpxOverlayListOverlay;
import ch.bailu.aat.views.map.overlay.grid.GridDynOverlay;
import ch.bailu.aat.views.preferences.VerticalScrollView;

public class GpxViewActivity extends AbsDispatcher
        implements OnClickListener, OnContentUpdatedInterface {

    private static final String SOLID_KEY=GpxViewActivity.class.getSimpleName();



    private ImageButton        nextView, fileOperation, copyTo;
    private BusyButton         busyButton;
    private MultiView          multiView;
    private OsmInteractiveView map;

    private String fileID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        Uri uri = intent.getData();

        if (uri==null) {

            if (intent.hasExtra(Intent.EXTRA_STREAM)) {
                uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            }
        }




        if (uri != null) {
            fileID = uri.toString();

            final LinearLayout contentView = new ContentView(this);
            contentView.addView(createButtonBar());
            multiView = createMultiView();
            contentView.addView(multiView);
            setContentView(contentView);
            createDispatcher();
        }


    }



    private ControlBar createButtonBar() {
        MainControlBar bar = new MainControlBar(getServiceContext());

        nextView = bar.addImageButton(R.drawable.go_next_inverse);
        copyTo = bar.addImageButton(R.drawable.document_save_as_inverse);

        fileOperation = bar.addImageButton(R.drawable.edit_select_all_inverse);

        ToolTip.set(copyTo, R.string.file_copy);
        ToolTip.set(fileOperation, R.string.tt_menu_file);

        busyButton = bar.getMenu();
        busyButton.startWaiting();

        bar.setOrientation(AppLayout.getOrientationAlongSmallSide(this));
        bar.setOnClickListener1(this);
        return bar;
    }


    private MultiView createMultiView() {
        map = new OsmInteractiveView(getServiceContext(), SOLID_KEY);

        final OsmOverlay overlayList[] = {
                new GpxOverlayListOverlay(map, getServiceContext()),
                new GpxDynOverlay(map, getServiceContext(), InfoID.FILEVIEW),
                new CurrentLocationOverlay(map),
                new GridDynOverlay(map, getServiceContext()),
                new NavigationBarOverlay(map),
                new InformationBarOverlay(map),

        };
        map.setOverlayList(overlayList);


        final ContentDescription summaryData[] = {
                new NameDescription(this),
                new PathDescription(this),
                new TimeDescription(this),
                new DateDescription(this),
                new EndDateDescription(this),
                new PauseDescription(this),
                new DistanceDescription(this),
                new AverageSpeedDescription(this),
                new MaximumSpeedDescription(this),
                new CaloriesDescription(this),
                new TrackSizeDescription(this),
        };

        VerticalScrollView summary = new VerticalScrollView(this);
        VerticalView graph = new VerticalView(this, SOLID_KEY, InfoID.FILEVIEW,
                new TrackDescriptionView[] {
                        new DistanceAltitudeGraphView(this, SOLID_KEY),
                        new DistanceSpeedGraphView(this, SOLID_KEY)
                });


        MultiView mv = new MultiView(this, SOLID_KEY, InfoID.ALL);
        mv.add(summary, summary.addAllContent(summaryData, InfoID.FILEVIEW));
        mv.addT(map);
        mv.addT(graph);

        return mv;
    }


    private void createDispatcher() {
        addSource(new TrackerSource(getServiceContext()));
        addSource(new CurrentLocationSource(getServiceContext()));
        addSource(new OverlaySource(getServiceContext()));
        addSource(new CustomFileSource(getServiceContext(), fileID));

        addTarget(multiView);
        addTarget(this, InfoID.FILEVIEW);
        addTarget(busyButton.getBusyControl(InfoID.FILEVIEW), InfoID.FILEVIEW);
    }




    @Override
    public void onContentUpdated(GpxInformation info) {
        map.frameBoundingBox(info.getBoundingBox());
    }


    @Override
    public void onClick(View v) {
        if (v == nextView) {
            multiView.setNext();

        } else if (v == copyTo) {
            new FileAction(this, new File(fileID)).copyTo();

        } else if (v == fileOperation) {
            new FileMenu(new FileAction(this, new File(fileID))).showAsPopup(this, fileOperation);
        }

    }
}
