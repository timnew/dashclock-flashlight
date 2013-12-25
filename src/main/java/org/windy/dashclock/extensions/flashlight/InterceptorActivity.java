package org.windy.dashclock.extensions.flashlight;

import android.app.Activity;
import android.os.Bundle;

import static org.windy.dashclock.extensions.flashlight.FlashlightExtension.ACTION_TOGGLE;
import static org.windy.dashclock.extensions.flashlight.FlashlightExtension.ACTION_TURN_OFF;
import static org.windy.dashclock.extensions.flashlight.FlashlightExtension.ACTION_TURN_ON;

public class InterceptorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        moveTaskToBack(true);

        FlashlightExtension extension = FlashlightExtension.getInstance();

        if (extension == null)
            return;

        String action = getIntent().getAction();

        if (ACTION_TOGGLE.equals(action)) {
            extension.toggleLight();
        } else if (ACTION_TURN_ON.equals(action)) {
            extension.turnLightOn();
        } else if (ACTION_TURN_OFF.equals(action)) {
            extension.turnLightOff();
        }

        finish();

        overridePendingTransition(0,0);
    }
}
