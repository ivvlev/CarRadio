package com.ivvlev.car.framework;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
//
//public class ToggleColorButton extends CompoundButtonCompat {
//}

/**
 * Displays checked/unchecked states as a button
 * with a "light" indicator and by default accompanied with the text "ON" or "OFF".
 *
 * <p>See the <a href="{@docRoot}guide/topics/ui/controls/togglebutton.html">Toggle Buttons</a>
 * guide.</p>
 *
 * @attr ref android.R.styleable#ToggleButton_textOn
 * @attr ref android.R.styleable#ToggleButton_textOff
 * @attr ref android.R.styleable#ToggleButton_disabledAlpha
 */
public class ToggleColorButton extends CompoundButton {
    private CharSequence mTextOn;
    private CharSequence mTextOff;
    private ColorStateList mTextColorOn;
    private ColorStateList mTextColorOff;
    private Drawable mBackgroundOn;
    private Drawable mBackgroundOff;

    public ToggleColorButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ToggleView, defStyleAttr, defStyleRes);
        mTextOn = a.getText(R.styleable.ToggleView_textOn);
        mTextOff = a.getText(R.styleable.ToggleView_textOff);
        mTextColorOn = ColorStateList.valueOf(a.getColor(R.styleable.ToggleView_textColorOn, 0xFF0000));
        mTextColorOff = ColorStateList.valueOf(a.getColor(R.styleable.ToggleView_textColorOff, 0xFFFFFF));
        mBackgroundOn = a.getDrawable(R.styleable.ToggleView_backgroundOn);
        mBackgroundOff = a.getDrawable(R.styleable.ToggleView_backgroundOff);
        syncTextState();
        a.recycle();
    }

    public ToggleColorButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ToggleColorButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleColorButton(Context context) {
        this(context, null);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);

        syncTextState();
    }

    private void syncTextState() {
//        boolean checked = isChecked();
//        if (checked && mTextOn != null) {
//            setText(mTextOn);
//        } else if (!checked && mTextOff != null) {
//            setText(mTextOff);
//        }
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

    /**
     * Returns the text for when the button is in the checked state.
     *
     * @return The text.
     */
    public CharSequence getTextOn() {
        return mTextOn;
    }

    /**
     * Sets the text for when the button is in the checked state.
     *
     * @param textOn The text.
     */
    public void setTextOn(CharSequence textOn) {
        mTextOn = textOn;
    }

    /**
     * Returns the text for when the button is not in the checked state.
     *
     * @return The text.
     */
    public CharSequence getTextOff() {
        return mTextOff;
    }

    /**
     * Sets the text for when the button is not in the checked state.
     *
     * @param textOff The text.
     */
    public void setTextOff(CharSequence textOff) {
        mTextOff = textOff;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return ToggleButton.class.getName();
    }
}
