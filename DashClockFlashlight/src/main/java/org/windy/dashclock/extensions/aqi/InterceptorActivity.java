package org.windy.dashclock.extensions.aqi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import static org.windy.dashclock.extensions.aqi.FlashlightExtension.ACTION_TOGGLE;
import static org.windy.dashclock.extensions.aqi.FlashlightExtension.ACTION_TURN_OFF;
import static org.windy.dashclock.extensions.aqi.FlashlightExtension.ACTION_TURN_ON;

public class InterceptorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setVisible(false);
        finish();

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        broadcastManager.sendBroadcast(new Intent(intent.getAction()));
    }
}
