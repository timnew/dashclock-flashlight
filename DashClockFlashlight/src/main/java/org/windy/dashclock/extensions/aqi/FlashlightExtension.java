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
        camera = Camera.open();

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
                .icon(isLightOn ? R.drawable.ic_light_bulb_on : R.drawable.ic_light_bulb_off)
                .status("Light is " + (isLightOn ? "On" : "Off"))
                .expandedTitle("Flash Light")
                .expandedBody("Light is " + (isLightOn ? "On" : "Off"))
                .clickIntent(new Intent(ACTION_TOGGLE, null, this, InterceptorActivity.class)));
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

