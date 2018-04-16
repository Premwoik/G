package com.example.konrad.geoxplore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

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
    }

    private void openLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void openRegActivity(){
        Intent intent = new Intent(this, RegActivity.class);
        startActivity(intent);
    }
}
