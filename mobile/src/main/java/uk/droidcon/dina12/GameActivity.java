package uk.droidcon.dina12;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.util.Random;

import uk.droidcon.dina12.uk.droidcon.dina12.model.Question;

public class GameActivity extends AppCompatActivity implements MessageApi.MessageListener {

	private static final String TAG = "GameActivity";
	private static final String SEND_CATEGORY_PATH = "/send_category";
	private static final String FIREBASE_URL = "https://dina12.firebaseio.com/";
	private Firebase myFirebaseRef;
	private GoogleApiClient mGoogleApiClient;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_host);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
					@Override
					public void onConnected(Bundle bundle) {
						Log.d(TAG, "onConnected");
						Wearable.MessageApi.addListener(mGoogleApiClient, GameActivity.this);
					}

					@Override
					public void onConnectionSuspended(int i) {
						Log.d(TAG, "onConnectionSuspended");
					}
				})
				.addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
					@Override
					public void onConnectionFailed(ConnectionResult connectionResult) {
						Log.d(TAG, "onConnectionFailed");
					}
				})
				.addApi(Wearable.API)
				.build();

	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Wearable.MessageApi.removeListener(mGoogleApiClient, this);
		mGoogleApiClient.disconnect();
	}

    private void getRandomQuestion (String category, final String sessionGame)
    {

        // open a read/write connection to Firebase
        myFirebaseRef = new Firebase(FIREBASE_URL);


        //Log.d("CUCURIGU", "child count:" + myFirebaseRef.);

        //the firebase does not allow us count for queries so we just hardcode this
        //to implement it as they suggest on http://goo.gl/k6FEcL is just stupid
        int countMathematics = 3;
        int countFun= 5;

        //calculate the random
        final int randomQuestionCounter;
        if ("mathematics".equals(category))
            randomQuestionCounter = randomWithRange(1,countMathematics);
        else
            randomQuestionCounter = randomWithRange(1, countFun);

        //take the questions from the DB based on the key
        Query queryRef = myFirebaseRef.child("questions").child(category).orderByKey().startAt(String.valueOf(randomQuestionCounter)).endAt(String.valueOf(randomQuestionCounter));
        Log.d(TAG, "we work on:" + randomQuestionCounter);

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                //read the active question
                Question randomQuestion = snapshot.getValue(Question.class);
                Log.d(TAG, snapshot.getKey() + " randomQuestion:" + randomQuestion.getQuestion_text());

                //write the active question in a session game
                myFirebaseRef.child("game_sessions").child(sessionGame).push().setValue(randomQuestion);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }

        });
    }

	int randomWithRange(int min, int max) {
		int range = (max - min) + 1;
		return (int)(Math.random() * range) + min;
	}

	@Override
	public void onMessageReceived(MessageEvent messageEvent) {
		Log.d(TAG, "Got message event with path " + messageEvent.getPath());
		if (SEND_CATEGORY_PATH.equals(messageEvent.getPath())) {
			String categ = new String(messageEvent.getData());
			Log.d(TAG, "Got category: " + categ);
			getRandomQuestion(categ, "game_droidcon");
		}
	}
}
