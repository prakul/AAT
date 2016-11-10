package ch.bailu.aat.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class fakeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Eavesdropping ","Recepting the intent for other component");
        String action = intent.getAction();
        Log.i("Eavesdropping ","Intent received , dumping data");


    }
}
