package net.swaas.drinfo.activity;

import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import net.swaas.drinfo.R;
import net.swaas.drinfo.logger.LogTracer;
import net.swaas.drinfo.utils.SettingsUtils;
import net.swaas.drinfo.views.DefaultTextView;

/**
 * Created by vinoth on 11/25/15.
 */
public class AboutActivity extends BaseActivity {

    private static final LogTracer LOG_TRACER = LogTracer.instance(AboutActivity.class);
    private DefaultTextView mVersionText;
    private Toolbar mToolbar;
    private DefaultTextView mWebsite;
    private DefaultTextView mContactUs;
    private DefaultTextView mUserName;
    private DefaultTextView mContactPhone;
    private DefaultTextView mContactEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.action_about));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mVersionText = (DefaultTextView) findViewById(R.id.about_version);
        mWebsite = (DefaultTextView) findViewById(R.id.website);
        mContactUs = (DefaultTextView) findViewById(R.id.contact_us);
        mUserName = (DefaultTextView) findViewById(R.id.current_user_log);
        mContactPhone = (DefaultTextView) findViewById(R.id.contact_phone);
        mContactEmail = (DefaultTextView) findViewById(R.id.contact_email);
        try {
            mVersionText.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            mVersionText.setText(e.getMessage());
        }
        String userName = SettingsUtils.getUserInfo(this).getUsername();
        mUserName.setText(String.format(getString(R.string.current_user_log), userName));
        mWebsite.setPaintFlags(mWebsite.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mContactPhone.setPaintFlags(mContactUs.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mContactEmail.setPaintFlags(mContactUs.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActionView("http://" + mWebsite.getText().toString());
            }
        });
        mContactPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActionView(getString(R.string.phoneto_contact));
            }
        });
        mContactEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(android.content.Intent.EXTRA_SUBJECT, getString(R.string.support_subject));
                openActionView(getString(R.string.mailto_contact), bundle);
            }
        });
    }
}
