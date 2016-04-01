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

import java.util.HashMap;

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

        FOTT_Preferences preferences = app.getPreferences();
        mURLView.setText(preferences.getString(getString(R.string.pref_sync_url), ""));
        mLoginView.setText(preferences.getString(getString(R.string.pref_sync_login), ""));
        mUntrustCA.setChecked(preferences.getBoolean(getString(R.string.pref_sync_certs), true));

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setText(preferences.getString(getString(R.string.pref_sync_password), ""));
        mSaveCred.setChecked(preferences.getBoolean(getString(R.string.pref_sync_save_creds), false));

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
        HashMap<String, String> values = new HashMap<>(5);
        values.put("url", mURLView.getText().toString());
        values.put("login", mLoginView.getText().toString());
        values.put("password", mPasswordView.getText().toString());
        if (mUntrustCA.isChecked()) {
            values.put("sa", "");
        } else {
            values.put("sa", null);
        }

        boolean cancel = false;
        View focusView = null;

        ULC = new FOTT_WebSyncTask(this);

        // Check for a empty url.
        if (TextUtils.isEmpty(values.get("url"))) {
            mURLView.setError(getString(R.string.error_field_required));
            focusView = mURLView;
            cancel = true;
        }

        if (!cancel) {

            // Check for a valid password, if the user entered one.
            if (TextUtils.isEmpty(values.get("password"))) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }
        }

        if (!cancel) {
            // Check for a empty login.
            if (TextUtils.isEmpty(values.get("login"))) {
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

    private void saveResultsAndFinish() {
        FOTT_Preferences preferences = app.getPreferences();
        preferences.set(getString(R.string.pref_sync_url), mURLView.getText().toString());
        preferences.set(getString(R.string.pref_sync_login), mLoginView.getText().toString());
        preferences.set(getString(R.string.pref_sync_certs), mUntrustCA.isChecked());
        preferences.set(getString(R.string.pref_sync_save_creds), mSaveCred.isChecked());
        if (mSaveCred.isChecked()) {
            preferences.set(getString(R.string.pref_sync_password), mPasswordView.getText().toString());
        } else {
            preferences.set(getString(R.string.pref_sync_password), "");
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void errorHandler(FOTT_Exceptions error) {
        mURLView.requestFocus();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.

    public class UserLoginCheck extends AsyncTask<HashMap<String, String>, Void, Boolean> {


        @Override
        protected Boolean doInBackground(HashMap<String,String>... params) {

            HashMap<String,String> param = params[0];
            try {
                FOApp = FOAPI_Connector.getInstance(app, param.get("url"),
                        param.get("login"), param.get("password"), param.get("sa") == null);
            } catch (FOAPI_Exceptions e) {
                //ToDo: error handler here
                mLoginView.setError(e.toString());
            }
            app.setNeedFullSync(true);

            return app.dataSynchronization();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            ULC = null;
            showProgress(false);

            if (success) {
                FOTT_Preferences preferences = app.getPreferences();
                preferences.set(getString(R.string.pref_sync_url), mURLView.getText().toString());
                preferences.set(getString(R.string.pref_sync_login), mLoginView.getText().toString());
                preferences.set(getString(R.string.pref_sync_certs), mUntrustCA.isChecked());
                preferences.set(getString(R.string.pref_sync_save_creds), mSaveCred.isChecked());
                if (mSaveCred.isChecked()) {
                    preferences.set(getString(R.string.pref_sync_password), mPasswordView.getText().toString());
                } else {
                    preferences.set(getString(R.string.pref_sync_password), "");
                }
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            } else {
                //app.getError().error_handler(FOTT_ErrorsHandler.ERROR_SHOW_MESSAGE,CLASS_NAME,FOApp.getError());
                mURLView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            ULC = null;
            showProgress(false);
        }
    }
     */
}

