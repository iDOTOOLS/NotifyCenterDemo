package com.idotools.notifycenterdemo.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by LvWind on 15/11/12.
 * ViewPager 多点触控时有下标越界 bug
 * https://github.com/chrisbanes/PhotoView/issues/31#issuecomment-19803926
 */
public class ViewPagerFixed extends ViewPager {

    public ViewPagerFixed(Context context) {
        super(context);
    }
    public ViewPagerFixed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
