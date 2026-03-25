package com.example.upt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class segunda_interfaz_reporte extends AppCompatActivity {
    Spinner spinnerEdificio, spinnerSalon;
    Button btnEnviar;

    ArrayList<String> listaEdificios = new ArrayList<>();
    ArrayList<Integer> idsEdificios = new ArrayList<>();

    ArrayList<String> listaSalones = new ArrayList<>();
    ArrayList<Integer> idsSalones = new ArrayList<>();

    int idUsuario;
    String descripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segunda_interfaz_reporte);
        spinnerEdificio = findViewById(R.id.spinner);
        spinnerSalon = findViewById(R.id.spinner2);
        btnEnviar = findViewById(R.id.button);

        descripcion = getIntent().getStringExtra("descripcion");
        idUsuario = getIntent().getIntExtra("id_usuario", -1);

        cargarEdificios();

        spinnerEdificio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarSalones(idsEdificios.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnEnviar.setOnClickListener(v -> insertarReporte());
    }
    private void insertarReporte() {

        if (spinnerEdificio.getSelectedItem() == null || spinnerSalon.getSelectedItem() == null) {
            Toast.makeText(this, "Selecciona edificio y salón", Toast.LENGTH_SHORT).show();
            return;
        }

        if (descripcion == null || descripcion.isEmpty()) {
            Toast.makeText(this, "Descripción vacía", Toast.LENGTH_SHORT).show();
            return;
        }

        int idEdificio = idsEdificios.get(spinnerEdificio.getSelectedItemPosition());
        int idSalon = idsSalones.get(spinnerSalon.getSelectedItemPosition());

        String fecha = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());

        String url = "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/reportes_i";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    System.out.println("RESPUESTA OK: " + response);

                    try {
                        JSONArray array = new JSONArray(response);

                        if (array.length() > 0) {
                            int idReporte = array.getJSONObject(0).getInt("id");

                            Intent intent = new Intent(this, tercera_interfaz_reporte.class);
                            intent.putExtra("id_reporte", idReporte);
                            intent.putExtra("id_usuario", idUsuario);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "No se recibió ID", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parseando respuesta", Toast.LENGTH_SHORT).show();
                    }

                },
                error -> {

                    error.printStackTrace();

                    if (error.networkResponse != null) {
                        String body = new String(error.networkResponse.data);
                        System.out.println("ERROR SERVER: " + body);
                    }

                    Toast.makeText(this, "Error al guardar reporte", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public byte[] getBody() {
                try {
                    org.json.JSONObject json = new org.json.JSONObject();

                    json.put("id_usuario", idUsuario);
                    json.put("id_edificio", idEdificio);
                    json.put("id_aula", idSalon);
                    json.put("descripcion", descripcion);
                    json.put("fecha", fecha);
                    json.put("id_estado", 1);

                    String body = json.toString();
                    System.out.println("JSON ENVIADO: " + body);

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
                headers.put("Prefer", "return=representation"); // 🔥 clave para recibir ID
                return headers;
            }
        };

        queue.add(request);
    }

    private void cargarEdificios() {

        String url = "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/edificios?select=*";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);

                        listaEdificios.clear();
                        idsEdificios.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            listaEdificios.add(obj.getString("nombre"));
                            idsEdificios.add(obj.getInt("id"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item, listaEdificios);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        spinnerEdificio.setAdapter(adapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error edificios", Toast.LENGTH_SHORT).show();
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

    private void cargarSalones(int idEdificio) {

        String url = "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/aulas?id_edificio=eq." + idEdificio + "&select=*";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);

                        listaSalones.clear();
                        idsSalones.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            listaSalones.add(obj.getString("nombre"));
                            idsSalones.add(obj.getInt("id"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item, listaSalones);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        spinnerSalon.setAdapter(adapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error salones", Toast.LENGTH_SHORT).show();
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

}