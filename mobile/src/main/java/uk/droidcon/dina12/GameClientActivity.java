package uk.droidcon.dina12;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;
import butterknife.OnLongClick;
import com.bumptech.glide.Glide;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.droidcon.dina12.uk.droidcon.dina12.model.Question;
import uk.droidcon.dina12.uk.droidcon.dina12.model.User;

public class GameClientActivity extends AppCompatActivity {

    private static final String FIREBASE_URL = "https://dina12.firebaseio.com/";

    private Firebase myFirebaseRef;

    private User mUser;

    @Bind(R.id.game_question_layout)
    View mQuestionLayout;

    @Bind(R.id.game_image_left)
    ImageView mImageLeft;

    @Bind(R.id.game_image_right)
    ImageView mImageRight;

    @Bind(R.id.game_question_text)
    TextView mQuestionTextView;

    @Bind(R.id.game_waiting_text)
    View mWaitingTextView;

    private Question mQuestion;
    private int mCorrectQuestions = 0;
    private boolean mIsNewQuestion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_client);

        ButterKnife.bind(this);

        mUser = getIntent().getParcelableExtra("user");

        myFirebaseRef = new Firebase(FIREBASE_URL);

        myFirebaseRef.child("game_sessions").child("game_droidcon").addChildEventListener(
            new ChildEventListener() {
                @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    mQuestion = dataSnapshot.getValue(Question.class);
                    mWaitingTextView.setVisibility(View.GONE);
                    mQuestionLayout.setVisibility(View.VISIBLE);
                    mQuestionTextView.setText(mQuestion.getQuestion_text());
                    Glide.with(GameClientActivity.this)
                        .load(mQuestion.getImage_left())
                        .into(mImageLeft);
                    Glide.with(GameClientActivity.this)
                        .load(mQuestion.getImage_right())
                        .into(mImageRight);
                    mIsNewQuestion = true;
                }

                @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override public void onCancelled(FirebaseError firebaseError) {
                }
            });
    }

    @OnClick({R.id.game_image_left, R.id.game_image_right})
    void answerClicked(View view) {

        if (!mIsNewQuestion) {
            // no cheating
            return;
        }

        boolean correct =
                ("image_right".equals(mQuestion.getCorrect_answer()) && view.getId() == R.id.game_image_right) ||
                ("image_left".equals(mQuestion.getCorrect_answer()) && view.getId() == R.id.game_image_left);

        String toast = "Your answer is " + ( correct ? "correct." : "incorrect" );

        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();

        if (correct) {
          mCorrectQuestions++;

          if (mCorrectQuestions % 3 == 0) {
              new TracksDialog().show(getSupportFragmentManager(), "winnersDialog");
          }
        }
        mIsNewQuestion = false;
    }

    @OnLongClick(R.id.game_question_text)
    public boolean questionClick() {
        new TracksDialog().show(getSupportFragmentManager(), "winnersDialog");
        return true;
    }

}
