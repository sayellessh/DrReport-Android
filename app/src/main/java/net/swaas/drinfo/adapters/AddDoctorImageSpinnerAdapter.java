package net.swaas.drinfo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.swaas.drinfo.R;

import java.util.List;

/**
 * Created by vinoth on 10/22/15.
 */
public class AddDoctorImageSpinnerAdapter extends ArrayAdapter<String> {

    private int hideItemIndex;

    public AddDoctorImageSpinnerAdapter(Context context, int textViewResourceId,
                                        List<String> objects) {
        super(context, textViewResourceId, objects);
        this.hideItemIndex = 0;
    }



    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (convertView != null) {

        } else {
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent,
                    false);
        }

        if (position == hideItemIndex) {
            convertView.setVisibility(View.GONE);
        } else {
            convertView.setVisibility(View.VISIBLE);
            convertView = super.getDropDownView(position, null, parent);
        }
        return convertView;

    }
}
