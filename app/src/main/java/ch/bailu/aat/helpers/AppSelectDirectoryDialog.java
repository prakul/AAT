package ch.bailu.aat.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.io.File;

import ch.bailu.aat.R;
import ch.bailu.aat.helpers.file.FileUI;

public class AppSelectDirectoryDialog  implements  DialogInterface.OnClickListener {
    private final File file;
    private final File directories[];


    private final Context context;
    public AppSelectDirectoryDialog (Context c, File f) {
        context=c;
        file=f;
        directories = new File[] {
                AppDirectory.getTrackListDirectory(context, 0), 
                AppDirectory.getTrackListDirectory(context, 1),
                AppDirectory.getTrackListDirectory(context, 2),
                AppDirectory.getTrackListDirectory(context, 3),
                AppDirectory.getTrackListDirectory(context, 4),
                AppDirectory.getDataDirectory(context, AppDirectory.DIR_OVERLAY),
                AppDirectory.getDataDirectory(context, AppDirectory.DIR_IMPORT)
                };



        final String[] names = new String[directories.length];
        
        for (int i=0; i< names.length; i++) names[i]=directories[i].getName();


        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(file.getName() + ": " + context.getString(R.string.file_copy));
        dialog.setItems(names, this);
        dialog.create();
        dialog.show();
    }


    @Override
    public void onClick(DialogInterface dialog, int i) {
        try {
            new FileUI(file).copyTo(context, directories[i]);
        } catch (Exception e) {
            AppLog.e(context, e);
        }
        dialog.dismiss();
    }
}
