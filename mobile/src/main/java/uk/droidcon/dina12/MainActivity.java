package uk.droidcon.dina12;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.esolutions.applibs.provider.Config;
import uk.droidcon.dina12.media.AudiController;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ButterKnife.bind(this);

    Config.ENABLE_MOCK_PROVIDERS = true;
    //if (true) {
    //  return;
    //}

    if (AudiController.getInstance(this).isHost()) {
      startActivity(new Intent(this, GameActivity.class));
    } else {
      startActivity(new Intent(this, RegistrationActivity.class));
    }
    finish();
  }

  @OnClick(R.id.driverButton) public void onClickDriverButton() {
    startActivity(new Intent(this, GameActivity.class));
  }

  @OnClick(R.id.userButton) public void onClickUserButton() {
    Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
    startActivity(intent);
  }
}
