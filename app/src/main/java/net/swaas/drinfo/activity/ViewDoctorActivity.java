package net.swaas.drinfo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

import net.swaas.drinfo.R;
import net.swaas.drinfo.beans.Doctor;
import net.swaas.drinfo.core.FileDownloader;
import net.swaas.drinfo.dao.DoctorDAO;
import net.swaas.drinfo.logger.LogTracer;
import net.swaas.drinfo.utils.SettingsUtils;
import net.swaas.drinfo.views.DefaultLinearLayout;
import net.swaas.drinfo.views.FormElementView;
import net.swaas.drinfo.views.FormElementView;

import org.w3c.dom.Text;

import java.math.BigInteger;

/**
 * Created by vinoth on 11/25/15.
 */
public class ViewDoctorActivity extends BaseActivity {

    private static final LogTracer LOG_TRACER = LogTracer.instance(ViewDoctorActivity.class);
    private Toolbar mToolbar;
    private Doctor mExistingDoctor;
    private ViewHolder mHolder;
    AlertDialog mAlertDialog = null;
    private FileDownloader mFileDownloader;
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
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewDoctorActivity.this);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_doctor);
        mExistingDoctor = (Doctor) getIntent().getSerializableExtra(AddDoctorActivity.EXISTING_DOCTOR);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mExistingDoctor = new DoctorDAO(this).get(mExistingDoctor.getDoctor_Id());
        mHolder = new ViewHolder();
        mHolder.updateValues();
    }

    public class ViewHolder {

        CollapsingToolbarLayout collapsingToolbarLayout;
        ImageView hospitalImage;
        FormElementView speciality;
        FormElementView phoneNumber;
        FormElementView email;
        FormElementView hospitalName;
        FormElementView hospitalAddress;
        FormElementView landmark;
        FormElementView workFrom1;
        FormElementView workFrom2;
        FormElementView workFrom3;
        FormElementView workTo1;
        FormElementView workTo2;
        FormElementView workTo3;
        FormElementView assistantName;
        FormElementView assistantPhone;
        FormElementView remarks;
        FormElementView trainerCode;
        CardView assistantLayout;
        CardView otherInfoLayout;

        public ViewHolder() {
            collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            hospitalImage = (ImageView) findViewById(R.id.backdrop);
            speciality = (FormElementView) findViewById(R.id.speciality);
            phoneNumber = (FormElementView) findViewById(R.id.contact_phone);
            email = (FormElementView) findViewById(R.id.contact_email);
            hospitalName = (FormElementView) findViewById(R.id.hospital_name);
            hospitalAddress = (FormElementView) findViewById(R.id.address);
            landmark = (FormElementView) findViewById(R.id.landmark);
            workFrom1 = (FormElementView) findViewById(R.id.work_from_1);
            workFrom2 = (FormElementView) findViewById(R.id.work_from_2);
            workFrom3 = (FormElementView) findViewById(R.id.work_from_3);
            workTo1 = (FormElementView) findViewById(R.id.work_to_1);
            workTo2 = (FormElementView) findViewById(R.id.work_to_2);
            workTo3 = (FormElementView) findViewById(R.id.work_to_3);
            assistantLayout = (CardView) findViewById(R.id.assistant_layout);
            assistantName = (FormElementView) findViewById(R.id.assistant_name);
            assistantPhone = (FormElementView) findViewById(R.id.assistant_phone);
            otherInfoLayout = (CardView) findViewById(R.id.other_information);
            remarks = (FormElementView) findViewById(R.id.remarks);
            trainerCode = (FormElementView) findViewById(R.id.trainer_code);
            if (SettingsUtils.getIsTrainer(ViewDoctorActivity.this)) {
                trainerCode.setVisibility(View.GONE);
            } else {
                trainerCode.setVisibility(View.VISIBLE);
            }
        }

        public void updateValues() {
            if (mExistingDoctor != null) {
                collapsingToolbarLayout.setTitle(mExistingDoctor.getDisplayName());
                if (!TextUtils.isEmpty(mExistingDoctor.getHospital_Photo_Url())) {
                    Ion.with(ViewDoctorActivity.this).load(mExistingDoctor.getHospital_Photo_Url()).intoImageView(hospitalImage);
                    hospitalImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mFileDownloader != null) mFileDownloader.cancel(true);
                            mFileDownloader = new FileDownloader(ViewDoctorActivity.this, mDownloadTask);
                            mFileDownloader.execute(mExistingDoctor.getHospital_Photo_Url());
                        }
                    });
                }
                speciality.setContent(mExistingDoctor.getSpeciality_Name());
                phoneNumber.setContent(mExistingDoctor.getPhone_Number());
                email.setContent(mExistingDoctor.getEmail_Id());
                hospitalName.setContent(mExistingDoctor.getHospital_Name());
                hospitalAddress.setContent(mExistingDoctor.getLocation_Full_Address());
                landmark.setContent(mExistingDoctor.getLandmark());
                workFrom1.setContent(mExistingDoctor.getWorking_From_Time());
                workFrom2.setContent(mExistingDoctor.getWorking_From_Time_2());
                workFrom3.setContent(mExistingDoctor.getWorking_From_Time_3());
                workTo1.setContent(mExistingDoctor.getWorking_To_Time());
                workTo2.setContent(mExistingDoctor.getWorking_To_Time_2());
                workTo3.setContent(mExistingDoctor.getWorking_To_Time_3());
                if (!TextUtils.isEmpty(mExistingDoctor.getAssistant_Phone_Number())
                        || !TextUtils.isEmpty(mExistingDoctor.getAssistant_Name())) {
                    assistantLayout.setVisibility(View.VISIBLE);
                    assistantName.setContent(mExistingDoctor.getAssistant_Name());
                    assistantPhone.setContent(mExistingDoctor.getAssistant_Phone_Number());
                } else {
                    assistantLayout.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(mExistingDoctor.getRemarks())
                        || mExistingDoctor.getTrainer_Code() != null) {
                    otherInfoLayout.setVisibility(View.VISIBLE);
                    remarks.setContent(mExistingDoctor.getRemarks());
                    trainerCode.setContent(mExistingDoctor.getTrainer_Code());
                } else {
                    otherInfoLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_viewdoctor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit: {
                Intent intent = new Intent(ViewDoctorActivity.this, AddDoctorActivity.class);
                intent.putExtra(AddDoctorActivity.EXISTING_DOCTOR, mExistingDoctor);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFileDownloader != null) mFileDownloader.cancel(true);
    }
}
