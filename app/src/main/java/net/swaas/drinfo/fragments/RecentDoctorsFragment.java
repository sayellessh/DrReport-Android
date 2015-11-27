package net.swaas.drinfo.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.swaas.drinfo.R;
import net.swaas.drinfo.activity.BaseActivity;
import net.swaas.drinfo.adapters.BaseFragmentPagerAdapter;
import net.swaas.drinfo.adapters.RecentDoctorsRecyclerAdapter;
import net.swaas.drinfo.asynctasks.RecentDoctorsAsyncTask;
import net.swaas.drinfo.beans.Doctor;
import net.swaas.drinfo.logger.LogTracer;
import net.swaas.drinfo.utils.SettingsUtils;
import net.swaas.drinfo.views.DefaultRecyclerView;
import net.swaas.drinfo.views.DefaultTextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by vinoth on 10/22/15.
 */
public class RecentDoctorsFragment extends BaseFragment implements BaseFragment.PagerSlideChange {

    public static final LogTracer LOG_TRACER = LogTracer.instance(RecentDoctorsFragment.class);
    BaseActivity mActivity;
    private View mView;
    private DefaultRecyclerView mRecyclerView;
    private RecentDoctorsRecyclerAdapter mRecyclerAdapter;
    private View mErrorMsg;
    private View mMessageLayout;
    private List<Doctor> mDoctors;
    private RecentDoctorsAsyncTask mTask;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mActivity.hideLoader();
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
                            LOG_TRACER.e(e);
                            return 0;
                        }
                    }
                });
                Collections.reverse(mDoctors);
                if (mRecyclerAdapter != null) {
                    mRecyclerAdapter.addAll(mDoctors);
                    mRecyclerAdapter.notifyDataSetChanged();
                } else {
                    mRecyclerAdapter = new RecentDoctorsRecyclerAdapter(RecentDoctorsFragment.this.getActivity(), mDoctors);
                    mRecyclerAdapter.setEmptyView(mErrorMsg);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(RecentDoctorsFragment.this.getActivity()));
                    mRecyclerView.setAdapter(mRecyclerAdapter);
                }
                //mErrorMsg.setVisibility(View.GONE);
                //mRecyclerView.setVisibility(View.VISIBLE);
                if (mErrorMsg instanceof DefaultTextView) {
                    ((DefaultTextView) mErrorMsg).setText(getString(R.string.empty_doctors));
                }
                if (mDoctors != null && mDoctors.size() > 0) {
                    mMessageLayout.setVisibility(View.VISIBLE);
                } else {
                    mMessageLayout.setVisibility(View.GONE);
                }
            } else if (msg.getData().containsKey(RecentDoctorsAsyncTask.ERROR_DATA)){
                LOG_TRACER.d(msg.getData().getString(RecentDoctorsAsyncTask.ERROR_DATA));
                //mErrorMsg.setVisibility(View.VISIBLE);
                //mRecyclerView.setVisibility(View.GONE);
            }
        }
    };

    public RecentDoctorsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_recent_doctors_list, container, false);
        mActivity = (BaseActivity) getActivity();
        mRecyclerView = (DefaultRecyclerView) mView.findViewById(R.id.doctor_list);
        mErrorMsg = mView.findViewById(R.id.common_error);
        mMessageLayout = mView.findViewById(R.id.common_text);
        mRecyclerAdapter = new RecentDoctorsRecyclerAdapter(RecentDoctorsFragment.this.getActivity(), new ArrayList<Doctor>());
        mRecyclerAdapter.setEmptyView(mErrorMsg);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(RecentDoctorsFragment.this.getActivity()));
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mMessageLayout.setVisibility(View.GONE);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onShow();
    }

    @Override
    public void onShow() {
        if (mDoctors == null || mDoctors.size() <= 0 || SettingsUtils.getRecentDoctorUpdateRequired(mActivity)) {
            if (mTask != null) mTask.cancel(true);
            mTask = new RecentDoctorsAsyncTask(this.getActivity(), mHandler);
            mActivity.showLoader(null, null, true);
            mTask.execute();
        }
    }

    @Override
    public void onHide() {
        if (mTask != null) mTask.cancel(true);
    }
}
