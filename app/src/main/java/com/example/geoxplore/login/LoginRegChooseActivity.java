package com.example.geoxplore.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.example.geoxplore.R;
import com.example.geoxplore.api.model.UserCredentials;
import com.example.geoxplore.utils.SavedData;

public class LoginRegChooseActivity extends AppCompatActivity {

    CardView mLoginButton;
    CardView mRegistryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_reg_choose);

        mLoginButton = (CardView) findViewById(R.id.cv_loginButton);
        mRegistryButton = (CardView) findViewById(R.id.cv_registryButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });

        mRegistryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegActivity();
            }
        });

        autoLogin();
    }

    private void openLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void openRegActivity(){
        Intent intent = new Intent(this, RegActivity.class);
        startActivity(intent);
    }

    private void autoLogin() {
        UserCredentials credentials = SavedData.getLoggedUserCredentials(getApplicationContext());
        if (UserCredentials.validate(credentials)) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(LoginActivity.USERNAME, credentials.getUsername());
            intent.putExtra(LoginActivity.PASSWORD, credentials.getPassword());
            startActivity(intent);
        }
    }
}
