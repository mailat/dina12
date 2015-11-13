package uk.droidcon.dina12;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by mailat on 31.10.15.
 */
public class DinaApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the Firebase and open a read/write connection
        Firebase.setAndroidContext(this);
    }

}
