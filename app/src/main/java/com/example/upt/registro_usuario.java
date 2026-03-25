package com.example.upt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class registro_usuario extends AppCompatActivity {

    Spinner spinnerCarrera, spinnerGrupo;

    ArrayList<String> listaCarreras = new ArrayList<>();
    ArrayList<Integer> listaIdCarreras = new ArrayList<>();

    ArrayList<String> listaGrupos = new ArrayList<>();
    ArrayList<Integer> listaIdGrupos = new ArrayList<>();

    String URL_BASE = "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        spinnerCarrera = findViewById(R.id.spinnerCarrera);
        spinnerGrupo = findViewById(R.id.spinnerGrupo);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(v -> registrarUsuario());

        cargarCarreras();

        spinnerCarrera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (listaIdCarreras.size() > 0) {
                    int idCarrera = listaIdCarreras.get(position);
                    cargarGrupos(idCarrera);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void cargarCarreras() {

        String url = URL_BASE + "carreras?select=*";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {

                    try {
                        JSONArray array = new JSONArray(response);

                        listaCarreras.clear();
                        listaIdCarreras.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            listaCarreras.add(obj.getString("nombre"));
                            listaIdCarreras.add(obj.getInt("id"));
                        }

                        if (listaCarreras.isEmpty()) {
                            Toast.makeText(this, "No hay carreras", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                listaCarreras
                        );

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCarrera.setAdapter(adapter);


                        cargarGrupos(listaIdCarreras.get(0));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parseando carreras", Toast.LENGTH_SHORT).show();
                    }

                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error conexión carreras", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> headers = new HashMap<>();

                headers.put("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB5eHVhcGJwaWN5bmZ2b3l0Z2drIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQzMTA1NjYsImV4cCI6MjA4OTg4NjU2Nn0.dNkk7jmUxxDq88wr7rAKQCekgSIAaP0KjZI8g0TaUKc");
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB5eHVhcGJwaWN5bmZ2b3l0Z2drIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQzMTA1NjYsImV4cCI6MjA4OTg4NjU2Nn0.dNkk7jmUxxDq88wr7rAKQCekgSIAaP0KjZI8g0TaUKc");

                return headers;
            }
        };

        queue.add(request);
    }


    private void cargarGrupos(int idCarrera) {

        String url = URL_BASE + "grupos?id_carrera=eq." + idCarrera + "&select=*";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {

                    try {
                        JSONArray array = new JSONArray(response);

                        listaGrupos.clear();
                        listaIdGrupos.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            listaGrupos.add(obj.getString("nombre"));
                            listaIdGrupos.add(obj.getInt("id"));
                        }

                        if (listaGrupos.isEmpty()) {
                            listaGrupos.add("Sin grupos");
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                listaGrupos
                        );

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerGrupo.setAdapter(adapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parseando grupos", Toast.LENGTH_SHORT).show();
                    }

                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error conexión grupos", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> headers = new HashMap<>();

                headers.put("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB5eHVhcGJwaWN5bmZ2b3l0Z2drIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQzMTA1NjYsImV4cCI6MjA4OTg4NjU2Nn0.dNkk7jmUxxDq88wr7rAKQCekgSIAaP0KjZI8g0TaUKc");
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB5eHVhcGJwaWN5bmZ2b3l0Z2drIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQzMTA1NjYsImV4cCI6MjA4OTg4NjU2Nn0.dNkk7jmUxxDq88wr7rAKQCekgSIAaP0KjZI8g0TaUKc");

                return headers;
            }
        };

        queue.add(request);
    }
    private void registrarUsuario() {

        String matricula = ((TextInputEditText) findViewById(R.id.edtMatricula)).getText().toString().trim();
        String nombre = ((TextInputEditText) findViewById(R.id.edtNombre)).getText().toString().trim();
        String apP = ((TextInputEditText) findViewById(R.id.edtApellidoP)).getText().toString().trim();
        String apM = ((TextInputEditText) findViewById(R.id.edtApellidoM)).getText().toString().trim();
        String correo = ((TextInputEditText) findViewById(R.id.edtCorreo)).getText().toString().trim();
        String pass = ((TextInputEditText) findViewById(R.id.edtContrasenia)).getText().toString().trim();

        int posGrupo = spinnerGrupo.getSelectedItemPosition();

        if (matricula.isEmpty() || nombre.isEmpty() || correo.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // validar correo
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        int idGrupo = listaIdGrupos.get(posGrupo);

        String url = URL_BASE + "usuarios";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(this, MainActivity.class));
                    finish();

                },
                error -> {

                    error.printStackTrace();

                    if (error.networkResponse != null) {
                        String body = new String(error.networkResponse.data);
                        System.out.println("ERROR SERVER: " + body);
                    }

                    Toast.makeText(this, "Error al registrar", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public byte[] getBody() {

                try {
                    JSONObject json = new JSONObject();

                    json.put("id_tipo_usuario", 1);
                    json.put("matricula", matricula);
                    json.put("nombre", nombre);
                    json.put("apellido_paterno", apP);
                    json.put("apellido_materno", apM);
                    json.put("correo", correo);
                    json.put("contrasenia", pass);
                    json.put("id_grupo", idGrupo);



                    String body = json.toString();
                    System.out.println("JSON: " + body);

                    return body.getBytes("utf-8");

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> headers = new HashMap<>();

                headers.put("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB5eHVhcGJwaWN5bmZ2b3l0Z2drIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQzMTA1NjYsImV4cCI6MjA4OTg4NjU2Nn0.dNkk7jmUxxDq88wr7rAKQCekgSIAaP0KjZI8g0TaUKc");
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB5eHVhcGJwaWN5bmZ2b3l0Z2drIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQzMTA1NjYsImV4cCI6MjA4OTg4NjU2Nn0.dNkk7jmUxxDq88wr7rAKQCekgSIAaP0KjZI8g0TaUKc");
                headers.put("Content-Type", "application/json");
                headers.put("Prefer", "return=representation");

                return headers;
            }
        };

        queue.add(request);
    }
}