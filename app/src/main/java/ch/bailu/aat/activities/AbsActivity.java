package ch.bailu.aat.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ch.bailu.aat.helpers.AppLog;
import ch.bailu.aat.helpers.file.FileIntent;
import ch.bailu.aat.preferences.PreferenceLoadDefaults;

public abstract class AbsActivity extends Activity {
    private AppLog logger;

    private static int instantiated=0;
    private static int created=0;


    public AbsActivity() {
        instantiated++;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new PreferenceLoadDefaults(this);
        created++;
    }


    @Override
    public void onDestroy() {
        created--;
        AppLog.d(this, "onDestroy() - " + created);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        logger.close();
        logger=null;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        logger = new AppLog(this);
    }


    @Override
    protected void finalize () throws Throwable {
        instantiated--;
        super.finalize();
    }

    public void appendStatusText(StringBuilder builder) {
        builder.append("<h1>");
        builder.append(getClass().getSimpleName());
        builder.append("</h1>");
        builder.append("<p>Instantiated activities: ");
        builder.append(instantiated);
        builder.append("<br>Created activities: ");
        builder.append(created);
        builder.append("<br>Count of application starts: ");
        builder.append(PreferenceLoadDefaults.getStartCount(this));
        builder.append("</p>");

    }


    @Override
    public void onActivityResult(int r, int id, Intent intent) {
        if (r == RESULT_OK) {
            FileIntent.pick(this, id, intent);
        }
    }

}
