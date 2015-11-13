package uk.droidcon.dina12.utils;

import android.database.Cursor;
import android.util.Log;

import uk.droidcon.dina12.BuildConfig;

/**
 * Created by Arne Poths (mail@arnepoths.de) on 31/10/15.
 */
public final class Lg {

    private Lg() {
    }

    public static void columns(String tag, Cursor c) {
        if (!BuildConfig.DEBUG) {
            return;
        }

        if (c == null) {
            Log.d(tag, "{cursor is null}");
            return;
        }

        String log = "";
        String[] columns = c.getColumnNames();

        if (columns != null || columns.length > 0) {
            log += "[";
            for (int i = 0, count = columns.length; i < count; i++) {
                log += columns[i] + ", ";
            }
            log += "]";
        } else {
            log += " no columns found ";
        }
        log += "}";
        Log.d(tag, log);
    }

    public static void log(String tag, Cursor c) {
        if (!BuildConfig.DEBUG) {
            return;
        }

        if (c == null) {
            Log.d(tag, "{cursor is null}");
            return;
        }

        String log = "{";

        String[] columns = c.getColumnNames();

        if (c.moveToFirst()) {
            do {
                log += "[";
                for (int i = 0, count = columns.length; i < count; i++) {
                    log += c.getString(c.getColumnIndex(columns[i])) + ", ";
                }
                log += "]";
            } while (c.moveToNext());
        } else {
            log += " CURSOR IS EMPTY! ";
        }
        log += "}";

        Log.d(tag, log);
    }
}
