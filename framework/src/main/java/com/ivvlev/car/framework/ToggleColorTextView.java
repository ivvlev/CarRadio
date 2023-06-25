package com.ivvlev.car.framework;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Checkable;

public class ToggleColorTextView extends android.support.v7.widget.AppCompatTextView implements Checkable {

    private CharSequence mTextOn;
    private CharSequence mTextOff;
    private ColorStateList mTextColorOn;
    private ColorStateList mTextColorOff;
    private Drawable mBackgroundOn;
    private Drawable mBackgroundOff;
    private boolean mChecked = false;

    public ToggleColorTextView(Context context) {
        this(context, (AttributeSet)null);
    }

    public ToggleColorTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842884);
    }

    public ToggleColorTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ToggleView, defStyleAttr, 0);
        mTextOn = a.getText(R.styleable.ToggleView_textOn);
        mTextOff = a.getText(R.styleable.ToggleView_textOff);
        mTextColorOn = ColorStateList.valueOf(a.getColor(R.styleable.ToggleView_textColorOn, 0xFF0000));
        mTextColorOff = ColorStateList.valueOf(a.getColor(R.styleable.ToggleView_textColorOff, 0xFFFFFF));
        mBackgroundOn = a.getDrawable(R.styleable.ToggleView_backgroundOn);
        mBackgroundOff = a.getDrawable(R.styleable.ToggleView_backgroundOff);
        a.recycle();
        syncVisualState();
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }


    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            onCheckedChanged();
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    protected void onCheckedChanged() {
        syncVisualState();
    }


    protected void syncVisualState() {
        if (isChecked()) {
            setBackground(mBackgroundOn != null ? mBackgroundOn : getBackground());
            setText(mTextOn != null ? mTextOn : getText());
            setTextColor(mTextColorOn != null ? mTextColorOn : getTextColors());
        } else {
            setBackground(mBackgroundOff != null ? mBackgroundOff : getBackground());
            setText(mTextOff != null ? mTextOff : getText());
            setTextColor(mTextColorOff != null ? mTextColorOff : getTextColors());
        }
    }

    public ColorStateList getTextColorOn() {
        return mTextColorOn;
    }

    public void setTextColorOn(ColorStateList textColorOn) {
        mTextColorOn = textColorOn;
    }

    public ColorStateList getTextColorOff() {
        return mTextColorOff;
    }

    public void setTextColorOff(ColorStateList textColorOff) {
        mTextColorOff = textColorOff;
    }

    public CharSequence getTextOn() {
        return mTextOn;
    }

    public void setTextOn(CharSequence textOn) {
        mTextOn = textOn;
    }

    public CharSequence getTextOff() {
        return mTextOff;
    }

    public void setTextOff(CharSequence textOff) {
        mTextOff = textOff;
    }

    public Drawable getBackgroundOn() {
        return mBackgroundOn;
    }

    public void setBackgroundOn(Drawable backgroundOn) {
        mBackgroundOn = backgroundOn;
    }

    public Drawable getBackgroundOff() {
        return mBackgroundOff;
    }

    public void setBackgroundOff(Drawable backgroundOff) {
        mBackgroundOff = backgroundOff;
    }
}
