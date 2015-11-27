package net.swaas.drinfo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import net.swaas.drinfo.R;
import net.swaas.drinfo.beans.Doctor;
import net.swaas.drinfo.logger.LogTracer;
import net.swaas.drinfo.views.CircularContactView;
import net.swaas.drinfo.views.DefaultTextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by vinoth on 10/22/15.
 */
public class DoctorReportRecyclerAdapter
        extends BaseAbstractRecyclerAdapter<DoctorReportRecyclerAdapter.ViewHolder>
        implements StickyRecyclerHeadersAdapter<DoctorReportRecyclerAdapter.HeaderHolder>, Filterable {

    private static final LogTracer LOG_TRACER = LogTracer.instance(DoctorReportRecyclerAdapter.class);

    private Context mContext;
    private List<Doctor> mDoctors;
    private List<Doctor> mFilteredDoctors;
    private LayoutInflater mLayoutInflater;
    private final int CONTACT_PHOTO_IMAGE_SIZE;
    private final int[] PHOTO_TEXT_BACKGROUND_COLORS;

    public DoctorReportRecyclerAdapter(Context context, List<Doctor> doctors) {
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
        this.mFilteredDoctors = doctors;
        if (getEmptyView() != null) {
            getEmptyView().setVisibility(mFilteredDoctors.size() <= 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mFilteredDoctors.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder vh = (ViewHolder) holder;
        final Doctor doctor = mFilteredDoctors.get(position);
        String displayName = doctor.getDisplayName();
        vh.doctorName.setText(displayName);
        String speciality = String.format(mContext.getString(R.string.formatted_speciality), TextUtils.isEmpty(doctor.getSpeciality_Name()) ? mContext.getString(R.string.na) : doctor.getSpeciality_Name());
        vh.doctorSpeciality.setText(speciality);
        String phoneNumber = String.format(mContext.getString(R.string.formatted_phone), TextUtils.isEmpty(doctor.getPhone_Number()) ? mContext.getString(R.string.na) : doctor.getPhone_Number());
        String emailId = String.format(mContext.getString(R.string.formatted_email), TextUtils.isEmpty(doctor.getEmail_Id()) ? mContext.getString(R.string.na) : doctor.getEmail_Id());
        vh.doctorPhone.setText(phoneNumber);
        vh.doctorEmail.setText(emailId);
        final int backgroundColorToUse = PHOTO_TEXT_BACKGROUND_COLORS[position
                % PHOTO_TEXT_BACKGROUND_COLORS.length];
        if (!TextUtils.isEmpty(displayName)) {
            final String characterToShow = TextUtils.isEmpty(displayName) ? "" : displayName.substring(0, 1).toUpperCase(Locale.getDefault());
            vh.thumbnail.setTextAndBackgroundColor(characterToShow, backgroundColorToUse);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.row_report_doctor_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public long getHeaderId(int position) {
        if (position < 0) {
            return -1;
        } else {
            try {
                return mFilteredDoctors.get(position).getUpdatedDateLong();
            } catch (ParseException e) {
                LOG_TRACER.e(e);
                return -1;
            }
            //return mFilteredDoctors.get(position).getDisplayName().charAt(0);
        }
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = mLayoutInflater.inflate(R.layout.row_report_header, parent, false);
        return new HeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder viewHolder, int position) {
        try {
            viewHolder.header.setText(mFilteredDoctors.get(position).getFormattedUpdatedDate());
        } catch (ParseException e) {
            LOG_TRACER.e(e);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        DefaultTextView doctorName;
        DefaultTextView doctorSpeciality;
        CircularContactView thumbnail;
        DefaultTextView doctorPhone;
        DefaultTextView doctorEmail;
        public ViewHolder(View v) {
            super(v);
            view = v;
            doctorName = (DefaultTextView) view.findViewById(R.id.doctor_name);
            doctorSpeciality = (DefaultTextView) view.findViewById(R.id.doctor_speciality);
            doctorPhone = (DefaultTextView) view.findViewById(R.id.doctor_phone);
            doctorEmail = (DefaultTextView) view.findViewById(R.id.doctor_email);
            thumbnail = (CircularContactView) view.findViewById(R.id.thumbnail);
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        public TextView header;

        public HeaderHolder(View itemView) {
            super(itemView);

            header = (TextView) itemView.findViewById(R.id.header_text);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Doctor> nlist = new ArrayList<>(mDoctors.size());
                int len = mDoctors.size() - 1;
                for (int i=0;i<=len;i++) {
                    if (mDoctors.get(i).getDisplayName().toLowerCase().trim().contains(charSequence.toString().toLowerCase())) {
                        nlist.add(mDoctors.get(i));
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = nlist;
                filterResults.count = nlist.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredDoctors = (List<Doctor>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
