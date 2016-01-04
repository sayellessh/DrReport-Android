package net.swaas.drinfo.activity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import net.swaas.drinfo.R;
import net.swaas.drinfo.asynctasks.AddDoctorAsyncTask;
import net.swaas.drinfo.asynctasks.SpecialityAsyncTask;
import net.swaas.drinfo.beans.Doctor;
import net.swaas.drinfo.beans.Speciality;
import net.swaas.drinfo.beans.User;
import net.swaas.drinfo.core.FileDownloader;
import net.swaas.drinfo.dao.DoctorDAO;
import net.swaas.drinfo.logger.LogTracer;
import net.swaas.drinfo.utils.FileUtils;
import net.swaas.drinfo.utils.SettingsUtils;
import net.swaas.drinfo.utils.Strings;
import net.swaas.drinfo.utils.ValidatorUtils;
import net.swaas.drinfo.views.DefaultAutocompleteTextView;
import net.swaas.drinfo.views.DefaultEditText;
import net.swaas.drinfo.views.DefaultTextInputLayout;
import net.swaas.drinfo.views.DefaultTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by vinoth on 10/22/15.
 */
public class AddDoctorActivity extends BaseActivity implements LocationListener {
    private static final LogTracer LOG_TRACER = LogTracer.instance(DoctorDetailsActivity.class);
    public static final String DATE_FORMAT = "hh:mm a";
    public static final String EXISTING_DOCTOR = "existing_doctor";
    public static final String DEFAULT_IMAGE_EXTENSION = ".jpg";
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_BROWSE = 2;
    private Toolbar mToolbar;
    private NestedScrollView mScrollView;
    private TextView mErrorMsg;
    private ViewHolder mHolder;
    private String mCurrentPhotoPath;
    private String mCloudPhotoPath;
    private LocationManager mLocationManager;
    private AsyncTask mLocationTask;
    private Location mLocation;
    private AddDoctorAsyncTask mSubmitTask;
    private SpecialityAsyncTask mSpecialityTask;
    private Speciality mSpeciality;
    private List<Speciality> mSpecialities;
    private Doctor mExistingDoctor;
    private ContextMenu.ContextMenuInfo mMenuInfo;
    private FileDownloader mFileDownloader;
    AlertDialog mAlertDialog = null;
    private FileDownloader.Task mDownloadTask = new FileDownloader.Task() {
        @Override
        public void onProgress(double downloaded, int fileLength, double progress) {
            String msg = String.format(getString(R.string.msg_downloading), Double.toString(Math.round(progress)));
            showLoader(null, msg);
        }

        @Override
        public void onComplete(String filePath) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + filePath), "image/*");
            startActivity(intent);
            hideLoader();
        }

        @Override
        public void onError(Exception e) {
            LOG_TRACER.e(e);
            hideLoader();
            AlertDialog.Builder builder = new AlertDialog.Builder(AddDoctorActivity.this);
            builder.setTitle(getString(R.string.error));
            builder.setMessage(e.getMessage());
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (mAlertDialog != null) mAlertDialog.dismiss();
                }
            });
            if (mAlertDialog != null) {
                mAlertDialog.dismiss();
            }
            mAlertDialog = builder.create();
            mAlertDialog.setCancelable(true);
            mAlertDialog.show();
        }
    };

    private static final String ADDRESS = "address";
    private static final String LANDMARK = "landmark";

    private final Handler mSubmitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.getData().containsKey(AddDoctorAsyncTask.SUCCESS_DATA)) {
                if (mExistingDoctor != null) {
                    Toast.makeText(AddDoctorActivity.this, getString(R.string.msg_doctor_updated_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddDoctorActivity.this, getString(R.string.msg_doctor_added_success), Toast.LENGTH_SHORT).show();
                }
                mErrorMsg.setVisibility(View.GONE);
                finish();
            } else if (msg.getData().containsKey(AddDoctorAsyncTask.ERROR_DATA)) {
                String errorMsg = msg.getData().getString(AddDoctorAsyncTask.ERROR_DATA);
                setError(errorMsg);
            }
            hideLoader();
        }
    };

    private class ViewHolder {
        public DefaultEditText firstName;
        public DefaultEditText lastName;
        public DefaultEditText speciality;
        public DefaultAutocompleteTextView hospitalName;
        public DefaultEditText phoneNumber;
        public DefaultEditText email;
        public DefaultEditText landmark;
        public DefaultEditText assistantName;
        public DefaultEditText assistantPhone;
        public DefaultEditText remarks;
        //public ImageButton geolocation;
        public ImageButton thumbnail;
        public ImageView thumbnailPreview;

        public ImageButton workingHoursAddBtn;
        public WorkingHoursHolder workingHoursHolder1;
        public WorkingHoursHolder workingHoursHolder2;
        public WorkingHoursHolder workingHoursHolder3;

        public View specialityWrapper;

        public DefaultEditText trainerCode;

        public View locationPickLayout;
        public View locationUpdateLayout;
        public DefaultEditText address;
        public Button locationPick;
        public Button locationUpdate;
        public DefaultTextView errorElement;

        public ViewHolder() {
            firstName = (DefaultEditText) findViewById(R.id.first_name);
            lastName = (DefaultEditText) findViewById(R.id.last_name);
            speciality = (DefaultEditText) findViewById(R.id.speciality);
            hospitalName = (DefaultAutocompleteTextView) findViewById(R.id.hospital_name);
            List<Doctor> doctors = new DoctorDAO(AddDoctorActivity.this).getAll();
            if (doctors != null && doctors.size() > 0) {
                Set<String> hospitals = new TreeSet<>();
                Iterator<Doctor> itr = doctors.iterator();
                Doctor doctor = null;
                while (itr.hasNext()) {
                    doctor = itr.next();
                    if (!TextUtils.isEmpty(doctor.getHospital_Name())) {
                        hospitals.add(doctor.getHospital_Name());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddDoctorActivity.this, android.R.layout.simple_list_item_1, hospitals.toArray(new String[8]));
                hospitalName.setAdapter(adapter);
                hospitalName.setThreshold(1);
            }

            locationPickLayout = findViewById(R.id.location_pick_layout);
            locationUpdateLayout = findViewById(R.id.location_update_layout);
            locationPick = (Button) findViewById(R.id.location_pick_btn);
            locationUpdate = (Button) findViewById(R.id.location_update_btn);
            errorElement = (DefaultTextView) findViewById(R.id.error_element);
            address = (DefaultEditText) findViewById(R.id.address);
            address.setEnabled(true);
            locationPickLayout.setVisibility(View.VISIBLE);
            locationUpdateLayout.setVisibility(View.GONE);
            errorElement.setVisibility(View.GONE);
            //geolocation = (ImageButton) findViewById(R.id.geo_location);

            phoneNumber = (DefaultEditText) findViewById(R.id.phone_number);
            email = (DefaultEditText) findViewById(R.id.email);
            landmark = (DefaultEditText) findViewById(R.id.landmark);
            assistantName = (DefaultEditText) findViewById(R.id.assistant_name);
            assistantPhone = (DefaultEditText) findViewById(R.id.assistant_phone);
            remarks = (DefaultEditText) findViewById(R.id.remarks);
            thumbnail = (ImageButton) findViewById(R.id.thumbnail);
            thumbnailPreview = (ImageView) findViewById(R.id.thumbnail_preview);
            specialityWrapper = findViewById(R.id.speciality_wrapper);
            workingHoursAddBtn = (ImageButton) findViewById(R.id.working_hours_add);
            trainerCode = (DefaultEditText) findViewById(R.id.trainer_code);
            workingHoursHolder1 = new WorkingHoursHolder(findViewById(R.id.working_hours_input_1), true);
            workingHoursHolder2 = new WorkingHoursHolder(findViewById(R.id.working_hours_input_2), false);
            workingHoursHolder2.setVisibility(View.GONE);
            workingHoursHolder3 = new WorkingHoursHolder(findViewById(R.id.working_hours_input_3), false);
            workingHoursHolder3.setVisibility(View.GONE);
            if (SettingsUtils.getIsTrainer(AddDoctorActivity.this)) {
                trainerCode.setVisibility(View.GONE);
            } else {
                trainerCode.setVisibility(View.VISIBLE);
            }
            registerForContextMenu(specialityWrapper);
            specialityWrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectSpeciality(view);
                }
            });
            workingHoursAddBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!workingHoursHolder1.isVisible()) {
                        workingHoursHolder1.setVisibility(View.VISIBLE);
                    } else if (!workingHoursHolder2.isVisible()) {
                        workingHoursHolder2.setVisibility(View.VISIBLE);
                    } else if (!workingHoursHolder3.isVisible()) {
                        workingHoursHolder3.setVisibility(View.VISIBLE);
                    }
                    if (workingHoursHolder1.isVisible()
                            && workingHoursHolder2.isVisible()
                            && workingHoursHolder3.isVisible()) {
                        workingHoursAddBtn.setVisibility(View.GONE);
                    } else {
                        workingHoursAddBtn.setVisibility(View.VISIBLE);
                    }
                    //mScrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }

        public void updateValues(Doctor doctor) {
            firstName.setText(doctor.getFirst_Name());
            lastName.setText(doctor.getLast_Name());
            speciality.setText(doctor.getSpeciality_Name());
            mSpeciality = new Speciality();
            mSpeciality.setSpeciality_Code(doctor.getSpeciality_Code());
            mSpeciality.setSpeciality_Name(doctor.getSpeciality_Name());
            hospitalName.setText(doctor.getHospital_Name());
            phoneNumber.setText(doctor.getPhone_Number());
            email.setText(doctor.getEmail_Id());
            if (!TextUtils.isEmpty(doctor.getLocation_Full_Address()) && !TextUtils.isEmpty(doctor.getLocation_Full_Address().trim())) {
                address.setText(doctor.getLocation_Full_Address());
                locationPickLayout.setVisibility(View.GONE);
                locationUpdateLayout.setVisibility(View.VISIBLE);
                address.setEnabled(true);
            } else {
                locationPickLayout.setVisibility(View.GONE);
                //locationPickLayout.setVisibility(View.VISIBLE);
                locationUpdateLayout.setVisibility(View.VISIBLE);
            }
            landmark.setText(doctor.getLandmark());
            assistantName.setText(doctor.getAssistant_Name());
            assistantPhone.setText(doctor.getAssistant_Phone_Number());
            remarks.setText(doctor.getRemarks());
            if (!TextUtils.isEmpty(doctor.getWorking_From_Time())) {
                workingHoursHolder1.workFrom.setText(doctor.getWorking_From_Time());
                workingHoursHolder1.workTo.setText(doctor.getWorking_To_Time());
            }
            if (!TextUtils.isEmpty(doctor.getWorking_From_Time_2())) {
                workingHoursHolder2.workFrom.setText(doctor.getWorking_From_Time_2());
                workingHoursHolder2.workTo.setText(doctor.getWorking_To_Time_2());
                workingHoursHolder2.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(doctor.getWorking_From_Time_3())) {
                workingHoursHolder3.workFrom.setText(doctor.getWorking_From_Time_3());
                workingHoursHolder3.workTo.setText(doctor.getWorking_To_Time_3());
                workingHoursHolder3.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(doctor.getHospital_Photo_Url())) {
                thumbnailPreview.setVisibility(View.VISIBLE);
                mCloudPhotoPath = doctor.getHospital_Photo_Url();
                Ion.with(AddDoctorActivity.this).load(doctor.getHospital_Photo_Url()).intoImageView(thumbnailPreview);
            } else {
                thumbnailPreview.setVisibility(View.GONE);
            }
            if (doctor.getTrainer_Code() != null && doctor.getTrainer_Code().compareTo(BigInteger.ZERO) > 0)
                trainerCode.setText(doctor.getTrainer_Code().toString());
        }

        public Doctor getBean() {
            Doctor doctor = mExistingDoctor;
            if (mExistingDoctor == null) {
                doctor = new Doctor();
            }
            doctor.setFirst_Name(firstName.getText().toString().trim());
            doctor.setLast_Name(lastName.getText().toString().trim());
            doctor.setSpeciality_Name(speciality.getText().toString().trim());
            doctor.setSpeciality_Code(mSpeciality.getSpeciality_Code());
            doctor.setHospital_Name(hospitalName.getText().toString().trim());
            //doctor.setHospital_Photo_Url(lastName.getText().toString().trim());
            if (mLocation != null) {
                doctor.setLatitude(Double.toString(mLocation.getLatitude()));
                doctor.setLongitude(Double.toString(mLocation.getLongitude()));
            }
            doctor.setPhone_Number(phoneNumber.getText().toString().trim());
            doctor.setEmail_Id(email.getText().toString().trim());
            doctor.setLocation_Full_Address(address.getText().toString().trim());
            doctor.setLandmark(landmark.getText().toString().trim());
            doctor.setAssistant_Name(assistantName.getText().toString().trim());
            doctor.setAssistant_Phone_Number(assistantPhone.getText().toString().trim());
            doctor.setRemarks(remarks.getText().toString().trim());
            List<String> fromHrs = new ArrayList<>(3);
            List<String> toHrs = new ArrayList<>(3);
            if (workingHoursHolder1.isVisible() && workingHoursHolder1.isFilled()) {
                fromHrs.add(workingHoursHolder1.workFrom.getText().toString().trim());
                toHrs.add(workingHoursHolder1.workTo.getText().toString().trim());
            }
            if (workingHoursHolder2.isVisible() && workingHoursHolder2.isFilled()) {
                fromHrs.add(workingHoursHolder2.workFrom.getText().toString().trim());
                toHrs.add(workingHoursHolder2.workTo.getText().toString().trim());
            }
            if (workingHoursHolder3.isVisible() && workingHoursHolder3.isFilled()) {
                fromHrs.add(workingHoursHolder3.workFrom.getText().toString().trim());
                toHrs.add(workingHoursHolder3.workTo.getText().toString().trim());
            }
            doctor.setWorking_From_Time(null);
            doctor.setWorking_To_Time(null);
            doctor.setWorking_From_Time_2(null);
            doctor.setWorking_To_Time_2(null);
            doctor.setWorking_From_Time_3(null);
            doctor.setWorking_To_Time_3(null);
            int len = fromHrs.size() - 1;
            int cnt = 1;
            for (int i = 0; i <= len; i++) {
                if (fromHrs.get(i) != null) {
                    switch (cnt) {
                        case 1: {
                            doctor.setWorking_From_Time(fromHrs.get(i));
                            doctor.setWorking_To_Time(toHrs.get(i));
                            cnt++;
                            break;
                        }
                        case 2: {
                            doctor.setWorking_From_Time_2(fromHrs.get(i));
                            doctor.setWorking_To_Time_2(toHrs.get(i));
                            cnt++;
                            break;
                        }
                        case 3: {
                            doctor.setWorking_From_Time_3(fromHrs.get(i));
                            doctor.setWorking_To_Time_3(toHrs.get(i));
                            cnt++;
                            break;
                        }
                    }
                }
            }
            if (!TextUtils.isEmpty(mCurrentPhotoPath))
                doctor.setHospitalLocatPath(mCurrentPhotoPath);
            if (!TextUtils.isEmpty(trainerCode.getText().toString().trim()))
                doctor.setTrainer_Code(new BigInteger(trainerCode.getText().toString().trim()));
            return doctor;
        }

        public void clearFocus() {
            firstName.clearFocus();
            lastName.clearFocus();
            hospitalName.clearFocus();
            address.clearFocus();
            phoneNumber.clearFocus();
            email.clearFocus();
            landmark.clearFocus();
            assistantName.clearFocus();
            assistantPhone.clearFocus();
            remarks.clearFocus();
        }

        /*public void clearErrors() {
            clearErrorElement(firstName);
            clearErrorElement(lastName);
            clearErrorElement(speciality);
            clearErrorElement(hospitalName);
            clearErrorElement(address);
            clearErrorElement(phoneNumber);
            clearErrorElement(email);
            clearErrorElement(landmark);
            clearErrorElement(assistantName);
            clearErrorElement(assistantPhone);
            clearErrorElement(remarks);
            clearErrorElement(workFrom);
            clearErrorElement(workTo);
        }*/

        public boolean validate() {
            //clearErrors();
            mErrorMsg.setVisibility(View.GONE);
            boolean hasError = false;
            boolean result = true;
            result = ValidatorUtils.validateResultElement(firstName, true, ValidatorUtils.ValidationType.NAME);
            if (!result) {
                setErrorElement(firstName);
                hasError = true;
            } else clearErrorElement(firstName);
            result = ValidatorUtils.validateResultElement(lastName, false, ValidatorUtils.ValidationType.NAME);
            if (!result) {
                setErrorElement(lastName);
                hasError = true;
            } else clearErrorElement(lastName);
            result = ValidatorUtils.validateResultElement(speciality, true, ValidatorUtils.ValidationType.DEFAULT);
            if (!result) {
                setErrorElement(speciality);
                hasError = true;
            } else clearErrorElement(speciality);
            result = ValidatorUtils.validateResultElement(hospitalName, false, ValidatorUtils.ValidationType.DEFAULT);
            if (!result) {
                setErrorElement(hospitalName);
                hasError = true;
            } else clearErrorElement(hospitalName);
            result = ValidatorUtils.validateResultElement(address, true, ValidatorUtils.ValidationType.DEFAULT);
            if (!result) {
                errorElement.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(address.getText().toString().trim())) {
                    errorElement.setText(getString(R.string.error_address_required));
                } else {
                    errorElement.setText(getString(R.string.error_address_invalid));
                }
                hasError = true;
            } else errorElement.setVisibility(View.GONE);;
            result = ValidatorUtils.validateResultElement(phoneNumber, false, ValidatorUtils.ValidationType.PHONE);
            if (!result) {
                setErrorElement(phoneNumber);
                hasError = true;
            } else clearErrorElement(phoneNumber);
            boolean emailMandatory = SettingsUtils.getIsTrainer(AddDoctorActivity.this) ? false : true;
            result = ValidatorUtils.validateResultElement(email, emailMandatory, ValidatorUtils.ValidationType.EMAIL);
            if (!result) {
                setErrorElement(email);
                hasError = true;
            } else if (mExistingDoctor == null
                    || !mExistingDoctor.getEmail_Id().equals(email.getText().toString().trim())) {
                if (!TextUtils.isEmpty(email.getText().toString().trim())) {
                    boolean duplicate = false;
                    List<Doctor> doctors = new DoctorDAO(AddDoctorActivity.this).getAll();
                    if (doctors != null && doctors.size() > 0) {
                        Iterator<Doctor> itr = doctors.iterator();
                        Doctor doctor = null;
                        while (itr.hasNext()) {
                            doctor = itr.next();
                            if (!TextUtils.isEmpty(doctor.getEmail_Id())
                                    && doctor.getEmail_Id().trim().equals(email.getText().toString().trim())) {
                                duplicate = true;
                                break;
                            }
                        }
                    }
                    if (duplicate) {
                        hasError = true;
                        setErrorElement(email, getString(R.string.error_email_already_exists));
                    } else {
                        clearErrorElement(email);
                    }
                } else {
                    clearErrorElement(email);
                }
            }
            result = ValidatorUtils.validateResultElement(landmark, false, ValidatorUtils.ValidationType.DEFAULT);
            if (!result) {
                setErrorElement(landmark);
                hasError = true;
            } else clearErrorElement(landmark);
            result = ValidatorUtils.validateResultElement(assistantName, false, ValidatorUtils.ValidationType.NAME);
            if (!result) {
                setErrorElement(assistantName);
                hasError = true;
            } else clearErrorElement(assistantName);
            result = ValidatorUtils.validateResultElement(assistantPhone, false, ValidatorUtils.ValidationType.PHONE);
            if (!result) {
                setErrorElement(assistantPhone);
                hasError = true;
            } else clearErrorElement(assistantPhone);
            result = ValidatorUtils.validateResultElement(remarks, false, ValidatorUtils.ValidationType.DEFAULT);
            if (!result) {
                setErrorElement(remarks);
                hasError = true;
            } else clearErrorElement(remarks);
            boolean hasError1 = workingHoursHolder1.validate();
            if (!hasError) hasError = hasError1;
            boolean hasError2 = workingHoursHolder2.validate();
            if (!hasError) hasError = hasError2;
            boolean hasError3 = workingHoursHolder3.validate();
            if (!hasError) hasError = hasError3;
            if (!hasError1 && !hasError2 && !hasError3) {
                if (!workingHoursHolder1.isFilled() && !workingHoursHolder2.isFilled() && !workingHoursHolder3.isFilled()) {
                    workingHoursHolder1.validate(true);
                    hasError = true;
                }
            }

            if (hasError) {
                setError(getString(R.string.error_validation));
            }
            return hasError;
        }

        public void clearErrorElement(DefaultEditText editText) {
            /*DefaultTextInputLayout parent = ((DefaultTextInputLayout) editText.getParent());
            parent.setErrorEnabled(false);
            parent.setError(null);*/
            clearErrorElement((TextView) editText);
        }

        public void clearErrorElement(TextView editText) {
            DefaultTextInputLayout parent = ((DefaultTextInputLayout) editText.getParent());
            parent.setErrorEnabled(false);
            parent.setError(null);
        }

        public void setErrorElement(DefaultEditText editText) {
            /*DefaultTextInputLayout parent = ((DefaultTextInputLayout) editText.getParent());
            String hint = parent.getHint().toString();
            parent.setErrorEnabled(true);
            if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                parent.setError(hint + getString(R.string.is_required));
            } else {
                parent.setError(hint + getString(R.string.is_invalid));
            }*/
            setErrorElement((TextView) editText);
        }

        public void setErrorElement(TextView editText) {
            DefaultTextInputLayout parent = ((DefaultTextInputLayout) editText.getParent());
            String hint = parent.getHint().toString();
            parent.setErrorEnabled(true);
            if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                parent.setError(hint + getString(R.string.is_required));
            } else {
                parent.setError(hint + getString(R.string.is_invalid));
            }
        }

        public void setErrorElement(DefaultEditText editText, String message) {
            DefaultTextInputLayout parent = ((DefaultTextInputLayout) editText.getParent());
            parent.setErrorEnabled(true);
            if (TextUtils.isEmpty(message)) {
                setErrorElement(editText);
            } else {
                parent.setError(message);
            }
        }

        public class WorkingHoursHolder {
            View mainView;
            public DefaultEditText workFrom;
            public DefaultEditText workTo;
            public ImageButton removeBtn;
            public View workFromWrapper;
            public View workToWrapper;

            public WorkingHoursHolder(View view, boolean hideRemoveBtn) {
                mainView = view;
                workFrom = (DefaultEditText) view.findViewById(R.id.work_from);
                workTo = (DefaultEditText) view.findViewById(R.id.work_to);
                workFromWrapper = view.findViewById(R.id.work_from_wrapper);
                workToWrapper = view.findViewById(R.id.work_to_wrapper);
                removeBtn = (ImageButton) view.findViewById(R.id.working_hours_remove);
                if (hideRemoveBtn) {
                    removeBtn.setVisibility(View.GONE);
                } else {
                    removeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mainView.setVisibility(View.GONE);
                            workingHoursAddBtn.setVisibility(View.VISIBLE);
                            workFrom.setText("");
                            workTo.setText("");
                        }
                    });
                }
                workFromWrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LOG_TRACER.d("CLICK");
                        try {
                            ViewHolder.this.clearFocus();
                            displayTimerDialog(workFrom);
                        } catch (ParseException e) {
                            LOG_TRACER.e(e);
                        }
                    }
                });
                workToWrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LOG_TRACER.d("CLICK");
                        try {
                            ViewHolder.this.clearFocus();
                            displayTimerDialog(workTo);
                        } catch (ParseException e) {
                            LOG_TRACER.e(e);
                        }
                    }
                });
            }

            public boolean isVisible() {
                int visibility = mainView.getVisibility();
                if (visibility == View.VISIBLE) {
                    return true;
                } else
                    return false;
            }

            public boolean isFilled() {
                if (TextUtils.isEmpty(workFrom.getText().toString())
                        && TextUtils.isEmpty(workTo.getText().toString())) {
                    return false;
                }
                return true;
            }

            public void setVisibility(int visibility) {
                mainView.setVisibility(visibility);
            }

            public boolean validate() {
                return validate(false);
            }

            public boolean validate(boolean forceValidate) {
                boolean hasError = false;
                if (isVisible() || forceValidate) {
                    if (!TextUtils.isEmpty(workFrom.getText().toString())
                            || !TextUtils.isEmpty(workTo.getText().toString()) || forceValidate) {
                        boolean result = ValidatorUtils.validateResultElement(workFrom, true, ValidatorUtils.ValidationType.DEFAULT);
                        if (!result) {
                            setErrorElement(workFrom);
                            hasError = true;
                        } else clearErrorElement(workFrom);
                        result = ValidatorUtils.validateResultElement(workTo, true, ValidatorUtils.ValidationType.DEFAULT);
                        if (!result) {
                            setErrorElement(workTo);
                            hasError = true;
                        } else if (!TextUtils.isEmpty(workFrom.getText().toString())
                                && !TextUtils.isEmpty(workTo.getText().toString())) {
                            try {
                                Date fromTime = getDateFromTime(workFrom.getText().toString());
                                Date toTime = getDateFromTime(workTo.getText().toString());
                                if (workFrom.getText().toString().equals(workTo.getText().toString())) {
                                    setErrorElement(workTo, getString(R.string.error_workto_should_not_be_equal));
                                    hasError = true;
                                } else if (toTime.equals(fromTime) || !toTime.after(fromTime)) {
                                    setErrorElement(workTo, getString(R.string.error_workto_should_be_greater));
                                    hasError = true;
                                } else {
                                    clearErrorElement(workTo);
                                }
                            } catch (ParseException e) {
                                LOG_TRACER.e(e);
                            }
                        } else clearErrorElement(workTo);
                    }
                }
                return hasError;
            }

            public Date getDateFromTime(String string) throws ParseException {
                DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
                Date date = format.parse(string);
                return date;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddoctor);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mScrollView = (NestedScrollView) findViewById(R.id.form_Layout);
        mErrorMsg = (TextView) findViewById(R.id.common_error);
        mHolder = new ViewHolder();
        setThumbnailActions();
        setGeolocationActions();
        hideInput();
        mExistingDoctor = (Doctor) getIntent().getSerializableExtra(EXISTING_DOCTOR);
        if (mExistingDoctor != null) {
            mHolder.updateValues(mExistingDoctor);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        hideInput();
        mHolder.firstName.clearFocus();
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
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        int id = view.getId();
        switch (id) {
            case R.id.speciality_wrapper: {
                Speciality spl = null;
                int len = mSpecialities.size() - 1;
                for (int i = 0; i <= len; i++) {
                    spl = mSpecialities.get(i);
                    menu.add(spl.getSpeciality_Name());
                }
                break;
            }
            default: {
                //menu.setHeaderTitle("Title");
                //menu.add(0, id, 0, "Item 1");
                menu.add(0, R.id.take_photo, 0, getString(R.string.take_photo));
                menu.add(1, R.id.choose_from_gallery, 0, getString(R.string.choose_from_gallery));
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.take_photo: {
                takePhoto();
                break;
            }
            case R.id.choose_from_gallery: {
                browsePhoto();
                break;
            }
            case 0: {
                mSpeciality = getMatchingSpeciality(item);
                mHolder.speciality.setText(mSpeciality.getSpeciality_Name());
            }
        }
        return super.onContextItemSelected(item);
    }

    private void setThumbnailActions() {
        registerForContextMenu(mHolder.thumbnail);
        mHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContextMenu(view);
            }
        });
        mHolder.thumbnailPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LOG_TRACER.d("Clicked");
                if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
                    mDownloadTask.onComplete(mCurrentPhotoPath);
                } else if (!TextUtils.isEmpty(mCloudPhotoPath)) {
                    mFileDownloader = new FileDownloader(AddDoctorActivity.this, mDownloadTask);
                    mFileDownloader.execute(mCloudPhotoPath);
                }
            }
        });
    }

    public void setGeolocationActions() {
        mHolder.locationPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initGeolocation();
            }
        });
        mHolder.locationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initGeolocation();
            }
        });
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String appDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getString(R.string.app_name);
        File storageDir = new File(appDir);
        if (!storageDir.exists()) {
            boolean out = storageDir.mkdirs();
            if (!out) {
                throw new IOException("Unable to make directory, error occurred.");
            }
        }
        File image = File.createTempFile(
                getGeneratedFileName(this),  /* prefix */
                DEFAULT_IMAGE_EXTENSION,         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static String getGeneratedFileName(Context context) {
        User user = SettingsUtils.getUserInfo(context);
        return user.getUser_Id() + "_" + new Date().getTime();
    }

    public void takePhoto() {
        try {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            File photo = createImageFile();
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photo));
            mCurrentPhotoPath = photo.getPath();
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } catch (IOException e) {
            LOG_TRACER.e(e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void browsePhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        startActivityForResult(intent, REQUEST_IMAGE_BROWSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE: {
                    onTakingPhoto(data);
                    break;
                }
                case REQUEST_IMAGE_BROWSE: {
                    onBrowsingPhoto(data);
                }
            }
        }
    }

    public void onTakingPhoto(Intent data) {
        setImageThumbnail(mCurrentPhotoPath);
    }

    public void onBrowsingPhoto(Intent data) {
        String path = FileUtils.getPath(this, data.getData());
        setImageThumbnail(path);
    }

    public void setImageThumbnail(String imagePath) {
        mCurrentPhotoPath = imagePath;
        /*BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, opts);*/
        Ion.with(this).load(imagePath).intoImageView(mHolder.thumbnailPreview);
        //mHolder.thumbnailPreview.setImageBitmap(bitmap);
        mHolder.thumbnailPreview.setVisibility(View.VISIBLE);
    }

    public void getAddressDetails() {
        mLocationTask = new AsyncTask<Object, Void, Object>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (o != null && o instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) o;
                        mHolder.address.setText(jsonObject.getString(ADDRESS));
                        //mHolder.landmark.setText(jsonObject.getString(LANDMARK));
                        if (!TextUtils.isEmpty(jsonObject.getString(ADDRESS)))
                            mHolder.address.setEnabled(true);
                        else mHolder.address.setEnabled(true);
                        mHolder.locationPickLayout.setVisibility(View.GONE);
                        mHolder.locationUpdateLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(AddDoctorActivity.this, getString(R.string.msg_got_location_details), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(AddDoctorActivity.this, getString(R.string.error_getting_location_details), Toast.LENGTH_SHORT).show();
                        mHolder.locationPickLayout.setVisibility(View.GONE);
                        mHolder.locationUpdateLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(AddDoctorActivity.this, getString(R.string.error_getting_location_details), Toast.LENGTH_SHORT).show();
                    mHolder.locationPickLayout.setVisibility(View.GONE);
                    mHolder.locationUpdateLayout.setVisibility(View.VISIBLE);
                }
                if (mLocationManager != null)
                    mLocationManager.removeUpdates(AddDoctorActivity.this);
            }

            @Override
            protected Object doInBackground(Object... objects) {
                try {
                    if (mLocation != null) {
                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(AddDoctorActivity.this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        //addresses = geocoder.getFromLocation(12.022986, 86.624403, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        if(addresses.size() > 0) {
                            String address = TextUtils.isEmpty(addresses.get(0).getAddressLine(0)) ? "" : addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = TextUtils.isEmpty(addresses.get(0).getLocality()) ? "" : addresses.get(0).getLocality();
                            String state = TextUtils.isEmpty(addresses.get(0).getAdminArea()) ? "" : addresses.get(0).getAdminArea();
                            String country = TextUtils.isEmpty(addresses.get(0).getCountryName()) ? "" : addresses.get(0).getCountryName();
                            String postalCode = TextUtils.isEmpty(addresses.get(0).getPostalCode()) ? ""
                                    : (" - " + addresses.get(0).getPostalCode());
                            String knownName = addresses.get(0).getFeatureName();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(ADDRESS, address + "\n" + city + "\n" + state + "\n" + country + postalCode);
                            jsonObject.put(LANDMARK, knownName);
                            if (mLocationManager != null)
                                mLocationManager.removeUpdates(AddDoctorActivity.this);
                            return jsonObject;
                        } else {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(ADDRESS, "");
                            jsonObject.put(LANDMARK, "");
                        }
                    } else throw new IOException("Location not initialized.");
                } catch (IOException e) {
                    LOG_TRACER.e(e);
                } catch (JSONException e) {
                    LOG_TRACER.e(e);
                }
                return null;
            }
        };
        mLocationTask.execute();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.mLocation = location;
        //getAddressDetails();
        /*
        *To enable address after getting the geolocation
        */
        enableAddress();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        initGeolocation();
    }

    @Override
    public void onProviderDisabled(String s) {
        if (mLocationManager != null) mLocationManager.removeUpdates(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubmitTask != null) mSubmitTask.cancel(true);
        if (mSpecialityTask != null) mSpecialityTask.cancel(true);
    }

    public void initGeolocation() {
        //Toast.makeText(this, getString(R.string.msg_getting_location_details), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, getString(R.string.getting_lat_long), Toast.LENGTH_SHORT).show();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //mLocation = null;
        //exceptions will be thrown if provider is not permitted.
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        try {
            gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            networkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (gpsEnabled) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 360000, 1000, this);
            //mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (networkEnabled) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 360000, 1000, this);
            if (mLocation == null){
                //mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

        }
        if (!gpsEnabled && !networkEnabled) {
            Toast.makeText(this, getString(R.string.error_gps_off), Toast.LENGTH_SHORT).show();
        }
        /*
        *To enable address after getting the geolocation
        */
        if (gpsEnabled && networkEnabled){
            enableAddress();
        }
        /*if(mLocation != null){
            getAddressDetails();
        }*/
    }

    public void validateAndSubmitData() {
        boolean hasError = mHolder.validate();
        if (!hasError) {
            Doctor doctor = mHolder.getBean();
            if (mSubmitTask != null) mSubmitTask.cancel(true);
            if (mExistingDoctor != null) {
                showLoader(null, getString(R.string.msg_updating_doctor));
            } else {
                showLoader(null, getString(R.string.msg_adding_doctor));
            }
            mSubmitTask = new AddDoctorAsyncTask(this, mSubmitHandler);
            mSubmitTask.execute(doctor);
        }
    }

    private void displayTimerDialog(final View view) throws ParseException {
        final DefaultEditText inst = (DefaultEditText) view;
        // Current Hour
        int hour = 0;
        // Current Minute
        int minute = 0;
        final Calendar c;
        if (TextUtils.isEmpty(inst.getText().toString())) {
            c = Calendar.getInstance();
        } else {
            c = parseCalendar(inst.getText().toString(), DATE_FORMAT);
        }
        // Current Hour
        hour = c.get(Calendar.HOUR_OF_DAY);
        // Current Minute
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                updateTime(inst, i, i1);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, timeSetListener, hour, minute, false);
        timePickerDialog.show();
    }

    // Used to convert 24hr format to 12hr format with AM/PM values
    private void updateTime(DefaultEditText output, int hours, int mins) {
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();
        output.setText(aTime);
    }

    private void selectSpeciality(final View view) {
        mErrorMsg.setVisibility(View.GONE);
        if (mSpecialities != null && mSpecialities.size() > 0) {
            openContextMenu(view);
        } else {
            if (mSpecialityTask != null) mSpecialityTask.cancel(true);
            showLoader(null, null);
            mSpecialityTask = new SpecialityAsyncTask(this, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.getData().containsKey(SpecialityAsyncTask.SUCCESS_DATA)) {
                        Object[] objects = (Object[]) msg.getData().getSerializable(SpecialityAsyncTask.SUCCESS_DATA);
                        Speciality[] specialities = Arrays.copyOf(objects, objects.length, Speciality[].class);
                        mSpecialities = new ArrayList<Speciality>(Arrays.asList(specialities));
                        openContextMenu(view);
                        mErrorMsg.setVisibility(View.GONE);
                    } else if (msg.getData().containsKey(SpecialityAsyncTask.ERROR_DATA)) {
                        String errorMsg = msg.getData().getString(SpecialityAsyncTask.ERROR_DATA);
                        setError(errorMsg);
                    }
                    hideLoader();
                }
            });
            mSpecialityTask.execute();
        }
    }

    public Speciality getMatchingSpeciality(MenuItem menuItem) {
        for (Speciality speciality : mSpecialities) {
            if (menuItem.getTitle().equals(speciality.getSpeciality_Name())) {
                return speciality;
            }
        }
        return null;
    }

    public void setError(String errorMsg) {
        mErrorMsg.setText(errorMsg);
        mErrorMsg.setVisibility(View.VISIBLE);
        mScrollView.smoothScrollTo(0, 0);
    }

    /*
    To enable address changed on dec/23/15
    */
    public void enableAddress(){
        mHolder.address.setEnabled(true);
        mHolder.locationPickLayout.setVisibility(View.GONE);
        mHolder.locationUpdateLayout.setVisibility(View.VISIBLE);
    }
}
