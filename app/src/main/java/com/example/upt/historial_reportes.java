package com.example.upt;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class historial_reportes extends AppCompatActivity {
    private Spinner spinnerEstado;
    private LinearLayout layoutReportes;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton btnMenu;

    int idUsuario, idTipoUsuario;

    private final ArrayList<String> listaEstados = new ArrayList<>();
    private final ArrayList<Integer> idsEstados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_reportes);

        spinnerEstado = findViewById(R.id.spinner3);
        layoutReportes = findViewById(R.id.layoutReportes);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);

        idUsuario = getIntent().getIntExtra("id_usuario", -1);
        idTipoUsuario = getIntent().getIntExtra("id_tipo_usuario", -1);


        MenuHelper.configurarMenu(this, drawerLayout, navigationView, btnMenu, idUsuario, idTipoUsuario);

        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarEstados();

        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                int idEstadoSeleccionado = idsEstados.get(position);
                cargarReportesUsuario(idEstadoSeleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void cargarEstados() {
        String url =  "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/estado_reporte?select=id,nombre&order=id.asc";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);

                        listaEstados.clear();
                        idsEstados.clear();

                        listaEstados.add("Todos");
                        idsEstados.add(-1);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            idsEstados.add(obj.getInt("id"));
                            listaEstados.add(obj.getString("nombre"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                listaEstados
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerEstado.setAdapter(adapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error cargando estados", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    if (error.networkResponse != null) {
                        String body = new String(error.networkResponse.data);
                        System.out.println("ERROR ESTADOS: " + body);
                    }
                    Toast.makeText(this, "Error al cargar estados", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return getSupabaseHeaders();
            }
        };

        queue.add(request);
    }

    private void cargarReportesUsuario(int idEstado) {
        String urlBase =  "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/reportes_i";

        String select =
                "id,descripcion,fecha," +
                        "edificios(nombre)," +
                        "aulas(nombre)," +
                        "estado_reporte(nombre)";

        String url;
        if (idEstado == -1) {
            url = urlBase
                    + "?id_usuario=eq." + idUsuario
                    + "&select=" + select
                    + "&order=fecha.desc";
        } else {
            url = urlBase
                    + "?id_usuario=eq." + idUsuario
                    + "&id_estado=eq." + idEstado
                    + "&select=" + select
                    + "&order=fecha.desc";
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        mostrarReportes(array);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error procesando reportes", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();

                    if (error.networkResponse != null) {
                        String body = new String(error.networkResponse.data);
                        System.out.println("ERROR REPORTES: " + body);
                    }

                    Toast.makeText(this, "Error cargando reportes", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return getSupabaseHeaders();
            }
        };

        queue.add(request);
    }

    private TextView crearTexto(String texto, int size) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextSize(size);
        tv.setTextColor(android.graphics.Color.parseColor("#333333")); // gris oscuro elegante
        return tv;
    }
    private void mostrarReportes(JSONArray array) {
        layoutReportes.removeAllViews();

        try {
            if (array.length() == 0) {
                TextView tvVacio = crearTexto("No hay reportes para mostrar", 18);
                tvVacio.setPadding(dp(16), dp(16), dp(16), dp(16));
                layoutReportes.addView(tvVacio);
                return;
            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                int idReporte = obj.optInt("id", -1);
                String descripcion = obj.optString("descripcion", "Sin descripción");
                String fecha = obj.optString("fecha", "Sin fecha");

                String edificio = "Sin edificio";
                String aula = "Sin aula";
                String estado = "Sin estado";

                JSONObject edificioObj = obj.optJSONObject("edificios");
                if (edificioObj != null) {
                    edificio = edificioObj.optString("nombre", "Sin edificio");
                }

                JSONObject aulaObj = obj.optJSONObject("aulas");
                if (aulaObj != null) {
                    aula = aulaObj.optString("nombre", "Sin aula");
                }

                JSONObject estadoObj = obj.optJSONObject("estado_reporte");
                if (estadoObj != null) {
                    estado = estadoObj.optString("nombre", "Sin estado");
                }

                LinearLayout tarjeta = new LinearLayout(this);
                tarjeta.setOrientation(LinearLayout.VERTICAL);
                tarjeta.setPadding(dp(16), dp(16), dp(16), dp(16));
                tarjeta.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

                LinearLayout.LayoutParams tarjetaParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                tarjetaParams.setMargins(0, 0, 0, dp(16));
                tarjeta.setLayoutParams(tarjetaParams);

                TextView tvEdificio = crearTexto("Edificio: " + edificio, 18);
                TextView tvAula = crearTexto("Aula: " + aula, 18);

                TextView tvEstado = crearTexto("Estado: " + estado, 18);
                tvEstado.setTextColor(android.graphics.Color.parseColor("#1976D2")); // azul pro

                TextView tvDescripcion = crearTexto("Descripción: " + descripcion, 16);
                TextView tvFecha = crearTexto("Fecha: " + fecha, 16);

                tarjeta.addView(tvEdificio);
                tarjeta.addView(tvAula);
                tarjeta.addView(tvEstado);
                tarjeta.addView(tvDescripcion);
                tarjeta.addView(tvFecha);

                layoutReportes.addView(tarjeta);

                if (idReporte != -1) {
                    cargarEvidenciaPorReporte(idReporte, tarjeta);
                } else {
                    TextView tvSinImagen = crearTexto("Sin evidencia", 16);
                    tvSinImagen.setPadding(0, dp(12), 0, 0);
                    tarjeta.addView(tvSinImagen);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error mostrando reportes", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarEvidenciaPorReporte(int idReporte, LinearLayout tarjeta) {
        String url =  "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/evidencias?id_reporte=eq." + idReporte + "&select=url&limit=1";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);

                        if (array.length() > 0) {
                            JSONObject evidenciaObj = array.getJSONObject(0);
                            String imagenUrl = evidenciaObj.optString("url", "");

                            System.out.println("ID REPORTE CON EVIDENCIA: " + idReporte);
                            System.out.println("URL ENCONTRADA: " + imagenUrl);

                            if (!imagenUrl.isEmpty() && !imagenUrl.equals("null")) {
                                ImageView imageView = new ImageView(this);

                                LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        dp(220)
                                );
                                imageParams.topMargin = dp(12);
                                imageView.setLayoutParams(imageParams);
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                imageView.setAdjustViewBounds(true);

                                tarjeta.addView(imageView);

                                Glide.with(this)
                                        .load(imagenUrl)
                                        .into(imageView);
                            } else {
                                TextView tvSinImagen = new TextView(this);
                                tvSinImagen.setText("Sin evidencia");
                                tvSinImagen.setTextSize(16);
                                tvSinImagen.setPadding(0, dp(12), 0, 0);
                                tarjeta.addView(tvSinImagen);
                            }

                        } else {
                            System.out.println("NO SE ENCONTRO EVIDENCIA PARA REPORTE: " + idReporte);

                            TextView tvSinImagen = new TextView(this);
                            tvSinImagen.setText("Sin evidencia");
                            tvSinImagen.setTextSize(16);
                            tvSinImagen.setPadding(0, dp(12), 0, 0);
                            tarjeta.addView(tvSinImagen);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                        TextView tvError = new TextView(this);
                        tvError.setText("Error cargando evidencia");
                        tvError.setTextSize(16);
                        tvError.setPadding(0, dp(12), 0, 0);
                        tarjeta.addView(tvError);
                    }
                },
                error -> {
                    error.printStackTrace();

                    if (error.networkResponse != null) {
                        String body = new String(error.networkResponse.data);
                        System.out.println("ERROR EVIDENCIA REPORTE " + idReporte + ": " + body);
                    }

                    TextView tvError = new TextView(this);
                    tvError.setText("Sin evidencia");
                    tvError.setTextSize(16);
                    tvError.setPadding(0, dp(12), 0, 0);
                    tarjeta.addView(tvError);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return getSupabaseHeaders();
            }
        };

        queue.add(request);
    }

    private Map<String, String> getSupabaseHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB5eHVhcGJwaWN5bmZ2b3l0Z2drIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQzMTA1NjYsImV4cCI6MjA4OTg4NjU2Nn0.dNkk7jmUxxDq88wr7rAKQCekgSIAaP0KjZI8g0TaUKc");
        headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB5eHVhcGJwaWN5bmZ2b3l0Z2drIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQzMTA1NjYsImV4cCI6MjA4OTg4NjU2Nn0.dNkk7jmUxxDq88wr7rAKQCekgSIAaP0KjZI8g0TaUKc");
        return headers;
    }

    private int dp(int valor) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                valor,
                getResources().getDisplayMetrics()
        );
    }
}

