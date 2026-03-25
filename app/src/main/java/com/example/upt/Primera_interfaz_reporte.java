package com.example.upt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class Primera_interfaz_reporte extends AppCompatActivity {
    EditText edtmDescripcion;
    Button btnSiguiente;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton btnMenu;

    int idUsuario, idTipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primera_interfaz_reporte);
        edtmDescripcion = findViewById(R.id.edtmDescripcion);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);

        idUsuario = getIntent().getIntExtra("id_usuario", -1);
        idTipoUsuario = getIntent().getIntExtra("id_tipo_usuario", -1);


        MenuHelper.configurarMenu(this, drawerLayout, navigationView, btnMenu, idUsuario, idTipoUsuario);

        btnSiguiente.setOnClickListener(v -> {
            String descripcion = edtmDescripcion.getText().toString().trim();

            if (descripcion.isEmpty()) {
                Toast.makeText(this, "Escribe una descripción", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, segunda_interfaz_reporte.class);
            intent.putExtra("descripcion", descripcion);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_tipo_usuario", idTipoUsuario);
            startActivity(intent);
        });
    }
}