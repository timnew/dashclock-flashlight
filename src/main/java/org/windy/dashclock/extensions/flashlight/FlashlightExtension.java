package org.windy.dashclock.extensions.flashlight;

import android.content.Intent;
import android.hardware.Camera;
import android.widget.Toast;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class FlashlightExtension extends DashClockExtension {

    public static final String ACTION_TURN_ON = "org.windy.dashclock.extensions.flashlight.turnOn";
    public static final String ACTION_TURN_OFF = "org.windy.dashclock.extensions.flashlight.turnOff";
    public static final String ACTION_TOGGLE = "org.windy.dashclock.extensions.flashlight.toggle";

    private boolean isLightOn;
    private Camera camera;

    private static FlashlightExtension instance;

    public static FlashlightExtension getInstance() {
        return instance;
    }

    public FlashlightExtension() {
        instance = this;
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
}

