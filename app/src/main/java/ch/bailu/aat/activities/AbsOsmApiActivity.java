package ch.bailu.aat.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.osmdroid.util.BoundingBoxE6;

import java.io.File;
import java.io.IOException;

import ch.bailu.aat.R;
import ch.bailu.aat.dispatcher.CustomFileSource;
import ch.bailu.aat.gpx.GpxInformation;
import ch.bailu.aat.gpx.InfoID;
import ch.bailu.aat.helpers.AbsTextBackup;
import ch.bailu.aat.helpers.AppBroadcaster;
import ch.bailu.aat.helpers.AppDirectory;
import ch.bailu.aat.helpers.AppIntent;
import ch.bailu.aat.helpers.AppLog;
import ch.bailu.aat.helpers.AppTheme;
import ch.bailu.aat.helpers.OsmApiHelper;
import ch.bailu.aat.helpers.ToolTip;
import ch.bailu.aat.services.background.BackgroundService;
import ch.bailu.aat.services.background.DownloadHandle;
import ch.bailu.aat.services.background.ProcessHandle;
import ch.bailu.aat.views.BusyButton;
import ch.bailu.aat.views.ContentView;
import ch.bailu.aat.views.ControlBar;
import ch.bailu.aat.views.MainControlBar;
import ch.bailu.aat.views.NodeListView;
import ch.bailu.aat.views.TagEditor;
import ch.bailu.aat.views.preferences.AddOverlayDialog;


public abstract class AbsOsmApiActivity extends AbsDispatcher implements OnClickListener {
    private static final String SOLID_KEY=AbsOsmApiActivity.class.getSimpleName();


    private TagEditor          tagEditor;
    private BusyButton         download;
    private View               erase;
    private View               saveCopy;
    private View               addLayer;

    private NodeListView       list;

    private OsmApiHelper       osmApi;
    private ProcessHandle      request=ProcessHandle.NULL;

    private final LayoutParams layout =
            new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            osmApi = createUrlGenerator(AppIntent.getBoundingBox(getIntent()));
            AppBroadcaster.register(this, onFileDownloaded, AppBroadcaster.FILE_CHANGED_ONDISK);
        } catch (Exception e) {
            AppLog.e(this,e);
        }
        setContentView(createContentView());

        addSource(new CustomFileSource(getServiceContext(),osmApi.getResultFile().toString()));
        addTarget(list);

        setQueryTextFromIntent();
    }  

    
    private void setQueryTextFromIntent() {
        String query = queryFromIntent(getIntent());
        if (query != null) {
            tagEditor.setText(query);
        }
    }


    public static String queryFromIntent(Intent intent) {
        Uri  uri = intent.getData();
        if (uri != null) return queryFromUri(uri);
        return null;
    }


    public static String queryFromUri(Uri uri) {

        String query = uri.getEncodedQuery();
        if (query != null) {
            Uri n = Uri.parse("http://bailu.ch/query?"+uri.getEncodedQuery()); // we need a hierarchical url
            String query_parameter = n.getQueryParameter("q");
            if (query_parameter != null) {
                query_parameter = query_parameter.replace('\n', ',');
                return query_parameter;
            }
        }
        return null;
    }



    private LinearLayout createContentView()  {
        ContentView view = new ContentView(this);
        ControlBar bar = createControlBar();
        view.addView(bar, layout);        


        TextView urlLabel = new TextView(this);
        urlLabel.setText(osmApi.getUrlStart());
        view.addView(urlLabel, layout);
        AppTheme.themify(urlLabel);

        tagEditor = new TagEditor(this, osmApi.getBaseDirectory());
        view.addView(tagEditor, layout);


        TextView postLabel = new TextView(this);
        postLabel.setText(osmApi.getUrlEnd());
        view.addView(postLabel, layout);
        AppTheme.themify(postLabel);

        list = new NodeListView(getServiceContext(),SOLID_KEY,
                InfoID.FILEVIEW);


        view.addView(list,layout);

        return view;
    }


    private ControlBar createControlBar() {
        ControlBar bar = new MainControlBar(getServiceContext(),6);      

        download = new BusyButton(this, R.drawable.go_bottom_inverse);

        bar.addView(download);
        download.setOnClickListener(this);

        erase = bar.addImageButton(R.drawable.edit_clear_all_inverse);
        addLayer = bar.addImageButton(R.drawable.view_paged_inverse);
        saveCopy = bar.addImageButton(R.drawable.document_save_as_inverse);


        ToolTip.set(download, R.string.tt_nominatim_query);
        ToolTip.set(erase, R.string.tt_nominatim_clear);
        ToolTip.set(addLayer, R.string.tt_nominatim_overlay);
        ToolTip.set(saveCopy, R.string.tt_nominatim_save);

        addButtons(bar);

        bar.setOnClickListener1(this);


        return bar;
    }


    public abstract OsmApiHelper createUrlGenerator(BoundingBoxE6 boundingBox) throws SecurityException, IOException;
    public abstract void addButtons(ControlBar bar);


    @Override
    public void onClick(View v) {
        if (v==download) {
            download();

        } else if (v==erase) {
            tagEditor.erase();

        } else if (v==saveCopy) {
            saveCopy();
        } else if (v==addLayer) {
            new AddOverlayDialog(this,osmApi.getResultFile());
        }


    }


    private void download() {
        try {
            String query = tagEditor.getText();

            BackgroundService.Self background = getServiceContext().getBackgroundService();


            request.stopLoading();
            download.startWaiting();

            request = new ApiQueryHandle(
                    osmApi.getUrl(query), 
                    osmApi.getResultFile(), 
                    query, 
                    osmApi.getQueryFile());

            background.download(request);

        } catch (Exception e) {
            download.stopWaiting();
            request = DownloadHandle.NULL;

            AppLog.e(this, e);
        }
    }



    private final BroadcastReceiver  onFileDownloaded = new BroadcastReceiver () {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AppIntent.hasUrl(intent, request.toString())) {
                download.stopWaiting();
                request = ProcessHandle.NULL;
            }
        }
    };


    private void saveCopy() {
        try {
            final File source = osmApi.getResultFile();
            final File target = getOverlayFile();

            AppDirectory.copyFile(source, target);
            AppBroadcaster.broadcast(
                    this, 
                    AppBroadcaster.FILE_CHANGED_ONDISK, 
                    target.toString(), 
                    source.toString());

        } catch (IOException e) {
            AppLog.e(this, e);
        }
    }


    private File getOverlayFile() throws IOException {
        final String query = AbsTextBackup.read(osmApi.getQueryFile());
        final String prefix = OsmApiHelper.getFilePrefix(query);
        final String extension = osmApi.getFileExtension();
        final File directory = AppDirectory.getDataDirectory(this, AppDirectory.DIR_OVERLAY);

        return AppDirectory.generateUniqueFilePath(directory, prefix, extension);
    }




    @Override
    public void onDestroy() {
        unregisterReceiver(onFileDownloaded);
        super.onDestroy();
    }


    private static class ApiQueryHandle extends DownloadHandle {
        private final String queryString;
        private final File queryFile;

        public ApiQueryHandle(String source, File target, String qs, File qf) {
            super(source, target);
            queryString = qs;
            queryFile   = qf;
        }

        @Override
        public long bgOnProcess() {
            long r=super.bgOnProcess();
            if (r>0) {
                try {
                    AbsTextBackup.write(queryFile, queryString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return r;
        }
    }



}
