package com.ivvlev.car.framework;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BindableArrayAdapter<T> extends ArrayAdapter<T> {

    private final LayoutInflater mInflater;
    private final int mResource;
    private int mRowCount = 2;
    private int mColumnCount = 3;


    public BindableArrayAdapter(@NonNull Context context, int resource) {
        this(context, resource, new ArrayList<T>());
    }

    public BindableArrayAdapter(@NonNull Context context, int resource, @NonNull T[] objects) {
        this(context, resource, Arrays.asList(objects));
    }

    public BindableArrayAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
        mInflater = LayoutInflater.from(context);
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createViewFromResource(mInflater, position, convertView, parent, mResource);
    }

    @NonNull
    private View createViewFromResource(@NonNull LayoutInflater inflater, int index,
                                        @Nullable View convertView, @NonNull ViewGroup parent, int resource) {
        final View view;

        if (convertView == null) {
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        final T item = (index >= 0 && index < getCount()) ? getItem(index) : null;
        bind(view, item, index);
        return view;
    }

    protected abstract void bind(View view, T item, int index);

    public void setRowCount(int mRowCount) {
        this.mRowCount = mRowCount;
    }

    public void setColumnCount(int mColumnCount) {
        this.mColumnCount = mColumnCount;
    }

    public int getRowCount() {
        return mRowCount;
    }

    public int getColumnCount() {
        return mColumnCount;
    }

}
