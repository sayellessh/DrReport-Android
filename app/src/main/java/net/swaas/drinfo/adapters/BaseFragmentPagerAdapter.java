package net.swaas.drinfo.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import net.swaas.drinfo.logger.LogTracer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vinoth on 10/22/15.
 */
public class BaseFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private static final LogTracer LOG_TRACER = LogTracer.instance(BaseFragmentPagerAdapter.class);
    private int NUM_PAGES = 0;

    //private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;
    private Context mContext;
    private String[] titles;
    private Fragment[] fragments;
    private Bundle[] bundles;
    private Bundle mBundle;

    public BaseFragmentPagerAdapter(Context context, FragmentManager fragmentManager, int numPages, Bundle bundle) {
        super(fragmentManager);
        this.mContext = context;
        this.mFragmentManager = fragmentManager;
        this.NUM_PAGES = numPages;
        //mFragmentTags = new HashMap<Integer, String>();
        fragments = new Fragment[numPages];
        titles = new String[numPages];
        bundles = new Bundle[numPages];
        mBundle = bundle;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            // record the fragment tag here.
            Fragment f = (Fragment) obj;
            //String tag = f.getTag();
            //mFragmentTags.put(position, tag);
            fragments[position] = f;
        }
        return obj;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    public Fragment getFragment(int position) {
        /*String tag = mFragmentTags.get(position);
        if (tag == null)
            return null;
        return mFragmentManager.findFragmentByTag(tag);*/
        return fragments[position];
    }

    public void addFragment(Fragment fragment, Bundle extras, String title, int position) {
        fragments[position] = fragment;
        titles[position] = title;
        bundles[position] = extras;
    }

    public Context getContext() {
        return mContext;
    }
}
