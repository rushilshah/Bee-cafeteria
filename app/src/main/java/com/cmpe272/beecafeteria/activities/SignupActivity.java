package com.cmpe272.beecafeteria.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmpe272.beecafeteria.R;
import com.cmpe272.beecafeteria.base.App;
import com.cmpe272.beecafeteria.modelResponse.PostResponse;
import com.cmpe272.beecafeteria.network.GsonPostRequest;
import com.cmpe272.beecafeteria.network.RegisterApiRequests;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rushil on 11/12/2015.
 */
public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_name)
    EditText _nameText;
    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.input_cellNumber)
    EditText _phoneNo;
    @Bind(R.id.input_confirm_password)
    EditText _confirmPasswordText;
    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.link_login)
    TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String strUsername = _nameText.getText().toString();
        String strEmail = _emailText.getText().toString();
        String strPassword = _passwordText.getText().toString();
        String strConfirmPassword = _confirmPasswordText.getText().toString();
        String strCellNo = _phoneNo.getText().toString();

        final GsonPostRequest gsonPostRequest =
                RegisterApiRequests.postRegisterrequest
                        (
                                new Response.Listener<PostResponse>() {
                                    @Override
                                    public void onResponse(PostResponse dummyObject) {
                                        // Deal with the DummyObject here
                                        //mProgressBar.setVisibility(View.GONE);
                                        //mContent.setVisibility(View.VISIBLE);
                                        ///setData(dummyObject);
                                        //_signupButton.setEnabled(true);
                                        progressDialog.dismiss();

                                        onSignupSuccess();
                                    }
                                }
                                ,
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // Deal with the error here
                                        //mProgressBar.setVisibility(View.GONE);
                                        //mErrorView.setVisibility(View.VISIBLE);
                                        progressDialog.dismiss();
                                        onSignupFailed();
                                    }
                                },
                                strUsername, strPassword, strEmail, strCellNo
                        );

        App.addRequest(gsonPostRequest, TAG);

    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign up failed! try again", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmPassword = _confirmPasswordText.getText().toString();
        String cellNo = _phoneNo.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (password.isEmpty() || confirmPassword.isEmpty() || !password.equals(confirmPassword)) {
            _passwordText.setError("Password and Confirm Password doesn't match.");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (cellNo.isEmpty()) {
            _phoneNo.setError("Enter a valid phone number");
            valid = false;
        } else {
            _phoneNo.setError(null);
        }

        return valid;
    }
}
