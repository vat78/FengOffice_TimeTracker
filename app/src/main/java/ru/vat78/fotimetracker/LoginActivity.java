package ru.vat78.fotimetracker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.vat78.fotimetracker.FOConnector;

//import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via login/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    private FOConnector FOApp;
    private UserLoginCheck ULC;

    // UI references.
    private AutoCompleteTextView mURLView;
    private AutoCompleteTextView mLoginView;
    private EditText mPasswordView;
    private CheckBox mUntrustCA;
    private CheckBox mSaveCred;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        //TODO russian translate?
        //TODO save credentials
        mURLView = (AutoCompleteTextView) findViewById(R.id.fo_url);
        mLoginView = (AutoCompleteTextView) findViewById(R.id.login);
        mUntrustCA = (CheckBox) findViewById(R.id.untrustCA);
        mSaveCred = (CheckBox) findViewById(R.id.save_cred);

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        mURLView.setText(preferences.getString(getString(R.string.prompt_url),""));
        mLoginView.setText(preferences.getString(getString(R.string.prompt_login),""));
        mUntrustCA.setChecked(preferences.getBoolean(getString(R.string.UseUntrustCA), false));

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setText(preferences.getString(getString(R.string.prompt_password),""));
        mSaveCred.setChecked(TextUtils.isEmpty(mPasswordView.getText().toString()));

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
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (ULC != null) {
            return;
        }

        // Reset errors.
        mURLView.setError(null);
        mLoginView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String url = mURLView.getText().toString();
        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;
        FOApp = new FOConnector();
        ULC = new UserLoginCheck();

        // Check for a empty url.
        if (TextUtils.isEmpty(url)) {
            mURLView.setError(getString(R.string.error_field_required));
            focusView = mURLView;
            cancel = true;
        } else {
            cancel = !FOApp.setFo_Url(url);
            if (cancel) {
                mURLView.setError(getString(R.string.error_incorrect_value));
                focusView = mURLView;
            }
        }
        FOApp.UseUntrustCA = mUntrustCA.isChecked();

        //TODO empty password?
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else {
            cancel = !FOApp.setFO_Pwd(password);
            if (cancel) {
                mPasswordView.setError(getString(R.string.error_incorrect_value));
                focusView = mPasswordView;
            }
        }

        // Check for a empty login.
        if (TextUtils.isEmpty(login)) {
            mLoginView.setError(getString(R.string.error_field_required));
            focusView = mLoginView;
            cancel = true;
        } else {
            cancel = !FOApp.setFO_User(login);
            if (cancel) {
                mLoginView.setError(getString(R.string.error_incorrect_value));
                focusView = mLoginView;
            }
        }

        cancel = !isNetworkAvailable();
        if (cancel){
            mURLView.setError(getString(R.string.error_no_internet));
            focusView = mURLView;
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
            ULC.execute((Void) null);
        }
    }

    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
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

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginCheck extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {

            return FOApp.TestConnection();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            ULC = null;
            showProgress(false);

            if (success) {
                SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.prompt_url), mURLView.getText().toString());
                editor.putString(getString(R.string.prompt_login), mLoginView.getText().toString());
                editor.putBoolean(getString(R.string.UseUntrustCA), mUntrustCA.isChecked());
                if (mSaveCred.isChecked()) {
                    editor.putString(getString(R.string.prompt_password), mPasswordView.getText().toString());
                } else {
                    editor.putString(getString(R.string.prompt_password), "");
                }
                editor.commit();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            } else {
                mURLView.setError(FOApp.getError());
                mURLView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            ULC = null;
            showProgress(false);
        }
    }
}

