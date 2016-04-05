package ru.vat78.fotimetracker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;


import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import ru.vat78.fotimetracker.connectors.fo_api.FOAPI_Exceptions;
import ru.vat78.fotimetracker.controllers.FOTT_Exceptions;

/**
 * A login screen that offers login via login/password.
 */
public class FOTT_LoginActivity extends AppCompatActivity implements FOTT_ActivityInterface {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private static final String CLASS_NAME = "FOTT_LoginActivity";

    private FOTT_App app;
    private FOTT_WebSyncTask ULC;

    // UI references.
    private AutoCompleteTextView mURLView;
    private AutoCompleteTextView mLoginView;
    private EditText mPasswordView;
    private CheckBox mUntrustCA;
    private CheckBox mSaveCred;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    public void onPostExecuteWebSyncing(FOTT_Exceptions result) {

        ULC = null;
        showProgress(false);
        if (result == null) {
            saveResultsAndFinish();
        } else {
            errorHandler(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hide back button on action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        app = (FOTT_App) getApplication();

        // Set up the login form.
        setContentView(R.layout.activity_login);

        mURLView = (AutoCompleteTextView) findViewById(R.id.fo_url);
        mLoginView = (AutoCompleteTextView) findViewById(R.id.login);
        mUntrustCA = (CheckBox) findViewById(R.id.untrustCA);
        mSaveCred = (CheckBox) findViewById(R.id.save_cred);
        mPasswordView = (EditText) findViewById(R.id.password);


                mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        if (!mPasswordView.getText().toString().isEmpty()) {attemptLogin();}
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getInitialParams();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (ULC!=null) {
            return;
        }

        // Reset errors.
        mURLView.setError(null);
        mLoginView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String[] values = new String[FOTT_WebSyncTask.PARAM_CNT];
        values[FOTT_WebSyncTask.URL] = mURLView.getText().toString();
        values[FOTT_WebSyncTask.LOGIN] = mLoginView.getText().toString();
        values[FOTT_WebSyncTask.PASSWORD] = mPasswordView.getText().toString();
        if (mUntrustCA.isChecked()) {
            values[FOTT_WebSyncTask.CERTIFICATES] = FOTT_WebSyncTask.ANY_CERTS;
        } else {
            values[FOTT_WebSyncTask.CERTIFICATES] = FOTT_WebSyncTask.BOOL_TRUE;
        }

        boolean cancel = false;
        View focusView = null;

        ULC = new FOTT_WebSyncTask(this);

        // Check for a empty url.
        if (TextUtils.isEmpty(values[FOTT_WebSyncTask.URL])) {
            mURLView.setError(getString(R.string.error_field_required));
            focusView = mURLView;
            cancel = true;
        }

        if (!cancel) {

            // Check for a valid password, if the user entered one.
            if (TextUtils.isEmpty(values[FOTT_WebSyncTask.PASSWORD])) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }
        }

        if (!cancel) {
            // Check for a empty login.
            if (TextUtils.isEmpty(values[FOTT_WebSyncTask.LOGIN])) {
                mLoginView.setError(getString(R.string.error_field_required));
                focusView = mLoginView;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            ULC = null;
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            ULC.execute(values);
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void getInitialParams() {

        Intent intent = getIntent();

        mURLView.setText(intent.getStringExtra("" + FOTT_WebSyncTask.URL));
        mLoginView.setText(intent.getStringExtra("" + FOTT_WebSyncTask.LOGIN));
        mUntrustCA.setChecked(intent.getStringExtra("" + FOTT_WebSyncTask.CERTIFICATES).equals(FOTT_WebSyncTask.ANY_CERTS));

        mPasswordView.setText(intent.getStringExtra("" + FOTT_WebSyncTask.PASSWORD));
        mSaveCred.setChecked(intent.getStringExtra("" + FOTT_WebSyncTask.SAVE_CREDENTIALS).equals(FOTT_WebSyncTask.BOOL_TRUE));
    }

    private void saveResultsAndFinish() {

        Intent intent = new Intent();

        intent.putExtra("" + FOTT_WebSyncTask.URL, mURLView.getText().toString());
        intent.putExtra("" + FOTT_WebSyncTask.LOGIN, mLoginView.getText().toString());
        if (mUntrustCA.isChecked()) {
            intent.putExtra("" + FOTT_WebSyncTask.CERTIFICATES, FOTT_WebSyncTask.ANY_CERTS);
        } else {
            intent.putExtra("" + FOTT_WebSyncTask.CERTIFICATES, FOTT_WebSyncTask.BOOL_TRUE);
        }

        if (mSaveCred.isChecked()) {
            intent.putExtra("" + FOTT_WebSyncTask.PASSWORD, mPasswordView.getText().toString());
            intent.putExtra("" + FOTT_WebSyncTask.SAVE_CREDENTIALS, FOTT_WebSyncTask.BOOL_TRUE);
        } else {
            intent.putExtra("" + FOTT_WebSyncTask.PASSWORD, "");
            intent.putExtra("" + FOTT_WebSyncTask.SAVE_CREDENTIALS, "");
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    private void errorHandler(FOTT_Exceptions error) {

        TextView focusView = null;
        if (error.getLevel() == FOTT_Exceptions.ExceptionLevels.CRITICAL) {
            FOAPI_Exceptions apiError = null;
            try {
                apiError = (FOAPI_Exceptions) error;
            } catch (Exception e) {
                focusView = mURLView;
            }

            if (apiError != null) {
                switch (apiError.getErrorCode()) {
                    case CREDENTIAL_ERROR:
                        focusView = mLoginView;
                        break;
                    default:
                        focusView = mURLView;
                        break;
                }
            }
        }

        if (focusView != null )
            showErrorAlert(focusView, error.getLocalizedMessage());
    }

    private void showErrorAlert(TextView errorView, String errorText) {
        errorView.setError(errorText);
        errorView.requestFocus();
    }

}

