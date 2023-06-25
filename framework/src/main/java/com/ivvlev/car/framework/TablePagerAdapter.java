package com.ivvlev.car.framework;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

public class TablePagerAdapter<T> extends PagerAdapter {

//    private static final String LOG_TAG = "RadioStationPagerAdap..";
//    private static final boolean DEBUG = true;

    private final BindableArrayAdapter<T> mAdapter;
    private final int mTableResource;
    private final int mRowResource;
//    private Map<String, View> mViewMap = new HashMap<>();
//    private View mCurrentPrimaryItem = null;


    public TablePagerAdapter(BindableArrayAdapter<T> adapter) {
        this(adapter, R.layout.table_page_layout, R.layout.table_page_row_layout);
    }

    public TablePagerAdapter(BindableArrayAdapter<T> adapter, int tableResource, int rowResource) {
        mAdapter = adapter;
        mTableResource = tableResource;
        mRowResource = rowResource;
    }

    /**
     * Return the RadioStationPage associated with a specified position.
     */
    @Override
    public int getCount() {
        if (mAdapter == null) return 0;
        int itemCount = (mAdapter.getCount() == 0) ? mAdapter.getRowCount() * mAdapter.getColumnCount() : mAdapter.getCount();
        return ((Double) Math.ceil((double) itemCount / (mAdapter.getRowCount() * mAdapter.getColumnCount()))).intValue();
    }

    public int getPageIndexByItem(T item) {
        //return getPageIndexByItemIndex(mAdapter.getItem(item));
        return -1;
    }

    public int getPageIndexByItemIndex(int itemIndex) {
        return ((Double) Math.ceil((double) (itemIndex + 1) / (mAdapter.getRowCount() * mAdapter.getColumnCount()))).intValue() - 1;
    }

//    @Override
//    public void startUpdate(ViewGroup container) {
////        if (container.getId() == View.NO_ID) {
////            throw new IllegalStateException("ViewPager with adapter " + this
////                    + " requires a view id");
////        }
//    }

    private LayoutInflater mInflater;

    @NonNull
    @SuppressWarnings("ReferenceEquality")
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int pageIndex) {

        //final long itemId = getItemId(pageIndex);
        // Do we already have this fragment?
        //String name = makeFragmentName(container.getId(), itemId);
        // Заплатка, позволяет программно переключать ViewPager на произвольную страницу.
        if (pageIndex > 0 && pageIndex > container.getChildCount()) {
            instantiateItem(container, pageIndex - 1);
        }

        LayoutInflater inflater = mInflater != null ? mInflater : (mInflater = LayoutInflater.from(container.getContext()));

        TableLayout page = (TableLayout) inflater.inflate(mTableResource, container, false);
        //page.setBackgroundColor(Color.rgb(205, 205, 205));
        final int rowCount = mAdapter.getRowCount();
        final int columnCount = mAdapter.getColumnCount();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            TableRow row = (TableRow) inflater.inflate(mRowResource, page, false);
            for (int i = 0; i < columnCount; i++) {
                int itemIndex = (pageIndex * columnCount * rowCount) + (rowIndex * columnCount) + i;
                // Если индекс больше чем кол-во элементов, адаптер должен вернуть пустой элемент.
                row.addView(mAdapter.getView(itemIndex, null, row));
            }
            page.addView(row);
        }
        container.addView(page, pageIndex);
        return page;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((TableLayout) object).removeAllViews();
        container.removeView((View) object);
    }

//    @SuppressWarnings("ReferenceEquality")
//    @Override
//    public void setPrimaryItem(ViewGroup container, int position, Object object) {
//        TableLayout fragment = (TableLayout) object;
//        if (fragment != mCurrentPrimaryItem) {
//            if (mCurrentPrimaryItem != null) {
//            }
//            if (fragment != null) {
//            }
//            mCurrentPrimaryItem = fragment;
//        }
//    }
//
//    @Override
//    public void finishUpdate(ViewGroup container) {
//    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }

//    @Override
//    public Parcelable saveState() {
//        return null;
//    }
//
//    @Override
//    public void restoreState(Parcelable state, ClassLoader loader) {
//    }

//    /**
//     * Return a unique identifier for the item at the given position.
//     *
//     * <p>The default implementation returns the given position.
//     * Subclasses should override this method if the positions of items can change.</p>
//     *
//     * @param position Position within this adapter
//     * @return Unique identifier for the item at position
//     */
//    public long getItemId(int position) {
//        return position;
//    }
//
//    private static String makeFragmentName(int viewId, long id) {
//        return "android:switcher:" + viewId + ":" + id;
//    }
}
