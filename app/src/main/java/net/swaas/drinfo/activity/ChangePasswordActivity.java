package net.swaas.drinfo.activity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import net.swaas.drinfo.R;
import net.swaas.drinfo.asynctasks.AddDoctorAsyncTask;
import net.swaas.drinfo.asynctasks.ChangePasswordAsyncTask;
import net.swaas.drinfo.asynctasks.SpecialityAsyncTask;
import net.swaas.drinfo.beans.CommonResponse;
import net.swaas.drinfo.beans.Doctor;
import net.swaas.drinfo.beans.Speciality;
import net.swaas.drinfo.beans.User;
import net.swaas.drinfo.logger.LogTracer;
import net.swaas.drinfo.utils.FileUtils;
import net.swaas.drinfo.utils.SettingsUtils;
import net.swaas.drinfo.utils.ValidatorUtils;
import net.swaas.drinfo.views.DefaultEditText;
import net.swaas.drinfo.views.DefaultTextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by vinoth on 10/22/15.
 */
public class ChangePasswordActivity extends BaseActivity {
    private static final LogTracer LOG_TRACER = LogTracer.instance(DoctorDetailsActivity.class);

    private Toolbar mToolbar;
    private NestedScrollView mScrollView;
    private TextView mErrorMsg;
    private ViewHolder mHolder;
    private ChangePasswordAsyncTask mSubmitTask;

    private final Handler mSubmitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.getData().containsKey(AddDoctorAsyncTask.SUCCESS_DATA)) {
                String message = ((CommonResponse) msg.getData().getSerializable(ChangePasswordAsyncTask.SUCCESS_DATA)).getMessage_To_Display();
                Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                mErrorMsg.setVisibility(View.GONE);
                finish();
            } else if (msg.getData().containsKey(ChangePasswordAsyncTask.ERROR_DATA)) {
                String errorMsg = msg.getData().getString(AddDoctorAsyncTask.ERROR_DATA);
                setError(errorMsg);
            }
            hideLoader();
        }
    };

    private class ViewHolder {
        public DefaultEditText oldPassword;
        public DefaultEditText newPassword;
        public DefaultEditText retypePassword;

        public ViewHolder() {
            oldPassword = (DefaultEditText) findViewById(R.id.old_password);
            newPassword = (DefaultEditText) findViewById(R.id.new_password);
            retypePassword = (DefaultEditText) findViewById(R.id.retype_password);
        }

        public void clearErrors() {
            clearErrorElement(oldPassword);
            clearErrorElement(newPassword);
            clearErrorElement(retypePassword);
        }

        public boolean validate() {
            //clearErrors();
            mErrorMsg.setVisibility(View.GONE);
            boolean hasError = false;
            boolean result = true;
            User user = SettingsUtils.getUserInfo(ChangePasswordActivity.this);
            result = ValidatorUtils.validateResultElement(oldPassword, true, ValidatorUtils.ValidationType.DEFAULT);
            if (!result) {
                setErrorElement(oldPassword);
                hasError = true;
            } else if (!oldPassword.getText().toString().equals(user.getPassword())) {
                setErrorElement(oldPassword, getString(R.string.error_old_password_no_match));
                hasError = true;
            } else clearErrorElement(oldPassword);
            result = ValidatorUtils.validateResultElement(newPassword, true, ValidatorUtils.ValidationType.DEFAULT);
            if (!result) {
                setErrorElement(newPassword);
                hasError = true;
            } else clearErrorElement(newPassword);
            result = ValidatorUtils.validateResultElement(retypePassword, true, ValidatorUtils.ValidationType.DEFAULT);
            if (!result) {
                setErrorElement(retypePassword);
                hasError = true;
            } else if (!retypePassword.getText().toString().equals(newPassword.getText().toString())) {
                setErrorElement(retypePassword, getString(R.string.error_password_no_match));
                hasError = true;
            } else clearErrorElement(retypePassword);
            return hasError;
        }

        public void clearErrorElement(DefaultEditText editText) {
            DefaultTextInputLayout parent = ((DefaultTextInputLayout) editText.getParent());
            parent.setError(null);
        }

        public void setErrorElement(DefaultEditText editText) {
            setErrorElement(editText, null);
        }
        public void setErrorElement(DefaultEditText editText, String message) {
            DefaultTextInputLayout parent = ((DefaultTextInputLayout) editText.getParent());
            String hint = parent.getHint().toString().toLowerCase();
            if (message != null) {
                parent.setError(message);
            } else if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                if (editText.getId() == R.id.retype_password) {
                    parent.setError(getString(R.string.please_retype_your_new_password));
                } else {
                    parent.setError(String.format(getString(R.string.please_enter_your), hint));
                }
            } else {
                parent.setError(hint + getString(R.string.is_invalid));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.action_change_password));
        mScrollView = (NestedScrollView) findViewById(R.id.form_Layout);
        mErrorMsg = (TextView) findViewById(R.id.common_error);
        mHolder = new ViewHolder();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_adddoctor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save: {
                validateAndSubmitData();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubmitTask != null) mSubmitTask.cancel(true);
    }

    public void validateAndSubmitData() {
        boolean hasError = mHolder.validate();
        if (!hasError) {
            if (mSubmitTask != null) mSubmitTask.cancel(true);
            showLoader(null, getString(R.string.msg_adding_doctor));
            mSubmitTask = new ChangePasswordAsyncTask(this, mSubmitHandler);
            mSubmitTask.execute(mHolder.newPassword.getText().toString());
        }
    }

    public void setError(String errorMsg) {
        mErrorMsg.setText(errorMsg);
        mErrorMsg.setVisibility(View.VISIBLE);
        mScrollView.smoothScrollTo(0, 0);
    }
}
