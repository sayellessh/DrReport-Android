package net.swaas.drinfo.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import net.swaas.drinfo.R;
import net.swaas.drinfo.adapters.DoctorInfoPagerAdapter;
import net.swaas.drinfo.core.NetworkConnection;
import net.swaas.drinfo.fragments.AllDoctorsFragment;
import net.swaas.drinfo.fragments.BaseFragment;
import net.swaas.drinfo.fragments.RecentDoctorsFragment;
import net.swaas.drinfo.logger.LogTracer;

/**
 * Created by vinoth on 10/20/15.
 */
public class DoctorDetailsActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private static final LogTracer LOG_TRACER = LogTracer.instance(DoctorDetailsActivity.class);
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private DoctorInfoPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private FloatingActionButton mFab;
    private MenuItem mSearchMenuItem;
    private SearchView mSearchViewAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctordetails);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.recent)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.all_doctors)));
        mPagerAdapter = new DoctorInfoPagerAdapter(this, getSupportFragmentManager(), mTabLayout.getTabCount(), null);
        mPagerAdapter.addFragment(new RecentDoctorsFragment(), null, getString(R.string.recent), 0);
        mPagerAdapter.addFragment(new AllDoctorsFragment(), null, getString(R.string.all_doctors), 1);
        mViewPager.setAdapter(mPagerAdapter);
        final int pageCount = mPagerAdapter.getCount() - 1;
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSearchMenuItem.collapseActionView();
                if (position == 1) {
                    mSearchMenuItem.setVisible(true);
                } else {
                    mSearchMenuItem.setVisible(false);
                }
                if (pageCount >= 0) {
                    for (int i = 0; i <= pageCount; i++) {
                        if (mPagerAdapter.getFragment(i) instanceof BaseFragment.PagerSlideChange) {
                            if (i == position) {
                                ((BaseFragment.PagerSlideChange) mPagerAdapter.getFragment(i)).onShow();
                            } else {
                                ((BaseFragment.PagerSlideChange) mPagerAdapter.getFragment(i)).onHide();
                            }
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(1);
                ((BaseFragment.PagerSlideChange) mPagerAdapter.getFragment(1)).onShow();
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkConnection.isNetworkAvailable(DoctorDetailsActivity.this))
                    startActivity(new Intent(DoctorDetailsActivity.this, AddDoctorActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_doctordetails, menu);
        SearchManager SManager =  (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        mSearchViewAction = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
        mSearchViewAction.setSearchableInfo(SManager.getSearchableInfo(getComponentName()));
        mSearchViewAction.setIconifiedByDefault(false);
        mSearchViewAction.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_change_password: {
                openChangePassword();
                break;
            }
            case R.id.action_view_report: {
                openDoctorReport();
                break;
            }
            case R.id.action_help: {
                openHelp();
                break;
            }
            case R.id.action_about: {
                openAbout();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void openChangePassword() {
        startActivity(new Intent(this, ChangePasswordActivity.class));
    }

    public void openDoctorReport() {
        startActivity(new Intent(this, DoctorReportActivity.class));
    }

    public void openHelp() {
        //startActivity(new Intent(this, HelpActivity.class));
        String url = "http://drreporthelp.hidoctor.me/";
        openActionView(url);
    }

    public void openAbout() {
        startActivity(new Intent(this, AboutActivity.class));
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Fragment fragment = mPagerAdapter.getItem(mViewPager.getCurrentItem());
        if (fragment != null && fragment instanceof BaseFragment.Searchable) {
            ((BaseFragment.Searchable) fragment).onSearch(newText);
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}
