package com.ivvlev.car.framework;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class PageableListView<I> extends LinearLayout {

    private static final boolean DEBUG = true;
    protected final String LOG_TAG = getClass().getCanonicalName();

    private EventListener<I> mEventListener;
    private ItemListAdapter mArrayAdapter;
    private TablePagerAdapter<I> mPagerAdapter;
    private ViewPager mViewPager;
    private List<I> mItemList;
    private int mSelectedItemIndex = -1;

    public PageableListView(@NonNull Context context) {
        super(context);
    }

    public PageableListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.pageable_list_view_layout, this);
        mViewPager = findViewById(R.id.list_view_pager);
    }

    public void setEventListener(EventListener<I> eventListener) {
        mEventListener = eventListener;
    }

    public void setListSource(List<I> list) {
        if (mItemList == list) {
            mArrayAdapter.notifyDataSetChanged();
        } else {
            mItemList = list;
            mArrayAdapter = new ItemListAdapter(getContext(), R.layout.pageable_list_view_button_layout, mItemList);
            mPagerAdapter = new TablePagerAdapter<I>(mArrayAdapter);
            mViewPager.setAdapter(mPagerAdapter);
            setSelectedItemByIndex(mSelectedItemIndex);
        }
    }

    public void setCurrentPage(int pageIndex, boolean smoothScroll) {
        mViewPager.setCurrentItem(pageIndex, smoothScroll);
    }

    public int getCurrentPage() {
        return mViewPager.getCurrentItem();
    }

    public int getSelectedItemIndex() {
        return mSelectedItemIndex;
    }

    protected void setSelectedItemIndex(int selectedItemIndex) {
        mSelectedItemIndex = selectedItemIndex;
    }

    public void setSelectedItemByIndex(int index) {
        setSelectedItemIndex(-1);
        if (0 <= index && index < mItemList.size()) {
            int pageIndex = mPagerAdapter.getPageIndexByItemIndex(index);
            setCurrentPage(pageIndex, true);
            setSelectedItemIndex(index);
            setSelectedItem(mItemList.get(index));
        } else {
            deselectItemViews();
        }
    }

    public void setSelectedItem(I item) {
        deselectItemViews();
        selectItemView(mViewPager, item);
    }

    private void deselectItemViews() {
        deselectItemViews(mViewPager);
    }

    private boolean selectItemView(ViewGroup rootView, I item) {
        for (int index = 0; index < rootView.getChildCount(); index++) {
            View view = rootView.getChildAt(index);
            if (view instanceof RelativeLayout && view.getTag() != null && ((ItemHolder<I>) view.getTag()).item == item) {
                makeItemViewSelected(view);
                return true;
            } else if (view instanceof ViewGroup) {
                if (selectItemView((ViewGroup) view, item))
                    return true;
            }
        }
        return false;
    }

    private void deselectItemViews(ViewGroup rootView) {
        for (int index = 0; index < rootView.getChildCount(); index++) {
            View view = rootView.getChildAt(index);
            if (view instanceof RelativeLayout) {
                makeItemViewUnselected(view);
            } else if (view instanceof ViewGroup) {
                deselectItemViews((ViewGroup) view);
            }
        }
    }

    private void makeItemViewSelected(View view) {
        view.getBackground().setLevel(1);
    }

    private void makeItemViewUnselected(View view) {
        view.getBackground().setLevel(0);
    }

    private class ItemListAdapter extends BindableArrayAdapter<I> {

        public ItemListAdapter(@NonNull Context context, int resource, @NonNull List<I> objects) {
            super(context, resource, objects);
        }

        final View.OnClickListener buttonClickListener = view -> {
            if (DEBUG) Log.d(LOG_TAG, "onClick");
            ItemHolder<I> itemHolder = (ItemHolder<I>) view.getTag();
            if (itemHolder != null && mEventListener != null) {
                setSelectedItemIndex(itemHolder.index);
                mEventListener.onClick(itemHolder.item, itemHolder.index);
            }
        };

        final View.OnLongClickListener buttonLongClickListener = view -> {
            if (DEBUG) Log.d(LOG_TAG, "onLongClick");
            ItemHolder<I> itemHolder = (ItemHolder<I>) view.getTag();
            if (itemHolder != null && mEventListener != null) {
                return mEventListener.onLongClick(itemHolder.item, itemHolder.index);
            }
            return false;
        };

        @Override
        protected void bind(View view, I item, int index) {
            if (item != null) {
                String caption = mEventListener != null ? mEventListener.onFormatItem(item, index) : item.toString();
                ((TextView) view.findViewById(R.id.text_index)).setText(String.valueOf(index + 1));
                ((TextView) view.findViewById(R.id.text_freq)).setText(caption);
                view.setTag(new ItemHolder<I>(index, item));
                view.setOnClickListener(buttonClickListener);
                view.setOnLongClickListener(buttonLongClickListener);
                if (index == mSelectedItemIndex)
                    makeItemViewSelected(view);
            } else {
                ((TextView) view.findViewById(R.id.text_index)).setText(String.valueOf(index + 1));
                ((TextView) view.findViewById(R.id.text_freq)).setText("");
            }
        }
    }

    public static class ItemHolder<I> {
        public final int index;
        public final I item;

        public ItemHolder(int index, I item) {
            this.index = index;
            this.item = item;
        }
    }

    public static interface EventListener<I> {
        void onClick(I item, int index);

        boolean onLongClick(I item, int index);

        String onFormatItem(I item, int index);
    }

}
