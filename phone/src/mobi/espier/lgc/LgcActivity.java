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

import mobi.espier.lgc.data.MeteorHelper;
import mobi.espier.lgc.service.LgcAccessibilityService;
import mobi.espier.lgc.util.LgcUtils;
import mobi.espier.lgc.util.LogUtils;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LgcActivity extends Activity {
    private static String TAG = "LgcActivity";
    private TextView mTvNotiServiceState;
    private Button mBtnAccessibility;

    private EditText mEdWebSocketUrl;
    private EditText mEdUsername;
    private EditText mEdPassword;

    private TextView mTvMeteorState;

    private Button mBtnStart;
    private Button mBtnStop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mTvNotiServiceState = (TextView) findViewById(R.id.lgc_tv_service_state);
        mBtnAccessibility = (Button) findViewById(R.id.lgc_btn_accessibility_setting);
        mBtnAccessibility.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startAccessibilitySetting();
            }

        });

        mEdWebSocketUrl = (EditText) findViewById(R.id.lgc_edit_websocket_url);
        mEdUsername = (EditText) findViewById(R.id.lgc_edit_username);
        mEdPassword = (EditText) findViewById(R.id.lgc_edit_password);

        mBtnStart = (Button) findViewById(R.id.lgc_btn_start);
        mBtnStart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startMeteorSync();
            }

        });
        mBtnStop = (Button) findViewById(R.id.lgc_btn_stop);
        mBtnStop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                stopMeteorSync();
            }

        });

        mTvMeteorState = (TextView) findViewById(R.id.tv_meteor_state);

        mEdWebSocketUrl.setText(LgcUtils.getWebsocketUrl(this, null));
        mEdUsername.setText(LgcUtils.getUsername(this));
        mEdPassword.setText(LgcUtils.getPasword(this));

        updateUIState();

        IntentFilter filter = new IntentFilter(LgcUtils.ACTION_METEOR_LOGIN_RESULT);
        filter.addAction(LgcUtils.ACTION_METEOR_STATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean run = LgcAccessibilityService.isRuning();
        if (run) {
            mTvNotiServiceState.setText(getString(R.string.lgc_noti_service_running));
        } else {
            mTvNotiServiceState.setText(getString(R.string.lgc_noti_service_stop));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    private void updateUIState() {
        if (MeteorHelper.getInstance(this).isConnected()) {
            mBtnStart.setEnabled(false);
            mBtnStop.setEnabled(true);

            mEdWebSocketUrl.setEnabled(false);
            mEdUsername.setEnabled(false);
            mEdPassword.setEnabled(false);
        } else {
            mBtnStart.setEnabled(true);
            mBtnStop.setEnabled(false);

            mEdWebSocketUrl.setEnabled(true);
            mEdUsername.setEnabled(true);
            mEdPassword.setEnabled(true);
        }
    }

    private void startAccessibilitySetting() {
        try {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        } catch (Exception e) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
            e.printStackTrace();
        }
    }


    private void startMeteorSync() {
        String username = mEdUsername.getText().toString();
        String password = mEdPassword.getText().toString();
        String url = mEdWebSocketUrl.getText().toString();

        LgcUtils.setUsername(this, username);
        LgcUtils.setPasswrod(this, password);
        LgcUtils.setWebsocketUrl(this, url);

        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            String warning = getString(R.string.lgc_user_pw_url_empty_error);
            Toast.makeText(this, warning, Toast.LENGTH_LONG).show();
            updateState(warning);
            return;
        }

        MeteorHelper meteor = MeteorHelper.getInstance(this);
        meteor.connect(url, username, password);
    }

    private void stopMeteorSync() {
        MeteorHelper meteor = MeteorHelper.getInstance(this);
        meteor.disconnect();

        updateUIState();
    }

    private void updateState(String state) {
        mTvMeteorState.setText(state);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(LgcUtils.ACTION_METEOR_LOGIN_RESULT, action)) {
                updateUIState();
                int event =
                        intent.getIntExtra(LgcUtils.KEY_METEOR_LOGIN_RESULT,
                                LgcUtils.METEOR_LOGIN_OK);
                if (LgcUtils.METEOR_LOGIN_FAILED == event) {
                    String reason = intent.getStringExtra(LgcUtils.KEY_REASON);
                    Toast.makeText(LgcActivity.this, getString(R.string.lgc_login_failed) + reason,
                            Toast.LENGTH_LONG).show();
                }
            } else if (TextUtils.equals(LgcUtils.ACTION_METEOR_STATE, action)) {
                String state = intent.getStringExtra(LgcUtils.KEY_METEOR_STATE);
                LogUtils.d(TAG, "................... meteor state : " + state);
                updateState(state);
            }
        }

    };
}
