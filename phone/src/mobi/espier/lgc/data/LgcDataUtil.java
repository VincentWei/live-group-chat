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

import java.util.ArrayList;
import java.util.List;

import mobi.espier.lgc.util.SecurityUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

public class LgcDataUtil {

    public static Uri saveNotification(Context context, String name, String content, int type,
            long time) {
        if (content == null || TextUtils.isEmpty(name) || TextUtils.isEmpty(content)) {
            return null;
        }

        String hash = SecurityUtils.md5(name + content + time);
        ContentValues values = new ContentValues();
        values.put(LgcProvider.DataColumns.NAME, name);
        values.put(LgcProvider.DataColumns.CONTENT, content);
        values.put(LgcProvider.DataColumns.TYPE, type);
        values.put(LgcProvider.DataColumns.TIME, time);
        values.put(LgcProvider.DataColumns.HASH, hash);
        values.put(LgcProvider.DataColumns.SYNC, LgcProvider.DataColumns.SYNC_DEFAULT);

        return context.getContentResolver().insert(LgcProvider.NOTIFICATION_URI, values);
    }

    public static int markSyncDone(Context context, List<String> ids) {
        if (ids == null || ids.size() < 1) {
            return 0;
        }

        ContentValues values = new ContentValues();
        values.put(LgcProvider.DataColumns.SYNC, LgcProvider.DataColumns.SYNC_DONE);

        StringBuilder sb = new StringBuilder();
        sb.append(" _id in (");
        int i = 0;
        for (String id : ids) {
            sb.append((i > 0) ? "," : "");
            sb.append(id);
            i++;
        }
        sb.append(" )");

        return context.getContentResolver().update(LgcProvider.NOTIFICATION_URI, values,
                sb.toString(), null);
    }

    public static int markSyncDone(Context context, long id) {
        if (id < 0) {
            return 0;
        }

        ContentValues values = new ContentValues();
        values.put(LgcProvider.DataColumns.SYNC, LgcProvider.DataColumns.SYNC_DONE);

        return context.getContentResolver().update(LgcProvider.NOTIFICATION_URI, values, "_id = ?",
                new String[] {Long.toString(id)});
    }

    public static int markAsSyncing(Context context, long id) {
        if (id < 0) {
            return 0;
        }

        ContentValues values = new ContentValues();
        values.put(LgcProvider.DataColumns.SYNC, LgcProvider.DataColumns.SYNC_ING);

        return context.getContentResolver().update(LgcProvider.NOTIFICATION_URI, values, "_id = ?",
                new String[] {Long.toString(id)});
    }

    public static class WxLiveNotification {
        public long id;
        public String name;
        public String content;
        public int type;
        public long time;
        public String hash;
        public int sync;
    }

    public static List<WxLiveNotification> queryNotification(Context context, int limit) {
        String order;
        if (limit > 0) {
            order = " _id asc limit " + limit;
        } else {
            order = " _ id asc";
        }

        Cursor cursor =
                context.getContentResolver().query(LgcProvider.NOTIFICATION_URI, null,
                        LgcProvider.DataColumns.SYNC + " = ?", new String[] {"0"}, order);
        if (cursor == null) {
            return null;
        }

        ArrayList<WxLiveNotification> infos = new ArrayList<WxLiveNotification>();
        while (cursor.moveToNext()) {
            WxLiveNotification noti = new WxLiveNotification();
            noti.id = cursor.getLong(LgcProvider.DataColumns.INDEX_ID);
            noti.name = cursor.getString(LgcProvider.DataColumns.INDEX_NAME);
            noti.content = cursor.getString(LgcProvider.DataColumns.INDEX_CONTENT);
            noti.time = cursor.getLong(LgcProvider.DataColumns.INDEX_TIME);
            noti.type = cursor.getInt(LgcProvider.DataColumns.INDEX_TYPE);
            noti.hash = cursor.getString(LgcProvider.DataColumns.INDEX_HASH);
            noti.sync = cursor.getInt(LgcProvider.DataColumns.INDEX_SYNC);
            infos.add(noti);
        }
        cursor.close();

        return infos;
    }

    public static int clearData(Context context) {
        return context.getContentResolver().delete(LgcProvider.NOTIFICATION_URI, " sync = ? ",
                new String[] {"1"});
    }

    public static int getNoSyncCount(Context context) {
        Cursor cursor =
                context.getContentResolver().query(LgcProvider.NOTIFICATION_URI,
                        new String[] {"count(*)"}, LgcProvider.DataColumns.SYNC + " = ?",
                        new String[] {"0"}, null);
        if (cursor == null) {
            return 0;
        }

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        return count;
    }
}
