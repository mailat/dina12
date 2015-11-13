package uk.droidcon.dina12;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.droidcon.dina12.uk.droidcon.dina12.model.User;
public class RegistrationActivity extends AppCompatActivity {

	private NfcAdapter nfcAdapter;

	private PendingIntent pendingIntent;
	private IntentFilter[] intentFiltersArray;
	private String[][] techList;

	private User user;

	@Bind(R.id.progressBar)	LinearLayout progressBar;
	@Bind(R.id.welcome) LinearLayout welcome;
	@Bind(R.id.registrationForm) LinearLayout registrationForm;
	@Bind(R.id.welcomeEmail) TextView welcomeEmail;
	@Bind(R.id.registerEmail) EditText registerEmail;
	@Bind(R.id.registerButton) Button registerButton;
	@Bind(R.id.welcomeAvatar) ImageView welcomeAvatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		ButterKnife.bind(this);

		pendingIntent = PendingIntent.getActivity(
			this,
			0,
			new Intent(
				this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
			0
		);

		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndef.addDataType("*/*");
		} catch (IntentFilter.MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}

		IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		try {
			tech.addDataType("*/*");
		} catch (IntentFilter.MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}

		IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		try {
			tag.addDataType("*/*");
		} catch (IntentFilter.MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}

		intentFiltersArray = new IntentFilter[] {tag, ndef, tech};

		techList = new String[][] {
			new String[] {
				NfcA.class.getName(),
				NfcB.class.getName(),
				NfcF.class.getName(),
				NfcV.class.getName(),
				IsoDep.class.getName(),
				MifareClassic.class.getName(),
				MifareUltralight.class.getName(),
				Ndef.class.getName()
			}
		};
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		nfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techList);
	}

	@Override
	public void onNewIntent(Intent intent) {
		final Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		String tagInfo = DinaTagUtil.read(tag);
		Log.i("tagInfo", tagInfo);

		//write deactivated
		//DinaTagUtil.write(tag, "emaik 7hash");

		try {
			final User user = new User(tagInfo);
			user.update();

			Firebase ref = new Firebase("https://dina12.firebaseio.com");
			// Create a handler to handle the result of the authentication
			Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
				@Override
				public void onAuthenticated(AuthData authData) {
					Toast.makeText(getApplicationContext(), "Authenticated!", Toast.LENGTH_LONG).show();
					user.uid = authData.getUid();
					RegistrationActivity.this.user = user;
					welcomeUser();
				}
				@Override
				public void onAuthenticationError(FirebaseError firebaseError) {
					Toast.makeText(getApplicationContext(), "This account does not exist!", Toast.LENGTH_LONG).show();

					showRegistration();
				}
			};

			ref.authWithPassword(user.email, user.token, authResultHandler);
		} catch(User.InvalidUserInfo invalidUserInfo) {
			Toast.makeText(getApplicationContext(), "Invalid user!", Toast.LENGTH_LONG).show();
			showRegistration();
		}
	}

	public void welcomeUser() {
		progressBar.setVisibility(View.GONE);
		registrationForm.setVisibility(View.GONE);
		welcome.setVisibility(View.VISIBLE);

		welcomeEmail.setText(user.email);
		Log.i("avatarLink", user.getAvatarLink());
		Glide.with(this).load(user.getAvatarLink()).into(welcomeAvatar);
	}

	public void showRegistration() {
		progressBar.setVisibility(View.GONE);
		welcome.setVisibility(View.GONE);
		registrationForm.setVisibility(View.VISIBLE);
	}

	@OnClick(R.id.registerButton)
	public void submitRegistration() {
		String email = registerEmail.getText().toString();
		Toast.makeText(getApplicationContext(), "Registered!", Toast.LENGTH_LONG).show();
	}

	@OnClick(R.id.welcomeStartGame)
	public void startGame() {
		Intent intent = new Intent(this, GameClientActivity.class);
		intent.putExtra("user", user);
		startActivity(intent);
	}
}
