package com.example.konrad.geoxplore;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.CardView;
        import android.text.TextWatcher;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.konrad.utils.TextValidator;

public class RegActivity extends AppCompatActivity {
    private EditText mLogin;
    private EditText mPassword;
    private EditText mPasswordRepeat;
    private EditText mEmail;

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
                }
            }
        });

        mPassword.addTextChangedListener(new TextValidator(mPassword) {
            @Override
            public void validate(EditText editText, String text) {
                //TODO #1 Póki co nie trzeba
            }
        });

        mPasswordRepeat.addTextChangedListener(new TextValidator(mPasswordRepeat) {
            @Override
            public void validate(EditText editText, String text) {
                final EditText password = getmPassword();
                if(!text.equals(password.getText().toString())){
                    editText.setError(getResources().getString(R.string.ms_different_passwords));
                }
            }
        });

        mEmail.addTextChangedListener(new TextValidator(mEmail) {
            @Override
            public void validate(EditText editText, String text) {
                if(!TextValidator.isValidEmail(text)){
                    editText.setError(getResources().getString(R.string.ms_invalid_email));
                }
            }
        });

        mRegistryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Rejestracja
                Toast.makeText(RegActivity.this, "Kliknięte", Toast.LENGTH_LONG).show();
            }
        });


    }
}
