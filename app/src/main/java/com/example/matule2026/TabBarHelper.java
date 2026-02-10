package com.example.matule2026;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

public class TabBarHelper {

    public static void setupTabbar(Activity activity, View tabBarView) {
        if (tabBarView == null) return;

        setupButton(activity, tabBarView, com.example.ui_kit.R.id.button_glavn, glavn.class);
        setupButton(activity, tabBarView, com.example.ui_kit.R.id.button_catalog, catalog.class);
        setupButton(activity, tabBarView, com.example.ui_kit.R.id.button_project, project.class);
        setupButton(activity, tabBarView, com.example.ui_kit.R.id.button_profile, profile.class);
    }

    public static void setupTabbarFromActivity(Activity activity) {
        setupButtonDirect(activity, com.example.ui_kit.R.id.button_glavn, glavn.class);
        setupButtonDirect(activity, com.example.ui_kit.R.id.button_catalog, catalog.class);
        setupButtonDirect(activity, com.example.ui_kit.R.id.button_project, project.class);
        setupButtonDirect(activity, com.example.ui_kit.R.id.button_profile, profile.class);
    }

    private static void setupButton(Activity activity, View rootView, int buttonId, Class<?> destinationClass) {
        ImageView button = rootView.findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(v -> navigateTo(activity, destinationClass));
        }
    }

    private static void setupButtonDirect(Activity activity, int buttonId, Class<?> destinationClass) {
        ImageView button = activity.findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(v -> navigateTo(activity, destinationClass));
        }
    }

    private static void navigateTo(Activity activity, Class<?> destinationClass) {
        String currentActivity = activity.getClass().getSimpleName().toLowerCase();
        String destActivity = destinationClass.getSimpleName().toLowerCase();

        if (currentActivity.contains(destActivity)) {
            return;
        }

        activity.startActivity(new Intent(activity, destinationClass));

        if (!destActivity.contains("profile")) {
            activity.finish();
        }
    }
}