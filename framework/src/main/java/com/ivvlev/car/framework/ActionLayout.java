package com.ivvlev.car.framework;

import android.view.View;

public interface ActionLayout {

    void addActionToLeft(String caption, View.OnClickListener handler);

    void addActionToRight(String caption, View.OnClickListener handler);

    void clearLeft();

    void clearRight();

}
