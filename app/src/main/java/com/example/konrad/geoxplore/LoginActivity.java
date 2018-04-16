package com.example.konrad.geoxplore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    private EditText mLogin;
    private EditText mPassword;
    private TextView mForgottenPassword;

    private CardView mLoginButtonCV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLogin = (EditText) findViewById(R.id.et_login);
        mPassword = (EditText) findViewById(R.id.et_password);

        mForgottenPassword = (TextView) findViewById(R.id.tv_forgottenPassword);
        mLoginButtonCV = (CardView) findViewById(R.id.cv_loginButton);

        mLoginButtonCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Login button clicked", Toast.LENGTH_LONG).show();
                //TODO  zaimplementować logowanie
            }
        });

        mForgottenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Forgotten password text view clicked.", Toast.LENGTH_LONG).show();
                //TODO można to zrobić jak będzie API
            }
        });
    }


}
