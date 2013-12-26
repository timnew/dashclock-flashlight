
package org.windy.dashclock.extensions.aqi;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(formKey = "",
        formUri = "http://www.bugsense.com/api/acra?api_key=5cb87750d08c5ccc853aa9e")
public class DashClockFlashlightApplication
        extends Application {
    @Override
    public void onCreate() {
        ACRA.init(this);
        super.onCreate();
    }
}
