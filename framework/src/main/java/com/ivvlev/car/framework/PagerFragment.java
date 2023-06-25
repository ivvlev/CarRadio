package com.ivvlev.car.framework;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

/**
 * Наследники данного класса являются страницами PageView.
 */
public abstract class PagerFragment extends Fragment {

    protected final String LOG_TAG = getClass().getCanonicalName();
    private static final boolean DEBUG = true;
    private int mPageIndex = -1;
    private Bundle mBundle;

    public int getPageIndex() {
        return mPageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.mPageIndex = pageIndex;
    }

    protected String getBundleKey() {
        return getClass().getCanonicalName();
    }

    @Override
    public void onDestroyView() {
        saveState();
        if (DEBUG) Log.d(LOG_TAG, "onDestroyView");
        super.onDestroyView();
    }

    /**
     * Вызывается при активации фрагмета в PageViewer
     *
     * @param context
     */
    public void onPageActivated(Context context) {
        if (DEBUG) Log.d(LOG_TAG, "onPageActivated");
    }

    /**
     * Вызывается при деактивации фрагмета в PageViewer
     *
     * @param context
     */
    public void onPageDeactivated(Context context) {
        if (DEBUG) Log.d(LOG_TAG, "onPageDeactivated");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (DEBUG)
            Log.d(LOG_TAG, "onViewStateRestored(" + (savedInstanceState != null ? "savedInstanceState)" : "null)"));
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(getBundleKey())) {
            restoreState(savedInstanceState.getBundle(getBundleKey()));
        } else {
            restoreState();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (DEBUG)
            Log.d(LOG_TAG, "onViewStateRestored(" + (outState != null ? "outState)" : "null)"));
        outState.putBundle(getBundleKey(), saveState());
        super.onSaveInstanceState(outState);
    }

    final protected Bundle saveState() {
        if (mBundle == null)
            mBundle = new Bundle();
        onSaveState(mBundle);
        return mBundle;
    }

    final protected void restoreState() {
        if (mBundle != null) {
            onRestoreState(mBundle);
        }
    }

    private void restoreState(Bundle bundle) {
        mBundle = bundle;
        restoreState();
    }

    protected void onSaveState(Bundle bundle) {
        if (DEBUG) Log.d(LOG_TAG, "onSaveState(bundle)");
        bundle.putInt("PageIndex", mPageIndex);
    }

    protected void onRestoreState(Bundle bundle) {
        if (DEBUG) Log.d(LOG_TAG, "onRestoreState(bundle)");
        mPageIndex = bundle.getInt("PageIndex");
    }

}
