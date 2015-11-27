package net.swaas.drinfo.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.swaas.drinfo.R;
import net.swaas.drinfo.activity.BaseActivity;
import net.swaas.drinfo.adapters.AllDoctorsRecyclerAdapter;
import net.swaas.drinfo.adapters.BaseAbstractRecyclerAdapter;
import net.swaas.drinfo.adapters.RecentDoctorsRecyclerAdapter;
import net.swaas.drinfo.asynctasks.AllDoctorsAsyncTask;
import net.swaas.drinfo.asynctasks.RecentDoctorsAsyncTask;
import net.swaas.drinfo.beans.Doctor;
import net.swaas.drinfo.dao.DoctorDAO;
import net.swaas.drinfo.logger.LogTracer;
import net.swaas.drinfo.utils.RecyclerHeaderDecoration;
import net.swaas.drinfo.utils.SettingsUtils;
import net.swaas.drinfo.views.DefaultRecyclerView;
import net.swaas.drinfo.views.DefaultTextView;
import net.swaas.headerdecor.DividerDecoration;
import net.swaas.headerdecor.StickyHeaderDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by vinoth on 10/22/15.
 */
public class AllDoctorsFragment extends BaseFragment implements BaseFragment.PagerSlideChange, BaseFragment.Searchable {

    public static final LogTracer LOG_TRACER = LogTracer.instance(AllDoctorsFragment.class);
    BaseActivity mActivity;
    private View mView;
    private View mErrorMsg;
    private DefaultRecyclerView mRecyclerView;
    private AllDoctorsRecyclerAdapter mRecyclerAdapter;
    private List<Doctor> mDoctors;
    private AllDoctorsAsyncTask mTask;
    private StickyHeaderDecoration decor;
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
                        return doctor.getDisplayName().compareToIgnoreCase(t1.getDisplayName());
                    }
                });
                if (mRecyclerAdapter != null) {
                    mRecyclerAdapter.addAll(mDoctors);
                    mRecyclerAdapter.notifyDataSetChanged();
                } else {
                    mRecyclerAdapter = new AllDoctorsRecyclerAdapter(AllDoctorsFragment.this.getActivity(), mDoctors);
                    mRecyclerAdapter.setEmptyView(mErrorMsg);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(AllDoctorsFragment.this.getActivity()));
                    final DividerDecoration divider = new DividerDecoration.Builder(AllDoctorsFragment.this.getActivity())
                            .setHeight(R.dimen.default_divider_height)
                            .setPadding(R.dimen.default_divider_padding)
                            .setColorResource(R.color.default_header_color)
                            .build();

                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(AllDoctorsFragment.this.getActivity()));
                    //mRecyclerView.addItemDecoration(divider);

                    decor = new StickyHeaderDecoration(mRecyclerAdapter);
                    mRecyclerView.setAdapter(mRecyclerAdapter);
                    mRecyclerView.addItemDecoration(decor);
                }
                /*mRecyclerAdapter.setOnLoadMoreListener(mRecyclerView, new BaseAbstractRecyclerAdapter.OnLoadMoreListener() {

                    int offset = 0;
                    int limit = 100;

                    @Override
                    public void onLoadMore() {
                        List<Doctor> newDoctors = new DoctorDAO(mActivity).getAlphabetically(limit, offset);
                        offset += limit;
                        mRecyclerAdapter.appendAll(newDoctors);
                    }
                });*/
                //mErrorMsg.setVisibility(View.GONE);
                //mRecyclerView.setVisibility(View.VISIBLE);
                if (mErrorMsg instanceof DefaultTextView) {
                    ((DefaultTextView) mErrorMsg).setText(getString(R.string.empty_doctors));
                }
            } else if (msg.getData().containsKey(RecentDoctorsAsyncTask.ERROR_DATA)){
                LOG_TRACER.d(msg.getData().getString(RecentDoctorsAsyncTask.ERROR_DATA));
                if (mErrorMsg instanceof DefaultTextView) {
                    ((DefaultTextView) mErrorMsg).setText(msg.getData().getString(RecentDoctorsAsyncTask.ERROR_DATA));
                }
                //mErrorMsg.setVisibility(View.VISIBLE);
                //mRecyclerView.setVisibility(View.GONE);
            }
        }
    };

    public AllDoctorsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_doctors_list, container, false);
        mActivity = (BaseActivity) getActivity();
        mRecyclerView = (DefaultRecyclerView) mView.findViewById(R.id.doctor_list);
        mErrorMsg = mView.findViewById(R.id.common_error);

        mRecyclerAdapter = new AllDoctorsRecyclerAdapter(AllDoctorsFragment.this.getActivity(), new ArrayList<Doctor>());
        mRecyclerAdapter.setEmptyView(mErrorMsg);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(AllDoctorsFragment.this.getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(AllDoctorsFragment.this.getActivity()));
        decor = new StickyHeaderDecoration(mRecyclerAdapter);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.addItemDecoration(decor);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onShow();
    }

    @Override
    public void onShow() {
        if (mDoctors == null || mDoctors.size() <= 0 || SettingsUtils.getDoctorUpdateRequired(mActivity)) {
            if (mTask != null) mTask.cancel(true);
            mTask = new AllDoctorsAsyncTask(this.getActivity(), mHandler);
            mActivity.showLoader(null, null, true);
            mTask.execute();
        } else if (mRecyclerAdapter != null) {
            mRecyclerAdapter.addAll(mDoctors);
            mRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onHide() {
        if (mTask != null) mTask.cancel(true);
    }

    @Override
    public void onSearch(CharSequence query) {
        if (mRecyclerAdapter != null) {
            mRecyclerAdapter.getFilter().filter(query);
        }
    }
}
