package net.swaas.drinfo.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import net.swaas.drinfo.R;
import net.swaas.drinfo.activity.AddDoctorActivity;
import net.swaas.drinfo.activity.ViewDoctorActivity;
import net.swaas.drinfo.asynctasks.AddDoctorAsyncTask;
import net.swaas.drinfo.beans.Doctor;
import net.swaas.drinfo.core.TouchExtendedClickListener;
import net.swaas.drinfo.views.CircularContactView;
import net.swaas.drinfo.views.DefaultTextView;
import net.swaas.headerdecor.StickyHeaderAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by vinoth on 10/22/15.
 */
public class AllDoctorsRecyclerAdapter
        extends BaseAbstractRecyclerAdapter<AllDoctorsRecyclerAdapter.ViewHolder>
        implements StickyHeaderAdapter<AllDoctorsRecyclerAdapter.HeaderHolder>, Filterable {

    private Context mContext;
    private List<Doctor> mDoctors;
    private List<Doctor> mFilteredDoctors;
    private LayoutInflater mLayoutInflater;
    private final int CONTACT_PHOTO_IMAGE_SIZE;
    private final int[] PHOTO_TEXT_BACKGROUND_COLORS;

    public AllDoctorsRecyclerAdapter(Context context, List<Doctor> doctors) {
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

    public void appendAll(List<Doctor> doctors) {
        this.mDoctors.addAll(doctors);
        this.mFilteredDoctors.addAll(doctors);
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
        String displayName = mFilteredDoctors.get(position).getDisplayName();
        vh.doctorName.setText(displayName);
        vh.doctorSpeciality.setText(mFilteredDoctors.get(position).getSpeciality_Name());
        final int backgroundColorToUse = PHOTO_TEXT_BACKGROUND_COLORS[position
                % PHOTO_TEXT_BACKGROUND_COLORS.length];
        if (!TextUtils.isEmpty(displayName)) {
            final String characterToShow = TextUtils.isEmpty(displayName) ? "" : displayName.substring(0, 1).toUpperCase(Locale.getDefault());
            vh.thumbnail.setTextAndBackgroundColor(characterToShow, backgroundColorToUse);
        }
        vh.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent  intent = new Intent(mContext, ViewDoctorActivity.class);
                intent.putExtra(AddDoctorActivity.EXISTING_DOCTOR, mFilteredDoctors.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.row_doctor_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public long getHeaderId(int position) {
        if (position < 0) {
            return -1;
        } else {
            return mFilteredDoctors.get(position).getDisplayName().toUpperCase().charAt(0);
        }
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = mLayoutInflater.inflate(R.layout.row_header, parent, false);
        return new HeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder viewHolder, int position) {
        viewHolder.header.setText(Character.toString(mFilteredDoctors.get(position).getDisplayName().toUpperCase().charAt(0)));
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
                if (getEmptyView() != null) {
                    getEmptyView().setVisibility(filterResults.count <= 0 ? View.VISIBLE : View.GONE);
                }
                notifyDataSetChanged();
            }
        };
    }
}
