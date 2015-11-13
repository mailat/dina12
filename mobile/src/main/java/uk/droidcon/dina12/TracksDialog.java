package uk.droidcon.dina12;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import de.audi.auditablet.contract.remote.NowPlayingMediaContract;
import uk.droidcon.dina12.media.AudiController;

/**
 * Created by Arne Poths (mail@arnepoths.de) on 31/10/15.
 */
public class TracksDialog extends DialogFragment implements DialogInterface.OnClickListener {

  Cursor mCursor;

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), getTheme());

    builder.setTitle("You won!");

    mCursor = AudiController.getInstance(getActivity()).getMusicFiles();

    SimpleCursorAdapter adapter =
        new SimpleCursorAdapter(getContext(), R.layout.item_track, mCursor, new String[] {
            NowPlayingMediaContract.TrackRemoteListColumns.ARTIST,
            NowPlayingMediaContract.TrackRemoteListColumns.TITLE,
        }, new int[] { R.id.track_artist, R.id.track_name }, -1);
    builder.setSingleChoiceItems(adapter, -1, this);

    return builder.create();
  }

  @Override public void onClick(DialogInterface dialog, int which) {

    AudiController.getInstance(getActivity()).selectSong(which);
    String name = "";

    if (mCursor.moveToPosition(which)) {
      String artist = mCursor.getString(
          mCursor.getColumnIndex(NowPlayingMediaContract.TrackRemoteListColumns.ARTIST));
      String title = mCursor.getString(
          mCursor.getColumnIndex(NowPlayingMediaContract.TrackRemoteListColumns.TITLE));
      name = artist + " - " + title;
    }

    Toast.makeText(getActivity(), "Now playing " + name, Toast.LENGTH_LONG).show();
    mCursor.close();
    dialog.dismiss();
  }
}
