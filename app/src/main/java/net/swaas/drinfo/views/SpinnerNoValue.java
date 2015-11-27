package net.swaas.drinfo.views;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import net.swaas.drinfo.R;

import java.lang.reflect.Method;

/**
 * Created by vinoth on 10/22/15.
 */
public class SpinnerNoValue extends DefaultSpinner {

    public SpinnerNoValue(Context context) {
        super(context);
    }

    public SpinnerNoValue(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpinnerNoValue(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(SpinnerAdapter orig) {
        final SpinnerAdapter adapter = new SpinnerAdapterWithNoValue(orig);
        super.setAdapter(adapter);

        try {
            final Method m = AdapterView.class.getDeclaredMethod("setNextSelectedPositionInt", int.class);
            m.setAccessible(true);
            m.invoke(this, -1);

            final Method n = AdapterView.class.getDeclaredMethod("setSelectedPositionInt", int.class);
            n.setAccessible(true);
            n.invoke(this, -1);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * getSelectedItem renvoi null si la valeur par defaut est séléctionnée
     *
     * @see android.widget.AdapterView#getSelectedItem()
     */
    @Override
    public Object getSelectedItem() {
        return super.getSelectedItem();
    }

    public class SpinnerAdapterWithNoValue implements SpinnerAdapter {

        private SpinnerAdapter _current;
        private final static String defaultValue = "Choisir";

        public SpinnerAdapterWithNoValue(SpinnerAdapter base) {
            _current = base;
        }

        @Override
        public int getCount() {
            return _current.getCount() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0 || position == -1) {
                return null;
            }
            return _current.getItem(position - 1);
        }

        @Override
        public long getItemId(int position) {
            if (position == 0 || position == -1) {
                return -1;
            }
            return _current.getItemId(position - 1);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == -1) {
                return -1;
            }
            return _current.getItemViewType(position - 1);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0 || position == -1) {
                /*final TextView v = (TextView) ((LayoutInflater) getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE)).inflate(android.R.layout.simple_spinner_item, parent, false);
                v.setText(defaultValue);*/
                final ImageView v = (ImageView) ((LayoutInflater) getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.default_spinner_image, parent, false);
                return v;
            }
            return _current.getView(position - 1, convertView, parent);
        }

        @Override
        public int getViewTypeCount() {
            return _current.getViewTypeCount();
        }

        @Override
        public boolean hasStableIds() {
            return _current.hasStableIds();
        }

        @Override
        public boolean isEmpty() {
            return _current.isEmpty();
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            _current.registerDataSetObserver(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            // TODO Auto-generated method stub
            _current.unregisterDataSetObserver(observer);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return _current.getDropDownView(position, convertView, parent);
        }
    }
}
