package com.example.geoxplore.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geoxplore.MainActivity;
import com.example.geoxplore.R;
import com.example.geoxplore.api.ApiUtils;
import com.example.geoxplore.api.model.SecurityToken;
import com.example.geoxplore.api.model.UserCredentials;
import com.example.geoxplore.api.service.UserService;
import com.example.geoxplore.utils.SavedData;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    private EditText mLogin;
    private EditText mPassword;
    private TextView mForgottenPassword;

    private CardView mLoginButtonCV;

    @BindView(R.id.login_activity_loading_bar)
    ProgressBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mLogin = (EditText) findViewById(R.id.et_login);
        mPassword = (EditText) findViewById(R.id.et_password);

        mForgottenPassword = (TextView) findViewById(R.id.tv_forgottenPassword);
        mLoginButtonCV = (CardView) findViewById(R.id.cv_loginButton);

        loginUsingSavedCredentials();

        mLoginButtonCV.setOnClickListener(v -> {
            String login = mLogin.getText().toString();
            String password = mPassword.getText().toString();
            UserCredentials credentials = new UserCredentials(login, password);
            loginWithApi(credentials);

        });

        mForgottenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO przypominanie hasła (potrzebne API)
            }
        });

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_USER)) {
            mLogin.setText(intent.getStringExtra(Intent.EXTRA_USER));
        }
    }

    private void loginUsingSavedCredentials(){
        try{
            Bundle bundle = getIntent().getExtras();
            String username = bundle.getString(USERNAME);
            String password = bundle.getString(PASSWORD);
            if(username != null && password != null) {
                mLogin.setText(username);
                mPassword.setText(password);
                loginWithApi(new UserCredentials(username, password));
            }
        }
        catch (NullPointerException ex){
            //NO DATA TO LOGIN
        }
    }


    private void loginWithApi(final UserCredentials credentials) {
        loadingBar.setVisibility(View.VISIBLE);
        ApiUtils
                .getService(UserService.class)
                .login(credentials)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(x -> new SecurityToken("ERROR"))
                .subscribe(x -> httpStatusService(x.getToken(), credentials));
    }

    public void httpStatusService(final String token, final UserCredentials credentials) {
        switch (token) {
            case "ERROR": {
                Toast.makeText(getApplicationContext(), "Try again later", Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(), "Spróbuj ponownie później!", Toast.LENGTH_LONG).show();
                break;
            }
            default: {
                SavedData.saveLoggedUserCredentials(getApplicationContext(), credentials);
//                //Toast.makeText(getApplicationContext(), "Zalogowano!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(Intent.EXTRA_USER, token);
                startActivity(intent);
                loadingBar.setVisibility(View.INVISIBLE);

            }
        }

    }


}
