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

import java.lang.ref.WeakReference;

import android.os.Handler;

public class WeakHandlerTemplate<T> extends Handler {
    private final WeakReference<T> mContext;

    public WeakHandlerTemplate(T context) {
        mContext = new WeakReference<T>(context);
    }

    public WeakHandlerTemplate(T context, Callback cb) {
        super(cb);
        mContext = new WeakReference<T>(context);
    }

    protected T getObject() {
        if (mContext != null) return mContext.get();
        return null;
    }
}
