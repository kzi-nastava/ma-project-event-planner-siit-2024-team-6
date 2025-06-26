package com.example.eventure.utils;

import android.view.Menu;

import com.example.eventure.R;
import com.example.eventure.clients.ClientUtils;
import com.google.android.material.navigation.NavigationView;

public class MenuUtils {
    public static void filterMenuByRole(NavigationView navView, String role) {
        Menu menu = navView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(false);
        }
        if(role == null || !ClientUtils.getAuthService().isLoggedIn() || ClientUtils.getAuthService().hasTokenExpired())
            return;

        if ("ROLE_ADMIN".equalsIgnoreCase(role)) {
            menu.findItem(R.id.nav_admin_categories).setVisible(true);
            menu.findItem(R.id.nav_admin_manage_comments).setVisible(true);
            menu.findItem(R.id.nav_admin_manage_reports).setVisible(true);
            menu.findItem(R.id.nav_event_types).setVisible(true);
        }

        if ("ROLE_PROVIDER".equalsIgnoreCase(role)) {
            menu.findItem(R.id.nav_my_offers).setVisible(true);
            menu.findItem(R.id.nav_price_list).setVisible(true);
            menu.findItem(R.id.nav_my_products).setVisible(true);
        }
        if("ROLE_ORGANIZER".equalsIgnoreCase(role)){
            menu.findItem(R.id.nav_my_events).setVisible(true);
        }

            menu.findItem(R.id.nav_notifications).setVisible(true);
            menu.findItem(R.id.nav_messages).setVisible(true);
            menu.findItem(R.id.nav_favorite_events).setVisible(true);
            menu.findItem(R.id.nav_favorite_products).setVisible(true);
            menu.findItem(R.id.nav_favorite_services).setVisible(true);
            menu.findItem(R.id.nav_my_calendar).setVisible(true);

    }
}
