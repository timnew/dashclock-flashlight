package org.windy.dashclock.extensions.aqi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import org.windy.dashclock.extensions.flashlight.R;

public class FlashlightExtension extends DashClockExtension {

    public static final String ACTION_TURN_ON = "org.windy.dashclock.extensions.flashlight.turnOn";
    public static final String ACTION_TURN_OFF = "org.windy.dashclock.extensions.flashlight.turnOff";
    public static final String ACTION_TOGGLE = "org.windy.dashclock.extensions.flashlight.toggle";

    private boolean isLightOn;
    private Camera camera;
    private LocalBroadcastManager broadcastManager;

    @Override
    public void onCreate() {
        super.onCreate();

        broadcastManager = LocalBroadcastManager.getInstance(getApplication());

        broadcastManager.registerReceiver(new FlashlightControlIntentReceiver(this), buildIntentFilter());
    }

    private IntentFilter buildIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(ACTION_TURN_ON);
        intentFilter.addAction(ACTION_TURN_OFF);
        intentFilter.addAction(ACTION_TOGGLE);

        return intentFilter;
    }

    public boolean isLightOn() {
        return isLightOn;
    }

    private void setLightOn(boolean isLightOn) {
        this.isLightOn = isLightOn;

        onUpdateData(UPDATE_REASON_UNKNOWN);
    }

    public void toggleLight() {
        turnLight(!isLightOn());
    }

    public void turnLight(boolean lightOn) {
        if (lightOn) {
            turnLightOn();
        } else {
            turnLightOff();
        }
    }

    public void turnLightOn() {
        try {
            camera = Camera.open();
        } catch (RuntimeException ex) { // Exception might be thrown when Camera service is unreachable.
            /*
            java.lang.RuntimeException: Fail to connect to camera service
            at android.hardware.Camera.native_setup(Native Method)
            at android.hardware.Camera.<init>(Camera.java:413)
            at android.hardware.Camera.open(Camera.java:384)
            at org.windy.dashclock.extensions.aqi.FlashlightExtension.turnLightOn(FlashlightExtension.java:68)
            at org.windy.dashclock.extensions.aqi.FlashlightExtension.turnLight(FlashlightExtension.java:61)
            at org.windy.dashclock.extensions.aqi.FlashlightExtension.toggleLight(FlashlightExtension.java:56)
            at org.windy.dashclock.extensions.aqi.FlashlightExtension$FlashlightControlIntentReceiver.onReceive(FlashlightExtension.java:119)
            at android.support.v4.content.LocalBroadcastManager.executePendingBroadcasts(LocalBroadcastManager.java:297)
            at android.support.v4.content.LocalBroadcastManager.access$000(LocalBroadcastManager.java:46)
            at android.support.v4.content.LocalBroadcastManager$1.handleMessage(LocalBroadcastManager.java:116)
            at android.os.Handler.dispatchMessage(Handler.java:99)
            at android.os.Looper.loop(Looper.java:137)
            at android.app.ActivityThread.main(ActivityThread.java:5450)
            at java.lang.reflect.Method.invokeNative(Native Method)
            at java.lang.reflect.Method.invoke(Method.java:525)
            at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1187)
            at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1003)
            at dalvik.system.NativeStart.main(Native Method)
             */
            ex.printStackTrace();
        }

        if (camera == null) {
            Toast.makeText(this, R.string.no_camera, Toast.LENGTH_SHORT).show();
        } else {
            Camera.Parameters param = camera.getParameters();
            param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            try {
                camera.setParameters(param);
                camera.startPreview();
                setLightOn(true);
            } catch (Exception e) {
                Toast.makeText(this, R.string.no_flash, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void turnLightOff() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        setLightOn(false);
    }

    @Override
    protected void onUpdateData(int reason) {

        publishUpdate(new ExtensionData()
                .visible(true)
                .icon(getIcon())
                .status(getStatus())
                .expandedTitle(getApplicationContext().getString(R.string.title))
                .expandedBody(getStatus())
                .clickIntent(buildClickIntent()));
    }

    private Intent buildClickIntent() {
        Intent clickIntent = new Intent(ACTION_TOGGLE, null, this, InterceptorActivity.class);
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return clickIntent;
    }

    private String getStatus() {
        return getApplicationContext().getString(R.string.status_template, getLightStatus());
    }

    private String getLightStatus() {
        return getApplicationContext().getString(isLightOn ? R.string.light_on : R.string.light_off);
    }

    private int getIcon() {
        return isLightOn ? R.drawable.ic_light_bulb_on : R.drawable.ic_light_bulb_off;
    }

    private static class FlashlightControlIntentReceiver extends BroadcastReceiver {

        private final FlashlightExtension extension;

        public FlashlightControlIntentReceiver(FlashlightExtension extension) {
            this.extension = extension;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_TOGGLE.equals(action)) {
                extension.toggleLight();
            } else if (ACTION_TURN_ON.equals(action)) {
                extension.turnLightOn();
            } else if (ACTION_TURN_OFF.equals(action)) {
                extension.turnLightOff();
            }
        }
    }
}

