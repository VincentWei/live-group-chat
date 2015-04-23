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
package mobi.espier.lgc.data;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.ResultListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.espier.lgc.R;
import mobi.espier.lgc.data.LgcDataUtil.WxLiveNotification;
import mobi.espier.lgc.util.LgcUtils;
import mobi.espier.lgc.util.LogUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

public class MeteorHelper implements MeteorCallback {
    private static final String TAG = "meteor";
    private final Context mContext;
    private Meteor mMeteor = null;
    private String mUrl;
    private String mUsername;
    private String mPassword;

    private final boolean mEnableUserAuth = true;

    private static MeteorHelper sMeteorHelper = null;

    public static MeteorHelper getInstance(Context context) {
        if (sMeteorHelper == null) {
            sMeteorHelper = new MeteorHelper(context);
        }
        return sMeteorHelper;
    }

    public MeteorHelper(Context context) {
        mContext = context;

        IntentFilter filter = new IntentFilter(LgcUtils.ACTION_DATA_CHANGE);
        filter.addAction(LgcUtils.ACTION_METEOR_LOGIN_RESULT);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, filter);
    }

    public void connect() {
        String url = LgcUtils.getWebsocketUrl(mContext, null);
        String username = LgcUtils.getUsername(mContext);
        String password = LgcUtils.getPasword(mContext);
        connect(url, username, password);
    }

    public void connect(String url, String username, String password) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            String warning = mContext.getString(R.string.lgc_user_pw_url_empty_error);
            Toast.makeText(mContext, warning, Toast.LENGTH_LONG).show();
            sendMeteorState(warning);
            LogUtils.d(TAG, "connect|url=" + url + "|user=" + username + "|password=" + password
                    + "|do nothing............................");
            return;
        }

        if (TextUtils.equals(url, mUrl) && TextUtils.equals(username, mUsername)
                && TextUtils.equals(password, mPassword) && mMeteor != null
                && mMeteor.isConnected()) {
            return;
        }
        disconnect();

        LogUtils.d(TAG, "connect|url=" + url + "|user=" + username + "|password=" + password);
        mUrl = url;
        mUsername = username;
        mPassword = password;

        sendMeteorState(mContext.getString(R.string.lgc_meteor_state_connect));

        mMeteor = new Meteor(mUrl);
        mMeteor.setCallback(this);
    }

    public void disconnect() {
        LogUtils.d(TAG, "disconnect");
        sendMeteorState("");
        if (mMeteor != null) {
            mMeteor.disconnect();
        }
        mMeteor = null;
    }

    public boolean isConnected() {
        LogUtils.d(TAG, "isConnected");
        if (mMeteor != null) {
            return mMeteor.isConnected();
        }
        return false;
    }

    @Override
    public void onConnect() {
        LogUtils.d(TAG, "onConnect");
        sendMeteorState(mContext.getString(R.string.lgc_meteor_state_connect_ok));
        if (!mEnableUserAuth) {
            return;
        }

        sendMeteorState(mContext.getString(R.string.lgc_meteor_state_user_login));
        // do user auth
        mMeteor.loginWithUsername(mUsername, mPassword, new ResultListener() {

            @Override
            public void onSuccess(String result) {
                LogUtils.i(TAG, "................................. login success : " + result);
                Intent intent = new Intent(LgcUtils.ACTION_METEOR_LOGIN_RESULT);
                intent.putExtra(LgcUtils.KEY_METEOR_LOGIN_RESULT, LgcUtils.METEOR_LOGIN_OK);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                sendMeteorState(mContext.getString(R.string.lgc_meteor_state_user_login_ok));
            }

            @Override
            public void onError(String error, String reason, String details) {
                LogUtils.i(TAG, "................................. login failed error=" + error
                        + "|reason=" + reason + "|details=" + details);

                Intent intent = new Intent(LgcUtils.ACTION_METEOR_LOGIN_RESULT);
                intent.putExtra(LgcUtils.KEY_METEOR_LOGIN_RESULT,
                        LgcUtils.METEOR_LOGIN_FAILED);
                intent.putExtra(LgcUtils.KEY_REASON, reason);

                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                disconnect();
                sendMeteorState(mContext.getString(R.string.lgc_meteor_state_user_login_failed));
            }

        });
    }

    @Override
    public void onDisconnect(int code, String reason) {
        LogUtils.d(TAG, "onDisconnect|code=" + code + "|reason=" + reason);
        sendMeteorState(mContext.getString(R.string.lgc_meteor_state_disconnect_reason) + reason);
        connect(mUrl, mUsername, mPassword);
    }

    @Override
    public void onDataAdded(String collectionName, String documentID, String fieldsJson) {
        LogUtils.d(TAG, "onDataAdded|collectionName=" + collectionName + "|documentID="
                + documentID + "|fieldsJson=" + fieldsJson);
    }

    @Override
    public void onDataChanged(String collectionName, String documentID, String updatedValuesJson,
            String removedValuesJson) {
        LogUtils.d(TAG, "onDataChanged|collectionName=" + collectionName + "|documentID="
                + documentID + "|updatedValuesJson=" + removedValuesJson);
    }

    @Override
    public void onDataRemoved(String collectionName, String documentID) {
        LogUtils.d(TAG, "onDataRemoved|collectionName=" + collectionName + "|documentID="
                + documentID);
    }

    @Override
    public void onException(Exception e) {
        if (e != null) {
            LogUtils.d(TAG, "onException|e=" + e.toString(), e);
        }
    }

    public void sendMeteorState(String state) {
        Intent intent = new Intent(LgcUtils.ACTION_METEOR_STATE);
        intent.putExtra(LgcUtils.KEY_METEOR_STATE, state);

        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    public void sendData(String nickId, String content, int contentType, long createAt,
            String hash, InsertListener listener) {
        sendMeteorState(mContext.getString(R.string.lgc_meteor_state_update_data));
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("nickId", nickId);
        data.put("content", content);
        data.put("contentType", contentType);
        data.put("createdAt", createAt);
        data.put("hash", hash);
        mMeteor.insert("wx_chat_records", data, listener);

    }

    public void doUploadData() {
        doUploadData(LgcUtils.UPLOAD_LIMIT);
    }

    public void doUploadData(int limit) {
        if (mMeteor == null || !mMeteor.isConnected()) {
            return;
        }

        List<WxLiveNotification> notis = LgcDataUtil.queryNotification(mContext, limit);
        LogUtils.d(TAG, ".................doUploadData query notification size = "
                + (notis != null ? notis.size() : 0));

        if (notis == null || notis.size() < 1) {
            return;
        }

        for (WxLiveNotification noti : notis) {
            sendData(noti.name, noti.content, noti.type, noti.time, noti.hash, new InsertListener(
                    mContext, noti.id));
            LgcDataUtil.markAsSyncing(mContext, noti.id);
            LogUtils.d(TAG, ".................doUploadData mark as ing = " + noti.id);
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(LgcUtils.ACTION_METEOR_LOGIN_RESULT, action)) {
                int event =
                        intent.getIntExtra(LgcUtils.KEY_METEOR_LOGIN_RESULT,
                                LgcUtils.METEOR_LOGIN_OK);
                if (LgcUtils.METEOR_LOGIN_OK == event) {
                    LogUtils.d(TAG,
                            "........................................ login ok.........................");
                    doUploadData();
                }
            } else if (TextUtils.equals(LgcUtils.ACTION_DATA_CHANGE, action)) {
                doUploadData();
            }
        }

    };

    private static class InsertListener implements ResultListener {
        private final long dbId;
        private final Context context;

        public InsertListener(Context context, long dbId) {
            this.dbId = dbId;
            this.context = context;
        }

        @Override
        public void onSuccess(String result) {
            LogUtils.i(TAG, ".........................................onSuccess=" + result);
            LgcDataUtil.markSyncDone(this.context, dbId);
            MeteorHelper.getInstance(context).sendMeteorState("");
        }

        @Override
        public void onError(String error, String reason, String details) {
            LogUtils.i(TAG, ".........................................error ...error=" + error
                    + "|reason=" + reason + "|details=" + details);
        }

    };
}
