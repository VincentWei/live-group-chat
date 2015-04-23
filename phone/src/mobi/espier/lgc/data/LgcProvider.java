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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

public class LgcProvider extends ContentProvider {
    static final String AUTHORITY = "mobi.espier.lgc";

    private static final String[] TABLE_NAMES = new String[] {LgcDBHelper.TABLE_NAME};

    private static final int URI_MATCH_NOTIFICATION = 0;
    private static final int URI_MATCH_NOTIFICATION_ID = 10;
    public static final Uri NOTIFICATION_URI = Uri.parse("content://" + AUTHORITY + "/"
            + TABLE_NAMES[URI_MATCH_NOTIFICATION]);

    private static final UriMatcher URI_MATCHER;
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAMES[URI_MATCH_NOTIFICATION], URI_MATCH_NOTIFICATION);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAMES[URI_MATCH_NOTIFICATION] + "/#",
                URI_MATCH_NOTIFICATION_ID);
    }

    private SQLiteOpenHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new LgcDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projectionIn, String selection, String[] selectionArgs,
            String sortOrder) {
        int match = URI_MATCHER.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException("Unknown URL");
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        String[] projection = null;
        if (projectionIn != null && projectionIn.length > 0) {
            projection = new String[projectionIn.length + 1];
            System.arraycopy(projectionIn, 0, projection, 0, projectionIn.length);
            projection[projectionIn.length] = "_id AS _id";
        }

        StringBuilder whereClause = new StringBuilder(256);
        if (match == URI_MATCH_NOTIFICATION_ID) {
            whereClause.append("(_id = ").append(uri.getPathSegments().get(1)).append(")");
        }

        if (selection != null && selection.length() > 0) {
            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }

            whereClause.append('(');
            whereClause.append(selection);
            whereClause.append(')');
        }

        Cursor c =
                db.query(TABLE_NAMES[match % 10], projection, whereClause.toString(),
                        selectionArgs, null, null, sortOrder, null);
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_MATCH_NOTIFICATION:
                return "vnd.android.cursor.dir/" + TABLE_NAMES[URI_MATCH_NOTIFICATION];
            case URI_MATCH_NOTIFICATION_ID:
                return "vnd.android.cursor.item/" + TABLE_NAMES[URI_MATCH_NOTIFICATION];
            default:
                throw new IllegalArgumentException("Unknown URL");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = URI_MATCHER.match(uri);
        Uri retUri = null;
        switch (match) {
            case URI_MATCH_NOTIFICATION: {
                long rowID = db.insert(TABLE_NAMES[URI_MATCH_NOTIFICATION], "content", values);
                if (rowID > 0) {
                    retUri = ContentUris.withAppendedId(NOTIFICATION_URI, rowID);
                }
                break;
            }

            default:
                throw new IllegalArgumentException("Unknown URL: " + uri);
        }

        if (retUri == null) {
            throw new IllegalArgumentException("Unknown URL: " + uri);
        }
        getContext().getContentResolver().notifyChange(retUri, null);

        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = URI_MATCHER.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException("Unknown URL");
        }

        if (match == URI_MATCH_NOTIFICATION_ID) {
            StringBuilder sb = new StringBuilder();
            if (selection != null && selection.length() > 0) {
                sb.append("( ");
                sb.append(selection);
                sb.append(" ) AND ");
            }
            String id = uri.getPathSegments().get(1);
            sb.append("_id = ");
            sb.append(id);
            selection = sb.toString();
        }

        int count = db.delete(TABLE_NAMES[match % 10], selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = URI_MATCHER.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException("Unknown URL");
        }

        if (match == URI_MATCH_NOTIFICATION_ID) {
            StringBuilder sb = new StringBuilder();
            if (selection != null && selection.length() > 0) {
                sb.append("( ");
                sb.append(selection);
                sb.append(" ) AND ");
            }
            String id = uri.getPathSegments().get(1);
            sb.append("_id = ");
            sb.append(id);
            selection = sb.toString();
        }

        int ret = db.update(TABLE_NAMES[match % 10], values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }

    public class DataColumns implements BaseColumns {
        public static final String NAME = "name";
        public static final String CONTENT = "content";
        public static final String TYPE = "type";
        public static final String TIME = "time";
        public static final String HASH = "hash";
        public static final String SYNC = "sync";

        public static final int INDEX_ID = 0;
        public static final int INDEX_NAME = 1;
        public static final int INDEX_CONTENT = 2;
        public static final int INDEX_TYPE = 3;
        public static final int INDEX_TIME = 4;
        public static final int INDEX_HASH = 5;
        public static final int INDEX_SYNC = 6;

        public static final int TYPE_TEXT = 1;
        public static final int TYPE_VOICE = 2;
        public static final int TYPE_IMAGE = 3;

        public static final int SYNC_DEFAULT = 0;
        public static final int SYNC_DONE = 1;
        public static final int SYNC_ING = 2;
    }
}
