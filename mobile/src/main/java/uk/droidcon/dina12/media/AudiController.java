package uk.droidcon.dina12.media;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import de.audi.auditablet.contract.remote.NowPlayingMediaContract;
import de.audi.auditablet.contract.remote.NowPlayingMediaContract.Selectors;
import uk.droidcon.dina12.utils.Lg;

/**
 * Created by Arne Poths (mail@arnepoths.de) on 31/10/15.
 */
public final class AudiController {

    private String TAG = AudiController.class.getSimpleName();

    private static AudiController sInstance;

    private ContentResolver mResolver;
    private Context mContext;

    private AudiController(Context context) {
        // hide
        this.mContext = context;
        this.mResolver = mContext.getContentResolver();
    }

    public static AudiController getInstance(FragmentActivity context) {
        if (sInstance == null) {
            sInstance = new AudiController(context);
        }

        sInstance.mContext = context;

        return sInstance;
    }

    public boolean isHost() {

        Cursor c = mResolver.query(getUri(Selectors.TRACK_LIST), null, null, null, null);
        boolean isHost = false;
        if (c == null) {
            isHost = true;
        } else {
            c.close();
        }

        Log.d(TAG, "ishost(" + isHost + ")");
        return isHost;
    }

    Cursor mCursor;

    public Cursor getMusicFiles() {

        Uri uri = getUri(Selectors.TRACK_LIST);

        Log.d(TAG, "Uri: (" + uri + ")");
        mCursor = mResolver.query(uri, null, null, null, null);

        Lg.columns(TAG, mCursor);
        Lg.log(TAG, mCursor);
        return mCursor;
    }

    private Uri getUri(String contentType) {
        Uri auth = Uri.parse("content://" + Constants.NOW_PLAYING_AUTHORITY);
        return Uri.withAppendedPath(auth, contentType);
    }

    public void selectSong(int position) {
        // position is ignored for now.
        ContentValues values = new ContentValues();
        values.put(NowPlayingMediaContract.TrackIndexColumns.TRACK_INDEX, position);
        int rows = mResolver.update(getUri(Selectors.REQUEST_TRACK), values, null, null);
        Log.d(TAG, "Updated rows(" + rows + ")");
    }
}
