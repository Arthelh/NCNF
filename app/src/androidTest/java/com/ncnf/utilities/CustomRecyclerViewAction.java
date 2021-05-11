package com.ncnf.utilities;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

public class CustomRecyclerViewAction {

    public static ViewAction longClickOnButtonInRecyclerViewItem(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a button inside a RecyclerView item";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performLongClick();
            }
        };
    }

}
