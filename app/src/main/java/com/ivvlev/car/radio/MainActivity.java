package com.ivvlev.car.radio;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ivvlev.car.framework.ActionLayout;
import com.ivvlev.car.framework.PagerFragment;
import com.ivvlev.car.radio.mst768.RadioFragment;
import com.ivvlev.car.radio.web.WebRadioFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ServiceOwnerActivity implements ActionLayout {

    protected final String LOG_TAG = getClass().getCanonicalName();
    protected static final boolean DEBUG = true;
    private TextView mTextViewClocks;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ViewGroup mLeftToolView;
    private ViewGroup mRightToolView;
    private Timer mTimer;
    private MyTimerTask mMyTimerTask;

    class MyTimerTask extends TimerTask {

        //            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
//                    "dd:MMMM:yyyy HH:mm:ss a", Locale.getDefault());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "HH:mm", Locale.getDefault());

        @Override
        public void run() {
            final Date date = Calendar.getInstance().getTime();
            final String sTime = simpleDateFormat.format(date);
//            if (DEBUG)
//                Log.d(LOG_TAG, "onClockTimer(" + date + ")");
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mTextViewClocks != null) mTextViewClocks.setText(sTime);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (DEBUG)
            Log.d(LOG_TAG, "onCreate(" + (savedInstanceState != null ? "savedInstanceState)" : "null)"));

//        if (savedInstanceState == null) {
//            SharedPreferences save = getSharedPreferences("com.ivvlev.car.radio.Application", MODE_PRIVATE);
//            savedInstanceState = BundleHelper.loadPreferencesBundle(save, "MainActivity");
//            if (DEBUG)
//                Log.d(LOG_TAG, "onCreate.loadSharedPreferences(" + (savedInstanceState != null ? "savedInstanceState)" : "null)"));
//        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mLeftToolView = findViewById(R.id.additional_left);
        mRightToolView = findViewById(R.id.additional_right);
        mTextViewClocks = findViewById(R.id.display_clocks);

        TabLayout tabLayout = findViewById(R.id.tabs);

        TabLayout.TabLayoutOnPageChangeListener listener = new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
            private int mPriorPosition = -1;

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (mPriorPosition >= 0) {
                    Fragment unselectedFragment = ((FragmentPagerAdapter) mViewPager.getAdapter()).getItem(mPriorPosition);
                    if (unselectedFragment instanceof PagerFragment)
                        ((PagerFragment) unselectedFragment).onPageDeactivated(MainActivity.this);
                }
                Fragment selectedFragment = ((FragmentPagerAdapter) mViewPager.getAdapter()).getItem(position);
                if (selectedFragment instanceof PagerFragment) {
                    ((PagerFragment) selectedFragment).onPageActivated(MainActivity.this);
                }
                mPriorPosition = position;
            }
        };
        mViewPager.addOnPageChangeListener(listener);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        // do this in a runnable to make sure the viewPager's views are already instantiated before triggering the onPageSelected call
        mViewPager.post(() -> listener.onPageSelected(0));
//        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (DEBUG)
            Log.d(LOG_TAG, "onSaveInstanceState(" + (outState != null ? "bundle)" : "null)"));
        super.onSaveInstanceState(outState);
//        if (outState != null) {
//            SharedPreferences save = getSharedPreferences("com.ivvlev.car.radio.Application", MODE_PRIVATE);
//            SharedPreferences.Editor editor = save.edit();
//            try {
//                BundleHelper.savePreferencesBundle(editor, "MainActivity", outState);
//            } finally {
//                //editor.apply();
//                editor.commit();
//            }
//        }
    }

    @Override
    protected void onPause() {
        mTimer.cancel();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();
        mTimer.schedule(mMyTimerTask, 1000, 1000);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (DEBUG)
            Log.d(LOG_TAG, "onRestoreInstanceState(" + (savedInstanceState != null ? "savedInstanceState)" : "null)"));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackBtnClicked(View view) {
        finish();
    }

    @Override
    public void addActionToLeft(String caption, View.OnClickListener handler) {
        Button view = new Button(this, null, R.attr.buttonBarButtonStyle);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                0.0F));
        view.setText(caption);
        view.setOnClickListener(handler);
        mLeftToolView.addView(view);
    }

    @Override
    public void addActionToRight(String caption, View.OnClickListener handler) {
        Button view = new Button(this, null, android.R.attr.buttonBarButtonStyle);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                0.0F));
        view.setText(caption);
        view.setOnClickListener(handler);
        mRightToolView.addView(view);
    }

    @Override
    public void clearLeft() {
        mLeftToolView.removeAllViews();
    }

    @Override
    public void clearRight() {
        mRightToolView.removeAllViews();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            PagerFragment pagerFragment = findPagerFragment(position);
            if (pagerFragment == null) {
                switch (position) {
                    case 0:
                        pagerFragment = RadioFragment.newInstance();
                        break;
                    default:
                        pagerFragment = WebRadioFragment.newInstance();
                        break;
                }
                pagerFragment.setPageIndex(position);
            }
            return pagerFragment;
        }

        private PagerFragment findPagerFragment(int pageIndex) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof PagerFragment) {
                    if (((PagerFragment) fragment).getPageIndex() == pageIndex) {
                        return (PagerFragment) fragment;
                    }
                }
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.tab_air_text);
                default:
                    return getResources().getString(R.string.tab_web_text);
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }


}
