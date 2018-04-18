package com.example.geoxplore.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geoxplore.MainActivity;
import com.example.geoxplore.R;
import com.example.geoxplore.api.ApiUtils;
import com.example.geoxplore.api.model.SecurityToken;
import com.example.geoxplore.api.model.UserCredentials;
import com.example.geoxplore.api.service.UserService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
                loginWithApi(mLogin.getText().toString(), mPassword.getText().toString());

            }
        });

        mForgottenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //TODO przypominanie hasła (potrzebne API)
            }
        });

        Intent intent = getIntent();

        if(intent!=null && intent.hasExtra(Intent.EXTRA_USER)){
            mLogin.setText(intent.getStringExtra(Intent.EXTRA_USER));
        }
    }

    private void loginWithApi(String username, String password){
        ApiUtils
                .getService(UserService.class)
                .login(new UserCredentials(username, password))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(x -> new SecurityToken("ERROR"))
                .subscribe(x -> httpStatusService(x.getToken()));
    }

    public void httpStatusService(final String token){
        switch(token){
            case "ERROR":{
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                break;
            }
            default: {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(Intent.EXTRA_USER, token);
                startActivity(intent);
            }
        }

    }


}
