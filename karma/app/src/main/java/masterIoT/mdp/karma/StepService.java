package masterIoT.mdp.karma;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * @Class StepService
 * @brief Manages the step counter to work in the background
 */
public class StepService extends Service implements SensorEventListener {

    private int numbSteps = 0;
    private SensorManager sensorManager;
    private Sensor stepSensor;

    /**
     * @brief Called when the service is created. Registers the listener, loads the saved step count and starts the service.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        SharedPreferences prefsSensor = getSharedPreferences("SensorData", Context.MODE_PRIVATE);
        numbSteps = prefsSensor.getInt("totalSteps", 0);

        startForeground(1, createNotification());
    }

    /**
     * @brief Creates a notification for the service.
     * @return Notification
     */
    private Notification createNotification() {

//        NotificationChannel channel = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            channel = new NotificationChannel(
//                    "step_channel",
//                    "Step Tracking",
//                    NotificationManager.IMPORTANCE_LOW
//            );
//
//            getSystemService(NotificationManager.class).createNotificationChannel(channel);
//
//            return new Notification.Builder(this, "step_channel")
//                    .setContentTitle("Tracking your steps")
//                    .setContentText("Step counter is active")
//                    .setSmallIcon(R.drawable.ic_launcher_foreground)
//                    .build();
//        }
        String channelId = "step_channel";
        Intent stopIntent = new Intent(this, StepService.class);
        stopIntent.setAction("STOP");
        PendingIntent stopPendingIntent =
                PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        // Create channel ONLY on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Step Tracking",
                    NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        // Use NotificationCompat for backward compatibility
        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Tracking your steps")
                .setContentText("Step counter is active")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .addAction(R.drawable.addmission, "Stop", stopPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    /**
     * @brief Called when the service is started.
     * @param intent The Intent supplied to {@link android.content.Context#startService},
     * as given.  This may be null if the service is being restarted after
     * its process has gone away, and it had previously returned anything
     * except {@link #START_STICKY_COMPATIBILITY}.
     * @param flags Additional data about this start request.
     * @param startId A unique integer representing this specific request to
     * start.  Use with {@link #stopSelfResult(int)}.
     *
     * @return Indicates how the system should treat this start request.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "STOP".equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }
        return START_STICKY;
    }

    /**
     * @brief Called when the service is bound (Not used).
     *
     * @param intent The Intent that was used to bind to this service,
     * as given to {@link android.content.Context#bindService
     * Context.bindService}.  Note that any extras that were included with
     * the Intent at that point will <em>not</em> be seen here.
     *
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * @brief Called when the accuracy of the registered sensor changes (Not used).
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *         {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * @brief Called when a sensor has changed. Increments the step count and Broadcasts it for the main activity.
     * @param sensorEvent the {@link android.hardware.SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //System.out.println(sensorEvent.sensor.getType());
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            numbSteps = (int) sensorEvent.values[0] + numbSteps;
//            if (numbSteps != 0 && numbSteps % 250 == 0) {
//                vibrator.vibrate(1000);
//                SharedPreferences prefs = getSharedPreferences("KarmaPoints", Context.MODE_PRIVATE);
//                karmaPoints = prefs.getInt("totalKarma", 0);
//                karmaPoints++;
//                tvKarma.setText(String.valueOf(karmaPoints));
//            }
            SharedPreferences prefs = getSharedPreferences("SensorData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("totalSteps", numbSteps);
            editor.apply();
            Intent intent = new Intent(getPackageName()+".STEP_UPDATE");
            intent.putExtra("totalSteps", numbSteps);
            Log.d("StepService", "Broadcasting steps: " + numbSteps);
            sendBroadcast(intent);
        }
    }

    /**
     * @brief Called when the service is destroyed. It unregisters the listener.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

}
