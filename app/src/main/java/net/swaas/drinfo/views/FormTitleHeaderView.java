package net.swaas.drinfo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.swaas.drinfo.R;

/**
 * Created by vinoth on 11/21/15.
 */
public class FormTitleHeaderView extends DefaultRelativeLayout {
    private View mSplitView;
    private DefaultTextView mTextView;
    private int tv_background;
    private int tv_gravity;
    private int tv_paddingLeft;
    private int tv_paddingRight;
    private int tv_paddingTop;
    private int tv_paddingBottom;
    private int tv_textColor;
    private int tv_textTypeface;
    private String tv_text;

    public FormTitleHeaderView(Context context) {
        super(context, null);
    }

    public FormTitleHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        View mView = inflater.inflate(R.layout.form_title_header_view, this, true);
        mTextView = (DefaultTextView) mView.findViewById(android.R.id.title);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FormTitleHeaderView);
            int idtext = a.getResourceId(R.styleable.FormTitleHeaderView_tv_text, 0);
            if (idtext > 0) {
                tv_text = context.getResources().getString(idtext);
            }
            tv_background = a.getResourceId(R.styleable.FormTitleHeaderView_tv_background, android.R.attr.background);
            mTextView.setBackgroundResource(tv_background);
            // gravity not work, need rework
            /*tv_gravity = a.getInteger(R.styleable.FormTitleHeaderView_tv_gravity, Gravity.CENTER);
            mTextView.setGravity(tv_gravity);*/
            tv_paddingLeft = a.getDimensionPixelSize(R.styleable.FormTitleHeaderView_tv_paddingLeft, 0);
            tv_paddingRight = a.getDimensionPixelSize(R.styleable.FormTitleHeaderView_tv_paddingRight, 0);
            tv_paddingTop = a.getDimensionPixelSize(R.styleable.FormTitleHeaderView_tv_paddingTop, 0);
            tv_paddingBottom = a.getDimensionPixelSize(R.styleable.FormTitleHeaderView_tv_paddingBottom, 0);
            mTextView.setPadding(tv_paddingLeft, tv_paddingTop, tv_paddingRight, tv_paddingBottom);
            tv_textColor = a.getColor(R.styleable.FormTitleHeaderView_tv_textColor, FormTitleHeaderView.getColorWrapper(context, android.R.color.black));
            tv_textTypeface = a.getInt(R.styleable.FormTitleHeaderView_tv_textTypeface, Typeface.BOLD);
            mTextView.setTextColor(tv_textColor);
            mTextView.setTypeface(mTextView.getTypeface(), tv_textTypeface);
            a.recycle();
        }
        if (!TextUtils.isEmpty(tv_text)) {
            mTextView.setText(tv_text);
        }
        //addView(mTextView);
    }

    public static int getColorWrapper(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            return context.getResources().getColor(id);
        }
    }
}
