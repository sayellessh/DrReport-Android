package net.swaas.drinfo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import net.swaas.drinfo.R;
import net.swaas.drinfo.adapters.DoctorReportRecyclerAdapter;
import net.swaas.drinfo.asynctasks.AllDoctorsAsyncTask;
import net.swaas.drinfo.asynctasks.RecentDoctorsAsyncTask;
import net.swaas.drinfo.beans.Doctor;
import net.swaas.drinfo.logger.LogTracer;
import net.swaas.drinfo.utils.SettingsUtils;
import net.swaas.drinfo.views.DefaultTextView;
import net.swaas.headerdecor.DividerDecoration;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by vinoth on 11/18/15.
 */
public class DoctorReportActivity extends BaseActivity {

    private static final LogTracer LOG_TRACER = LogTracer.instance(DoctorReportActivity.class);
    private View mErrorMsg;
    private RecyclerView mRecyclerView;
    private DoctorReportRecyclerAdapter mRecyclerAdapter;
    private List<Doctor> mDoctors;
    private AllDoctorsAsyncTask mTask;
    Toolbar mToolbar;
    //private TopStickyHeaderDecoration decor;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideLoader();
            if (msg.getData().containsKey(RecentDoctorsAsyncTask.SUCCESS_DATA)) {
                Object[] objects = (Object[]) msg.getData().getSerializable(RecentDoctorsAsyncTask.SUCCESS_DATA);
                Doctor[] doctors = Arrays.copyOf(objects, objects.length, Doctor[].class);
                mDoctors = new ArrayList<Doctor>(Arrays.asList(doctors));
                Collections.sort(mDoctors, new Comparator<Doctor>() {
                    @Override
                    public int compare(Doctor doctor, Doctor t1) {
                        try {
                            return doctor.getUpdatedDate().compareTo(t1.getUpdatedDate());
                        } catch (ParseException e) {

                        }
                        return 0;
                    }
                });
                Collections.reverse(mDoctors);
                if (mRecyclerAdapter != null) {
                    mRecyclerAdapter.addAll(mDoctors);
                    mRecyclerAdapter.notifyDataSetChanged();
                } else {
                    mRecyclerAdapter = new DoctorReportRecyclerAdapter(DoctorReportActivity.this, mDoctors);
                    mRecyclerAdapter.setEmptyView(mErrorMsg);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(DoctorReportActivity.this));
                    final DividerDecoration divider = new DividerDecoration.Builder(DoctorReportActivity.this)
                            .setHeight(R.dimen.default_divider_height)
                            .setPadding(R.dimen.default_divider_padding)
                            .setColorResource(R.color.default_header_color)
                            .build();

                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(DoctorReportActivity.this));
                    //mRecyclerView.addItemDecoration(divider);

                    mRecyclerView.setAdapter(mRecyclerAdapter);
                    mRecyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(mRecyclerAdapter));
                }
                //mErrorMsg.setVisibility(View.GONE);
                //mRecyclerView.setVisibility(View.VISIBLE);
                if (mErrorMsg instanceof DefaultTextView) {
                    ((DefaultTextView) mErrorMsg).setText(getString(R.string.empty_doctors_report));
                }
            } else if (msg.getData().containsKey(RecentDoctorsAsyncTask.ERROR_DATA)){
                LOG_TRACER.d(msg.getData().getString(RecentDoctorsAsyncTask.ERROR_DATA));
                //mErrorMsg.setVisibility(View.VISIBLE);
                //mRecyclerView.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_report);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.doctor_list);
        mErrorMsg = findViewById(R.id.common_error);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.report));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mRecyclerAdapter = new DoctorReportRecyclerAdapter(DoctorReportActivity.this, new ArrayList<Doctor>());
        mRecyclerAdapter.setEmptyView(mErrorMsg);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DoctorReportActivity.this));
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(mRecyclerAdapter));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDoctors == null || mDoctors.size() <= 0) {
            if (mTask != null) mTask.cancel(true);
            mTask = new AllDoctorsAsyncTask(this, mHandler);
            showLoader(null, null, true);
            mTask.execute();
        }
    }
}
