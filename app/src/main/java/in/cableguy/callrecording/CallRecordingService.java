package in.cableguy.callrecording;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by manoj on 05/09/2017.
 */

public class CallRecordingService extends Service {

    final MediaRecorder recorder = new MediaRecorder();
    public static final String TAG = CallRecordingService.class.getName();
    boolean recording = false;
    int i = 0;
    String fname, timing;
    DateFormat dateFormat;

    BroadcastReceiver callRecorder = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            i++;
            if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
                Toast.makeText(getApplicationContext(), state, Toast.LENGTH_LONG).show();
                Toast.makeText(context, "Start CaLLED " + recording + fname, Toast.LENGTH_LONG).show();
                Log.d(TAG, "onReceive: Start called");

                startRecording();


            }
            if (TelephonyManager.EXTRA_STATE_IDLE.equals(state) && recording == true) {
                Toast.makeText(getApplicationContext(), state, Toast.LENGTH_LONG).show();
                Toast.makeText(context, "STOP CaLLED :" + recording, Toast.LENGTH_LONG).show();
                Log.d(TAG, "onReceive: stop called");
                stopRecording();
            }

            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {

                fname = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                timing = dateFormat.format(new Date());
                Log.d(TAG, "ringing: Date " + timing);
                fname = fname + " " + timing;
                Toast.makeText(getApplicationContext(), state + " : " + fname, Toast.LENGTH_LONG).show();
                Log.d(TAG, "onReceive: Ringing");
            }
        }
    };
    BroadcastReceiver outGoingNumDetect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fname = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            timing = dateFormat.format(new Date());
            Log.d(TAG, "out going: Date " + timing);
            fname = fname + " " + timing;
        }
    };

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Toast.makeText(getApplicationContext(), "Service Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate: Service created");
        IntentFilter RecFilter = new IntentFilter();
        RecFilter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(callRecorder, RecFilter);
        IntentFilter OutGoingNumFilter = new IntentFilter();
        OutGoingNumFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(outGoingNumDetect, OutGoingNumFilter);

        dateFormat = new SimpleDateFormat("yyyy MM dd HH mm ss");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(callRecorder);
        unregisterReceiver(outGoingNumDetect);
        Toast.makeText(getApplicationContext(), "Destroyed", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onDestroy: Destroyed");
    }


    public void startRecording() {
        if (recording == false) {
            Toast.makeText(getApplicationContext(), "Recorder_Sarted" + fname, Toast.LENGTH_LONG).show();
            Log.d(TAG, "startRecording: ");
            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOnErrorListener(errorListener);
            recorder.setOnInfoListener(infoListener);

            String file = Environment.getExternalStorageDirectory().toString();
            String filepath = file + "/11111111111111";
            File dir = new File(filepath);
            dir.mkdirs();

            filepath += "/" + fname + ".amr";
            recorder.setOutputFile(filepath);
            Log.d(TAG, "startRecording: filepath " + filepath);
            try {
                recorder.prepare();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                Log.d(TAG, "exception");
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d(TAG, "exception2 :");
                e.printStackTrace();

            }
            recorder.start();
            recording = true;
        }
    }

    public void stopRecording() {
        if (recording == true) {
            Toast.makeText(getApplicationContext(), "Recorder_Relesed from " + recording, Toast.LENGTH_LONG).show();
            Log.d(TAG, "stopRecording: ");

            recorder.stop();
            recorder.reset();
            recorder.release();
            recording = false;
            broadcastIntent();
        }
    }

    public void broadcastIntent() {
        Intent intent = new Intent();
        intent.setAction("in.cableguy.callrecording.CUSTOM_INTENT");
        sendBroadcast(intent);
        Toast.makeText(getApplicationContext(), "BroadCast", Toast.LENGTH_LONG).show();
        Log.d(TAG, "broadcastIntent() ");

    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            //Toast.makeText(CallActivity.this, "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onError: what " + what + " , " + extra);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            //Toast.makeText(CallActivity.this, "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onInfo: what " + what + " , " + extra);
        }
    };
}
