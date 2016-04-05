package ru.vat78.fotimetracker;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityTestCase;
import android.test.ActivityUnitTestCase;
import android.test.TouchUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class FO_LoginTest extends ActivityInstrumentationTestCase2<FOTT_LoginActivity> {


    public FO_LoginTest()  {
        super(FOTT_LoginActivity.class);
    }

    @Override
    public void setUp()throws Exception {



    }

    public void testLogin() throws Exception {

        FOTT_LoginActivity la = getActivity();
        assertNotNull(la);

        AutoCompleteTextView mURLView = (AutoCompleteTextView) la.findViewById(R.id.fo_url);
        AutoCompleteTextView mLoginView = (AutoCompleteTextView) la.findViewById(R.id.login);
        EditText mPasswordView = (EditText) la.findViewById(R.id.password);
        CheckBox mUntrustCA = (CheckBox) la.findViewById(R.id.untrustCA);

        mURLView.setText(SecretCredentials.getUrl());
        mLoginView.setText(SecretCredentials.getUser());
        mPasswordView.setText(SecretCredentials.getPwd());
        mUntrustCA.setChecked(true);

        Button mSignInButton = (Button) la.findViewById(R.id.sign_in_button);

        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, mSignInButton);
    }

    public void testNewLogin() throws Exception {

        FOTT_LoginActivity la = getActivity();
        //assertNotNull(la);

        AutoCompleteTextView mURLView = (AutoCompleteTextView) la.findViewById(R.id.fo_url);
        AutoCompleteTextView mLoginView = (AutoCompleteTextView) la.findViewById(R.id.login);
        EditText mPasswordView = (EditText) la.findViewById(R.id.password);
        CheckBox mUntrustCA = (CheckBox) la.findViewById(R.id.untrustCA);

        mURLView.setText("ya.ru");
        mLoginView.setText("user");
        mPasswordView.setText("password");
        mUntrustCA.setChecked(true);

        getInstrumentation().waitForMonitor(new Instrumentation.ActivityMonitor(la.getLocalClassName(),null,false));
    }
}
