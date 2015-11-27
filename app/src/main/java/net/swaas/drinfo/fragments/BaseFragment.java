package net.swaas.drinfo.fragments;

import android.support.v4.app.Fragment;

/**
 * Created by vinoth on 10/22/15.
 */
public class BaseFragment extends Fragment {

    public interface Searchable {
        public void onSearch(CharSequence query);
    }

    public interface PagerSlideChange {
        public void onShow();
        public void onHide();
    }
}
