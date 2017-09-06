package in.cableguy.callrecording;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by manoj on 05/09/2017.
 */

public class ServiceCaller extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.stopService(new Intent(context, CallRecordingService.class));
        intent = new Intent(context, CallRecordingService.class);
        context.startService(intent);
        Toast.makeText(context, "Service explicitly called", Toast.LENGTH_SHORT).show();
        Log.d("ServiceCaller", "onReceive: service explicitly called.");
    }
}
