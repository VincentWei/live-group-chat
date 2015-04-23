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
package mobi.espier.lgc.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import mobi.espier.lgc.data.LgcDataUtil.WxLiveNotification;
import mobi.espier.lgc.util.LgcUtils;
import mobi.espier.lgc.util.LogUtils;
import mobi.espier.lgc.util.SecurityUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

public class UploadHelper {
    private static final String TAG = "UploadHelper";
    private final Context mContext;
    private final String mUploadUrl;
    private List<String> mIDList;

    public static String VERIFY_HEAD = "327aa";
    public static String VERIFY_TAIL = "738f6";

    public UploadHelper(Context context, String url) {
        mContext = context;
        mUploadUrl = url;
    }

    private String doHttpPostAndGetResponse(String url, byte[] data) {
        if (TextUtils.isEmpty(url) || data == null || data.length < 1) {
            return null;
        }

        String response = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            OutputStream outStream = conn.getOutputStream();
            outStream.write(data);

            int mResponseCode = conn.getResponseCode();
            if (mResponseCode == 200) {
                response = getHttpResponse(conn);
            }
            conn.disconnect();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private String getHttpResponse(HttpURLConnection conn) {
        String ret = "";
        try {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String next = null;
            while ((next = reader.readLine()) != null) {
                ret += next;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public String packageData(List<WxLiveNotification> notifications) {
        try {
            JSONArray array = new JSONArray();
            mIDList = new ArrayList<String>();
            for (WxLiveNotification noti : notifications) {
                mIDList.add(Long.toString(noti.id));
                JSONObject stoneObject = new JSONObject();
                stoneObject.put("nickId", noti.name);
                stoneObject.put("content", noti.content);
                stoneObject.put("contentType", noti.type);
                stoneObject.put("createdAt", noti.time);
                stoneObject.put("hash", noti.hash);
                array.put(stoneObject);
            }

            String record = array.toString();
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append(VERIFY_HEAD).append(record).append(VERIFY_TAIL);

            StringBuilder sb = new StringBuilder();
            sb.append("chatRecords=").append(URLEncoder.encode(record)).append("&verify=")
                    .append(SecurityUtils.md5(sBuilder.toString()));
            // return URLEncoder.encode(sb.toString());
            return sb.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void doUploadData() {
        doUploadData(LgcUtils.UPLOAD_LIMIT);
    }

    public void doUploadData(int limit) {
        List<WxLiveNotification> notis = LgcDataUtil.queryNotification(mContext, limit);
        LogUtils.d(TAG, ".................doUploadData query notification size = "
                + (notis != null ? notis.size() : 0));

        if (notis == null || notis.size() < 1) {
            return;
        }

        String data = packageData(notis);
        LogUtils.d(TAG, "................. doUploadData data = " + data);
        if (TextUtils.isEmpty(data)) {
            return;
        }

        LogUtils.d(TAG, "................. doUploadData url = " + mUploadUrl);
        String response = doHttpPostAndGetResponse(mUploadUrl, data.getBytes());
        LogUtils.d(TAG, "................. doUploadData response = " + response);
        LogUtils.d(TAG, "................. doUploadData mIDList = " + mIDList);

        if (TextUtils.equals("OK", response) && mIDList.size() > 0) {
            int count = LgcDataUtil.markSyncDone(mContext, mIDList);
            LogUtils.d(TAG, "................. doUploadData mark sync done = " + count);
        }
    }
}
