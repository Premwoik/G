package com.example.geoxplore.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.geoxplore.R;
import com.example.geoxplore.api.ApiUtils;
import com.example.geoxplore.api.model.UserRegistrationForm;
import com.example.geoxplore.api.service.UserService;
import com.example.geoxplore.utils.TextValidator;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RegActivity extends AppCompatActivity {
    private EditText mLogin;
    private EditText mPassword;
    private EditText mPasswordRepeat;
    private EditText mEmail;

    private boolean pass=false, rpass=false, login=false, email=false;

    private CardView mRegistryButton;


    public EditText getmPassword() {
        return mPassword;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);



        mRegistryButton = (CardView) findViewById(R.id.cv_register);


        mLogin = (EditText) findViewById(R.id.et_login);
        mPassword = (EditText) findViewById(R.id.et_password);
        mPasswordRepeat = (EditText) findViewById(R.id.et_passwordRepeat);
        mEmail = (EditText) findViewById(R.id.et_email);

        mLogin.addTextChangedListener(new TextValidator(mLogin) {
            @Override
            public void validate(EditText editText, String text) {
                if(text.isEmpty() || text.length()<4 || text.length()>16){
                    editText.setError(getResources().getString(R.string.ms_invalid_login));
                    login=false;
                }else{
                    login=true;
                }
            }
        });

        mPassword.addTextChangedListener(new TextValidator(mPassword) {
            @Override
            public void validate(EditText editText, String text) {
                //TODO Póki co nie trzeba
                pass = true;
            }
        });

        mPasswordRepeat.addTextChangedListener(new TextValidator(mPasswordRepeat) {
            @Override
            public void validate(EditText editText, String text) {
                final EditText password = getmPassword();
                if(!text.equals(password.getText().toString())){
                    editText.setError(getResources().getString(R.string.ms_different_passwords));
                    rpass = false;
                }else{
                    rpass = true;
                }
            }
        });

        mEmail.addTextChangedListener(new TextValidator(mEmail) {
            @Override
            public void validate(EditText editText, String text) {
                if(!TextValidator.isValidEmail(text)){
                    editText.setError(getResources().getString(R.string.ms_invalid_email));
                    email = false;
                }else{
                    email = true;
                }
            }
        });

        mRegistryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateAll()){
                    registerWithApi(mEmail.getText().toString(), mLogin.getText().toString(), mPassword.getText().toString());
                }else {
                    Toast.makeText(getApplicationContext(),"All data must be correct!",Toast.LENGTH_LONG).show();
                }

            }
        });


    }




    private void httpStatusService(final int status){
        switch(status) {
            case 409: {
                mLogin.setError(getResources().getString(R.string.ms_username_already_in_use));
                break;
            }
            case 200: {
                Toast.makeText(getApplicationContext(), "Udało się założyć konto, zaloguj się :)", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RegActivity.this, LoginActivity.class);
                intent.putExtra(Intent.EXTRA_USER, mLogin.getText());
                startActivity(intent);
                break;
            }
            case 500: {
                Toast.makeText(getApplicationContext(), "Error danych", Toast.LENGTH_LONG).show();
                break;
            }
            default:
                Toast.makeText(getApplicationContext(), "coś nie pykło", Toast.LENGTH_LONG).show();
        }
    }

    private void registerWithApi(String email, String username, String password){
        ApiUtils
                .getService(UserService.class)
                .register(new UserRegistrationForm(email, username, password))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(x-> {x.printStackTrace(); return null;})
                .subscribe(x-> httpStatusService(x.code()));
    }

    private boolean validateAll(){
        return login && pass && rpass && email;
    }
}
