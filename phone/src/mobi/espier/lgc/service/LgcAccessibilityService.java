/*
 *     Copyright (C) 2010~2014 FMSoft (Espier Studio)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Affero General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 *    GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package mobi.espier.lgc.service;

import mobi.espier.lgc.LgcApp;
import mobi.espier.lgc.util.LogUtils;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

public class LgcAccessibilityService extends AccessibilityService {
    private static final String TAG = "WxLiveAccessibilibyService";

    private static boolean run = false;
    private static boolean enabled = true;

    public static boolean isRuning() {
        return run;
    }

    public static void setEnabled(boolean b) {
        enabled = b;
    }

    public LgcAccessibilityService() {}

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!enabled) {
            return;
        }
        if (AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED == event.getEventType()) {
            Notification notification = (Notification) event.getParcelableData();
            if (notification != null) {
                LogUtils.d(TAG, "receive notification from " + event.getPackageName()
                        + "|title is " + notification.tickerText);
                LgcApp.getInstance().onNotification(event.getPackageName(), notification,
                        System.currentTimeMillis());
            }
        } else if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event.getEventType()) {
            // find in android sources event.getPackageName() will not be null, but crash collect
            // show it is may be null
            CharSequence package_name = event.getPackageName();
            if (package_name != null) {
                LogUtils.d(TAG, "current package is : " + package_name);
            }
        }
    }

    @Override
    public void onInterrupt() {
        run = false;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        run = true;
        if (Build.VERSION.SDK_INT < 14) {
            AccessibilityServiceInfo serverInfo = new AccessibilityServiceInfo();
            serverInfo.eventTypes =
                    AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED
                            | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
            serverInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL;
            serverInfo.flags = AccessibilityServiceInfo.DEFAULT;
            serverInfo.notificationTimeout = 0L;
            setServiceInfo(serverInfo);
        }
    }

    @Override
    public void onDestroy() {
        run = false;
        super.onDestroy();
    }

    @Override
    public void onRebind(Intent intent) {
        run = true;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        run = false;
        return super.onUnbind(intent);
    }

}
