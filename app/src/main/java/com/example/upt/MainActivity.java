package com.example.upt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextInputEditText edtMatricula, edtPassword;
    Button btnLogin, btnRegistro;

    String URL = "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/usuarios";
    String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB5eHVhcGJwaWN5bmZ2b3l0Z2drIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQzMTA1NjYsImV4cCI6MjA4OTg4NjU2Nn0.dNkk7jmUxxDq88wr7rAKQCekgSIAaP0KjZI8g0TaUKc";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtMatricula = findViewById(R.id.edtMatricula);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnRegistro);

        btnLogin.setOnClickListener(v -> login());

        btnRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, registro_usuario.class));
        });
    }

    private void login() {

        String matricula = edtMatricula.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();


        if (matricula.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }


        String urlFinal = URL +
                "?matricula=eq." + matricula +
                "&contrasenia=eq." + password + "&select=id,nombre,id_tipo_usuario";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, urlFinal,
                response -> {

                    try {
                        JSONArray array = new JSONArray(response);

                        if (array.length() > 0) {

                            JSONObject user = array.getJSONObject(0);

                            int idUsuario = user.getInt("id");
                            int tipoUsuario = user.getInt("id_tipo_usuario");
                            String nombreUsuario = user.getString("nombre");


                            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();

                            // 🔥 ENVÍA AL MENÚ
                            Intent intent = new Intent(this, menu_aplicaciones.class);
                            intent.putExtra("id_usuario", idUsuario);
                            intent.putExtra("id_tipo_usuario", tipoUsuario);
                            intent.putExtra("nombre", nombreUsuario);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar", Toast.LENGTH_SHORT).show();
                    }

                },
                error -> {
                    error.printStackTrace();

                    if (error.networkResponse != null) {
                        String body = new String(error.networkResponse.data);
                        System.out.println("ERROR: " + body);
                    }

                    Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> headers = new HashMap<>();

                headers.put("apikey", API_KEY);
                headers.put("Authorization", "Bearer " + API_KEY);

                return headers;
            }
        };

        queue.add(request);
    }
}