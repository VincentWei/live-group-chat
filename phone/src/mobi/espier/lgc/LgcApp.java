/*
 * Copyright (C) 2010~2014 FMSoft (Espier Studio)
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package mobi.espier.lgc;

import mobi.espier.lgc.data.LgcDataUtil;
import mobi.espier.lgc.data.LgcProvider;
import mobi.espier.lgc.data.MeteorHelper;
import mobi.espier.lgc.util.LgcUtils;
import mobi.espier.lgc.util.LogUtils;
import mobi.espier.lgc.util.WeakHandlerTemplate;
import android.app.Application;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

public class LgcApp extends Application {
    private static final int MSG_ON_NOTIFICATION = 10001;
    public static final String KEY_NOTIFICATION = "key_notification";
    public static final String KEY_NOTIFICATION_TIME = "key_notification_time";
    public static final String KEY_PACKAGE_NAME = "key_package_name";

    private WeakHandler mHandler = null;
    private static LgcApp sWxLiveApp = null;

    public static LgcApp getInstance() {
        return sWxLiveApp;
    }

    public LgcApp() {
        sWxLiveApp = this;
        LogUtils.i("xsm", "........................................ app.pid=" + Process.myPid());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new WeakHandler(this);
        MeteorHelper.getInstance(this).connect();
    }

    public void onNotification(CharSequence packageName, Notification notification, long time) {
        Message message = mHandler.obtainMessage(MSG_ON_NOTIFICATION);
        Bundle data = new Bundle();
        data.putCharSequence(KEY_PACKAGE_NAME, packageName);
        data.putParcelable(KEY_NOTIFICATION, notification);
        data.putLong(KEY_NOTIFICATION_TIME, time);
        message.setData(data);
        mHandler.sendMessage(message);
    }

    public void saveNotification(Bundle data) {
        CharSequence packageName = data.getCharSequence(KEY_PACKAGE_NAME);
        if (!TextUtils.equals(packageName, "com.tencent.mm")) {
            return;
        }

        Notification notification = (Notification) data.getParcelable(KEY_NOTIFICATION);
        long time = data.getLong(KEY_NOTIFICATION_TIME);
        if (TextUtils.isEmpty(notification.tickerText)) {
            return;
        }
        // add code for save notification to db
        String title = notification.tickerText.toString();
        int index = title.indexOf(':');

        if (index > 0) {
            String name = title.substring(0, index).trim();
            int type = LgcProvider.DataColumns.TYPE_TEXT;
            String content = null;
            if (index < title.length()) {
                content = title.substring(index + 1).trim();
            }

            if (TextUtils.equals(content, "[图片]")) {
                type = LgcProvider.DataColumns.TYPE_IMAGE;
            } else if (TextUtils.equals(content, "[语音]")) {
                type = LgcProvider.DataColumns.TYPE_VOICE;
            }
            LgcDataUtil.saveNotification(this, name.trim(), content, type, time);
            LocalBroadcastManager.getInstance(this).sendBroadcast(
                    new Intent(LgcUtils.ACTION_DATA_CHANGE));
        }
    }

    static class WeakHandler extends WeakHandlerTemplate<LgcApp> {

        public WeakHandler(LgcApp context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            LgcApp app = getObject();
            switch (msg.what) {
                case MSG_ON_NOTIFICATION:
                    app.saveNotification(msg.getData());
                    break;

                default:
            }
        }
    };
}
