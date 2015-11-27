package net.swaas.drinfo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import net.swaas.drinfo.R;

import java.math.BigInteger;

/**
 * Created by vinoth on 11/21/15.
 */
public class FormElementView extends DefaultRelativeLayout {
    private View mView;
    private DefaultTextView mTitle;
    private DefaultTextView mContent;
    private String tv_title;
    private String tv_content;
    private boolean tv_hideIfContentEmpty;

    public FormElementView(Context context) {
        super(context, null);
    }

    public FormElementView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.inflate(R.layout.element_viewer_layout, this, true);
        mTitle = (DefaultTextView) mView.findViewById(android.R.id.title);
        mContent = (DefaultTextView) mView.findViewById(android.R.id.message);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FormElementView);
            tv_title = a.getString(R.styleable.FormElementView_tv_title);
            tv_content = a.getString(R.styleable.FormElementView_tv_content);
            tv_hideIfContentEmpty = a.getBoolean(R.styleable.FormElementView_tv_hideIfContentEmpty, true);
            setContent(tv_content);
            a.recycle();
        }
    }

    public void setContent(BigInteger content) {
        if (content != null) setContent(content.toString());
        else setContent("");
    }
    public void setContent(CharSequence content) {
        mTitle.setText(tv_title);
        if (tv_hideIfContentEmpty && TextUtils.isEmpty(content)) {
            tv_title = null;
            mView.setVisibility(GONE);
        } else {
            tv_content = content.toString();
            mView.setVisibility(VISIBLE);
            mTitle.setText(tv_title);
            mContent.setText(tv_content);
            if (TextUtils.isEmpty(tv_title)) {
                mTitle.setVisibility(GONE);
            } else {
                mTitle.setVisibility(VISIBLE);
            }
            if (TextUtils.isEmpty(tv_content)) {
                mContent.setVisibility(GONE);
            } else {
                mContent.setVisibility(VISIBLE);
            }
        }
    }
}
