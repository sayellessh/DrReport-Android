package net.swaas.drinfo.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.swaas.drinfo.R;
import net.swaas.drinfo.asynctasks.UserAuthenticationAsyncTask;
import net.swaas.drinfo.beans.User;
import net.swaas.drinfo.logger.LogTracer;
import net.swaas.drinfo.utils.SettingsUtils;

public class MainActivity extends BaseActivity implements TextWatcher {

    private static final LogTracer LOG_TRACER = LogTracer.instance(MainActivity.class);
    private UserAuthenticationAsyncTask mUserAuthAsyncTask = null;
    private Button mSubmit;
    private EditText mUserName;
    private EditText mPassword;
    private TextView mError;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.getData().containsKey(UserAuthenticationAsyncTask.KEY_ERROR)) {
                mError.setText(msg.getData().getString(UserAuthenticationAsyncTask.KEY_ERROR));
                mError.setVisibility(View.VISIBLE);
            } else if (msg.getData().containsKey(UserAuthenticationAsyncTask.KEY_RESPOSE)) {
                openDoctorDetailsActivity();
            }
            hideLoader();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        User user = SettingsUtils.getUserInfo(this);
        if (user != null && user.getUser_Id() > 0) {
            openDoctorDetailsActivity();
        }
        mUserName = (EditText) findViewById(R.id.userid);
        mPassword = (EditText) findViewById(R.id.password);
        mSubmit = (Button) findViewById(R.id.btn_submit);
        mError = (TextView) findViewById(R.id.common_error);
        mUserName.addTextChangedListener(this);
        mPassword.addTextChangedListener(this);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndSubmit();
            }
        });
    }

    public void validateAndSubmit() {
        mError.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mUserName.getText().toString().trim())) {
            mError.setText(getString(R.string.error_invalid_username));
            mError.setVisibility(View.VISIBLE);
            return;
        }
        if (TextUtils.isEmpty(mPassword.getText().toString().trim())) {
            mError.setText(getString(R.string.error_invalid_password));
            mError.setVisibility(View.VISIBLE);
            return;
        }
        checkUserAuthentication(mUserName.getText().toString().trim(), mPassword.getText().toString());
    }
    public void checkUserAuthentication(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        checkUserAuthentication(user);
    }

    public void checkUserAuthentication(User user) {
        if (mUserAuthAsyncTask != null) mUserAuthAsyncTask.cancel(true);
        mUserAuthAsyncTask = new UserAuthenticationAsyncTask(this, mHandler);
        showLoader(null, getString(R.string.logging_in));
        mUserAuthAsyncTask.execute(user);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mError.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(mUserName.getText().toString())
                && !TextUtils.isEmpty(mPassword.getText().toString())) {
            mSubmit.setEnabled(true);
        } else {
            mSubmit.setEnabled(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    private void openDoctorDetailsActivity() {
        Intent intent = new Intent(MainActivity.this, DoctorDetailsActivity.class);
        startActivity(intent);
    }
}
