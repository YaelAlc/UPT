package com.example.upt;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ImageButton;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MenuHelper {

    public static void configurarMenu(Activity activity,
                                      DrawerLayout drawerLayout,
                                      NavigationView navigationView,
                                      ImageButton btnMenu,
                                      int idUsuario,
                                      int idTipoUsuario) {

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        Menu menu = navigationView.getMenu();

        menu.findItem(R.id.nav_primera_interfaz_reporte).setVisible(false);
        menu.findItem(R.id.nav_historial_reportes).setVisible(false);
        menu.findItem(R.id.nav_reportes_admin).setVisible(false);
        menu.findItem(R.id.nav_historial_admin).setVisible(false);


        if (idTipoUsuario == 1 || idTipoUsuario == 2) {
            menu.findItem(R.id.nav_primera_interfaz_reporte).setVisible(true);
            menu.findItem(R.id.nav_historial_reportes).setVisible(true);

        } else if (idTipoUsuario == 3) {
            menu.findItem(R.id.nav_reportes_admin).setVisible(true);
            menu.findItem(R.id.nav_historial_admin).setVisible(true);

        } else {
            menu.findItem(R.id.nav_primera_interfaz_reporte).setVisible(true);
            menu.findItem(R.id.nav_historial_reportes).setVisible(true);

        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_primera_interfaz_reporte) {
                if (!(activity instanceof Primera_interfaz_reporte)) {
                    Intent intent = new Intent(activity, Primera_interfaz_reporte.class);
                    intent.putExtra("id_usuario", idUsuario);
                    intent.putExtra("id_tipo_usuario", idTipoUsuario);
                    activity.startActivity(intent);
                }
            } else if (id == R.id.nav_historial_reportes) {
                if (!(activity instanceof historial_reportes)) {
                    Intent intent = new Intent(activity, historial_reportes.class);
                    intent.putExtra("id_usuario", idUsuario);
                    intent.putExtra("id_tipo_usuario", idTipoUsuario);
                    activity.startActivity(intent);
                }
            } else if (id == R.id.nav_reportes_admin) {
                if (!(activity instanceof reportes_admin)) {
                    Intent intent = new Intent(activity, reportes_admin.class);
                    intent.putExtra("id_usuario", idUsuario);
                    intent.putExtra("id_tipo_usuario", idTipoUsuario);
                    activity.startActivity(intent);
                }
            } else if (id == R.id.nav_historial_admin) {
                if (!(activity instanceof historial_admin)) {
                    Intent intent = new Intent(activity, historial_admin.class);
                    intent.putExtra("id_usuario", idUsuario);
                    intent.putExtra("id_tipo_usuario", idTipoUsuario);
                    activity.startActivity(intent);
                }
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}