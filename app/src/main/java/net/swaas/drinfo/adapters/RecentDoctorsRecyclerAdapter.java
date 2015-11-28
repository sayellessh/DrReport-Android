package net.swaas.drinfo.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.swaas.drinfo.R;
import net.swaas.drinfo.activity.AddDoctorActivity;
import net.swaas.drinfo.activity.ViewDoctorActivity;
import net.swaas.drinfo.beans.Doctor;
import net.swaas.drinfo.views.CircularContactView;
import net.swaas.drinfo.views.DefaultRecyclerView;
import net.swaas.drinfo.views.DefaultTextView;

import java.util.List;
import java.util.Locale;

/**
 * Created by vinoth on 10/22/15.
 */
public class RecentDoctorsRecyclerAdapter extends BaseAbstractRecyclerAdapter<RecentDoctorsRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<Doctor> mDoctors;
    private LayoutInflater mLayoutInflater;
    private final int CONTACT_PHOTO_IMAGE_SIZE;
    private final int[] PHOTO_TEXT_BACKGROUND_COLORS;

    public RecentDoctorsRecyclerAdapter(Context context, List<Doctor> doctors) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(this.mContext);
        this.addAll(doctors);
        PHOTO_TEXT_BACKGROUND_COLORS = mContext.getResources()
                .getIntArray(R.array.contacts_text_background_colors);
        CONTACT_PHOTO_IMAGE_SIZE = mContext.getResources().getDimensionPixelSize(
                R.dimen.imageview_size);
    }

    public void addAll(List<Doctor> doctors) {
        this.mDoctors = doctors;
        if (getEmptyView() != null) {
            getEmptyView().setVisibility(mDoctors.size() <= 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mDoctors.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder vh = (ViewHolder) holder;
        String displayName = mDoctors.get(position).getDisplayName();
        vh.doctorName.setText(displayName);
        vh.doctorSpeciality.setText(mDoctors.get(position).getSpeciality_Name());
        final int backgroundColorToUse = PHOTO_TEXT_BACKGROUND_COLORS[position
                % PHOTO_TEXT_BACKGROUND_COLORS.length];
        if (!TextUtils.isEmpty(displayName)) {
            final String characterToShow = TextUtils.isEmpty(displayName) ? "" : displayName.substring(0, 1).toUpperCase(Locale.getDefault());
            vh.thumbnail.setTextAndBackgroundColor(characterToShow, backgroundColorToUse);
        }
        vh.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ViewDoctorActivity.class);
                intent.putExtra(AddDoctorActivity.EXISTING_DOCTOR, mDoctors.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.row_recent_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        DefaultTextView doctorName;
        DefaultTextView doctorSpeciality;
        CircularContactView thumbnail;
        public ViewHolder(View v) {
            super(v);
            view = v;
            doctorName = (DefaultTextView) view.findViewById(R.id.doctor_name);
            doctorSpeciality = (DefaultTextView) view.findViewById(R.id.doctor_speciality);
            thumbnail = (CircularContactView) view.findViewById(R.id.thumbnail);
        }
    }
}
