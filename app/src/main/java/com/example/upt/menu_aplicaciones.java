package com.example.upt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class menu_aplicaciones extends AppCompatActivity {
    LinearLayout itemReportes;
    TextView tvWelcomeName;

    int idUsuario, idTipoUsuario;
    String nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_aplicaciones);

        itemReportes = findViewById(R.id.itemReportes);
        tvWelcomeName = findViewById(R.id.tvWelcomeName);


        idUsuario = getIntent().getIntExtra("id_usuario", -1);
        idTipoUsuario = getIntent().getIntExtra("id_tipo_usuario", -1);
        nombreUsuario = getIntent().getStringExtra("nombre");


        tvWelcomeName.setText(nombreUsuario);
        itemReportes.setOnClickListener(v -> {

            if (idTipoUsuario == 1 || idTipoUsuario == 2) {

                Intent intent = new Intent(this, historial_reportes.class);
                intent.putExtra("id_usuario", idUsuario);
                intent.putExtra("id_tipo_usuario", idTipoUsuario);
                startActivity(intent);

            } else if (idTipoUsuario == 3) {

                Intent intent = new Intent(this, reportes_admin.class);
                intent.putExtra("id_usuario", idUsuario);
                intent.putExtra("id_tipo_usuario", idTipoUsuario);
                startActivity(intent);

            } else {
                Toast.makeText(this, "Tipo de usuario no válido", Toast.LENGTH_SHORT).show();
            }
        });

    }
}