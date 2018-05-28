package io.github.mionick.set.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import io.github.mionick.set.R;

public class MenuActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    Intent recordIntent = new Intent(getApplicationContext(), RecordActivity.class);
                    startActivity(recordIntent);
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void StartOnePlayerGame(View view) {
        Intent startGameIntent = new Intent(this, OnePlayerGameActivity.class);
        startActivity(startGameIntent);
    }

    public void StartMultiPlayerGame(View view) {
        Intent startGameIntent = new Intent(this, MultiPlayerLobbyActivity.class);
        startActivity(startGameIntent);
    }

}
