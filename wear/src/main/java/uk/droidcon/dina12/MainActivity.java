package uk.droidcon.dina12;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.DelayedConfirmationView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends WearableActivity implements MessageApi.MessageListener {

    private static final String TAG = "MainActivity";

    private static final String MODERATE_QUESTIONS_CAPABILITY_NAME = "moderate_questions";
    private static final String SEND_CATEGORY_PATH = "/send_category";

    private GoogleApiClient mGoogleApiClient;

    private BoxInsetLayout mContainerView;
    private Button mStartGameButton;
    private View mCategoryLayout;
    private TextView mCategoryTextView;
    private DelayedConfirmationView mDelayedView;
    private String mSelectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mStartGameButton = (Button) findViewById(R.id.start_game);
        mStartGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySpeechRecognizer();
            }
        });

        mDelayedView = (DelayedConfirmationView) findViewById(R.id.delayed_confirm);
        mDelayedView.setListener(mDelayedConfirmationListener);

        mCategoryTextView = (TextView) findViewById(R.id.selected_category);

        mCategoryLayout = findViewById(R.id.category_layout);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d(TAG, "onConnected");
                        Wearable.MessageApi.addListener(mGoogleApiClient, MainActivity.this);
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

    private DelayedConfirmationView.DelayedConfirmationListener mDelayedConfirmationListener =
            new DelayedConfirmationView.DelayedConfirmationListener() {
                @Override
                public void onTimerFinished(View view) {
                    Intent intent = new Intent(MainActivity.this, ConfirmationActivity.class);
                    intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                            ConfirmationActivity.SUCCESS_ANIMATION);
                    intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                            getString(R.string.msg_sent));
                    startActivity(intent);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            CapabilityApi.GetCapabilityResult capabilityResult = Wearable.CapabilityApi
                                    .getCapability(mGoogleApiClient, MODERATE_QUESTIONS_CAPABILITY_NAME,
                                            CapabilityApi.FILTER_REACHABLE)
                                    .await();

                            if (capabilityResult.getStatus().isSuccess()) {
                                sendCategory(capabilityResult.getCapability());
                            } else {
                                Log.e(TAG, "Failed to get capabilities, status: "
                                        + capabilityResult.getStatus().getStatusMessage());
                            }
                        }
                    }).start();
                    mStartGameButton.setVisibility(View.VISIBLE);
                    mCategoryLayout.setVisibility(View.GONE);
                }

                @Override
                public void onTimerSelected(View view) {
//                    displaySpeechRecognizer();
                    mStartGameButton.setVisibility(View.VISIBLE);
                    mCategoryLayout.setVisibility(View.GONE);
                }
            };

    private void sendCategory(CapabilityInfo capabilityInfo) {
        Set<Node> connectedNodes = capabilityInfo.getNodes();
        if (connectedNodes.isEmpty()) {
            Log.w(TAG, "No node capable of moderating questions was found");
        } else {
            for (Node node : connectedNodes) {
                Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), SEND_CATEGORY_PATH,
                        mSelectedCategory.getBytes());
            }
            Log.d(TAG, "Message sent to " + connectedNodes.size() + " devices capable of moderating questions");
        }
    }

    private void confirmText(String spokenText) {
        mStartGameButton.setVisibility(View.GONE);
        mCategoryLayout.setVisibility(View.VISIBLE);

        mCategoryTextView.setText("Selected category: " + spokenText);
        mSelectedCategory = spokenText;

        mDelayedView.setTotalTimeMs(3000);
        mDelayedView.start();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
//            mStartGameButton.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            mContainerView.setBackground(null);
//            mStartGameButton.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private static final int SPEECH_REQUEST_CODE = 0;

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            confirmText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }
}
