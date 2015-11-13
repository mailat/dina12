package uk.droidcon.dina12;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ButterKnife.bind(this);




    //  startActivity(new Intent(this, GameActivity.class));
      startActivity(new Intent(this, RegistrationActivity.class));
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
