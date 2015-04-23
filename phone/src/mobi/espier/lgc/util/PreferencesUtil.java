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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesUtil {

    public static boolean getBoolean(Context context, String key, boolean def) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(key, def);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editPrefs =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        editPrefs.putBoolean(key, value);
        editPrefs.commit();
    }

    public static String getString(Context context, String key, String def) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key, def);
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences.Editor editPrefs =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        editPrefs.putString(key, value);
        editPrefs.commit();
    }

    public static Long getLong(Context context, String key, Long def) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(key, def);
    }

    public static void putLong(Context context, String key, Long value) {
        SharedPreferences.Editor editPrefs =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        editPrefs.putLong(key, value);
        editPrefs.commit();
    }

    public static int getInt(Context context, String key, int def) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(key, def);
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences.Editor editPrefs =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        editPrefs.putInt(key, value);
        editPrefs.commit();
    }

    public static float getFloat(Context context, String key, float def) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getFloat(key, def);
    }

    public static void putFloat(Context context, String key, int value) {
        SharedPreferences.Editor editPrefs =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        editPrefs.putFloat(key, value);
        editPrefs.commit();
    }
}
