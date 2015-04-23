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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LgcDBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "wxlive.db";
    public static final String TABLE_NAME = "notifications";

    public LgcDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + LgcProvider.DataColumns._ID
                + " INTEGER PRIMARY KEY, " + LgcProvider.DataColumns.NAME + " TEXT, "
                + LgcProvider.DataColumns.CONTENT + " TEXT, " + LgcProvider.DataColumns.TYPE
                + " INTEGER, " + LgcProvider.DataColumns.TIME + " INTEGER, "
                + LgcProvider.DataColumns.HASH + " TEXT, " + LgcProvider.DataColumns.SYNC
                + " INTEGER " + " );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
