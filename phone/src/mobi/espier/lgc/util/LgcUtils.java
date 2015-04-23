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
package mobi.espier.lgc.util;


import android.content.Context;

public class LgcUtils {
    public static final int UPLOAD_DELAYED = 30000;
    public static final int UPLOAD_LIMIT = 10;

    public static final String KEY_UPLOAD_DELAYED = "key_upload_delayed";
    public static final String KEY_WEBSOCKET_URL = "key_websocket_url";
    public static final String KEY_USERNAME = "key_username";
    public static final String KEY_PASSWORD = "key_password";
    public static final String KEY_REASON = "key_reason";
    public static final String KEY_METEOR_LOGIN_RESULT = "key_meteor_login_result";
    public static final String KEY_METEOR_STATE = "key_meteor_state";

    public static final String ACTION_DATA_CHANGE = "action_data_change";
    public static final String ACTION_METEOR_LOGIN_RESULT = "action_meteor_login_result";
    public static final String ACTION_METEOR_STATE = "action_meteor_state";

    public static final int METEOR_LOGIN_FAILED = 0;
    public static final int METEOR_LOGIN_OK = 1;

    public static int getUploadDelayed(Context context) {
        return PreferencesUtil.getInt(context, KEY_UPLOAD_DELAYED, UPLOAD_DELAYED);
    }

    public static void setUploadDelayed(Context context, int delayed) {
        PreferencesUtil.putInt(context, KEY_UPLOAD_DELAYED, delayed);
    }

    public static String getWebsocketUrl(Context context, String defUrl) {
        return PreferencesUtil.getString(context, KEY_WEBSOCKET_URL, defUrl);
    }

    public static void setWebsocketUrl(Context context, String url) {
        PreferencesUtil.putString(context, KEY_WEBSOCKET_URL, url);
    }

    public static String getUsername(Context context) {
        return PreferencesUtil.getString(context, KEY_USERNAME, null);
    }

    public static void setUsername(Context context, String username) {
        PreferencesUtil.putString(context, KEY_USERNAME, username);
    }

    public static String getPasword(Context context) {
        return PreferencesUtil.getString(context, KEY_PASSWORD, null);
    }

    public static void setPasswrod(Context context, String password) {
        PreferencesUtil.putString(context, KEY_PASSWORD, password);
    }
}
