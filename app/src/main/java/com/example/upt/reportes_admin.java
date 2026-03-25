package com.example.upt;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class reportes_admin extends AppCompatActivity {
    private LinearLayout layoutReportesAdmin;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton btnMenu;

    int idUsuario, idTipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes_admin);

        layoutReportesAdmin = findViewById(R.id.layoutReportesAdmin);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);

        idUsuario = getIntent().getIntExtra("id_usuario", -1);
        idTipoUsuario = 3;

        MenuHelper.configurarMenu(this, drawerLayout, navigationView, btnMenu, idUsuario, idTipoUsuario);

        cargarReportesAdmin();

    }
    private void cargarReportesAdmin() {
        try {
            String select = "id,descripcion,fecha,id_estado," +
                    "usuarios(id,matricula,nombre,apellido_paterno,apellido_materno,id_grupo)," +
                    "edificios(nombre)," +
                    "aulas(nombre)," +
                    "estado_reporte(nombre)";

            String url = "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/reportes_i"
                    + "?id_estado=in.(1,2)"
                    + "&select=" + URLEncoder.encode(select, "UTF-8")
                    + "&order=fecha.desc";

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest request = new StringRequest(Request.Method.GET, url,
                    response -> {
                        try {
                            System.out.println("RESPUESTA REPORTES ADMIN: " + response);
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
                            System.out.println("ERROR CARGAR REPORTES ADMIN: " + body);
                        }

                        Toast.makeText(this, "Error al cargar reportes", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    return getSupabaseHeaders();
                }
            };

            queue.add(request);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error construyendo consulta", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarReportes(JSONArray array) {
        layoutReportesAdmin.removeAllViews();

        try {
            if (array.length() == 0) {
                TextView tvVacio = new TextView(this);
                tvVacio.setText("No hay reportes con estado 1 o 2");
                tvVacio.setTextSize(18);
                tvVacio.setPadding(dp(16), dp(16), dp(16), dp(16));
                layoutReportesAdmin.addView(tvVacio);
                return;
            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                int idReporte = obj.optInt("id", -1);
                int idEstadoActual = obj.optInt("id_estado", 1);

                String descripcion = obj.optString("descripcion", "Sin descripción");
                String fecha = obj.optString("fecha", "Sin fecha");

                String matricula = "Sin matrícula";
                String nombre = "Sin nombre";
                String apellidoPaterno = "";
                String apellidoMaterno = "";
                int idGrupo = -1;

                String edificio = "Sin edificio";
                String aula = "Sin salón";
                String estadoNombre = "Sin estado";

                JSONObject usuarioObj = obj.optJSONObject("usuarios");
                if (usuarioObj != null) {
                    matricula = usuarioObj.optString("matricula", "Sin matrícula");
                    nombre = usuarioObj.optString("nombre", "Sin nombre");
                    apellidoPaterno = usuarioObj.optString("apellido_paterno", "");
                    apellidoMaterno = usuarioObj.optString("apellido_materno", "");
                    idGrupo = usuarioObj.optInt("id_grupo", -1);
                }

                JSONObject edificioObj = obj.optJSONObject("edificios");
                if (edificioObj != null) {
                    edificio = edificioObj.optString("nombre", "Sin edificio");
                }

                JSONObject aulaObj = obj.optJSONObject("aulas");
                if (aulaObj != null) {
                    aula = aulaObj.optString("nombre", "Sin salón");
                }

                JSONObject estadoObj = obj.optJSONObject("estado_reporte");
                if (estadoObj != null) {
                    estadoNombre = estadoObj.optString("nombre", "Sin estado");
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

                TextView tvMatricula = new TextView(this);
                tvMatricula.setText("Matrícula: " + matricula);
                tvMatricula.setTextSize(18);

                TextView tvNombre = new TextView(this);
                tvNombre.setText("Nombre: " + nombre + " " + apellidoPaterno + " " + apellidoMaterno);
                tvNombre.setTextSize(18);

                TextView tvGrupo = new TextView(this);
                tvGrupo.setText("Grupo: cargando...");
                tvGrupo.setTextSize(18);

                TextView tvEdificio = new TextView(this);
                tvEdificio.setText("Edificio: " + edificio);
                tvEdificio.setTextSize(18);

                TextView tvAula = new TextView(this);
                tvAula.setText("Salón: " + aula);
                tvAula.setTextSize(18);

                TextView tvFecha = new TextView(this);
                tvFecha.setText("Fecha: " + fecha);
                tvFecha.setTextSize(16);

                TextView tvDescripcion = new TextView(this);
                tvDescripcion.setText("Descripción: " + descripcion);
                tvDescripcion.setTextSize(16);

                TextView tvEstadoActual = new TextView(this);
                tvEstadoActual.setText("Estado actual: " + estadoNombre);
                tvEstadoActual.setTextSize(18);

                TextView tvSelecciona = new TextView(this);
                tvSelecciona.setText("Selecciona nuevo estado:");
                tvSelecciona.setTextSize(16);
                tvSelecciona.setPadding(0, dp(12), 0, dp(6));

                Spinner spinnerEstados = new Spinner(this);

                ArrayList<String> listaEstados = new ArrayList<>();
                listaEstados.add("Pendiente");
                listaEstados.add("En progreso");
                listaEstados.add("Completado");

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        listaEstados
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerEstados.setAdapter(adapter);

                if (idEstadoActual == 1) {
                    spinnerEstados.setSelection(0);
                } else if (idEstadoActual == 2) {
                    spinnerEstados.setSelection(1);
                } else if (idEstadoActual == 3) {
                    spinnerEstados.setSelection(2);
                }

                Button btnActualizar = new Button(this);
                btnActualizar.setText("Actualizar estado");

                btnActualizar.setOnClickListener(v -> {
                    int nuevoEstado;

                    if (spinnerEstados.getSelectedItemPosition() == 0) {
                        nuevoEstado = 1;
                    } else if (spinnerEstados.getSelectedItemPosition() == 1) {
                        nuevoEstado = 2;
                    } else {
                        nuevoEstado = 3;
                    }

                    actualizarEstadoReporte(idReporte, nuevoEstado);
                });

                tarjeta.addView(tvMatricula);
                tarjeta.addView(tvNombre);
                tarjeta.addView(tvGrupo);
                tarjeta.addView(tvEdificio);
                tarjeta.addView(tvAula);
                tarjeta.addView(tvFecha);
                tarjeta.addView(tvDescripcion);
                tarjeta.addView(tvEstadoActual);

                if (idGrupo != -1) {
                    cargarNombreGrupo(idGrupo, tvGrupo);
                } else {
                    tvGrupo.setText("Grupo: Sin grupo");
                }

                if (idReporte != -1) {
                    cargarEvidenciaPorReporte(idReporte, tarjeta);
                } else {
                    TextView tvSinImagen = new TextView(this);
                    tvSinImagen.setText("Sin evidencia");
                    tvSinImagen.setTextSize(16);
                    tvSinImagen.setPadding(0, dp(12), 0, 0);
                    tarjeta.addView(tvSinImagen);
                }

                tarjeta.addView(tvSelecciona);
                tarjeta.addView(spinnerEstados);
                tarjeta.addView(btnActualizar);

                layoutReportesAdmin.addView(tarjeta);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error mostrando reportes", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarNombreGrupo(int idGrupo, TextView tvGrupo) {
        String url = "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/grupos?id=eq." + idGrupo + "&select=grupos&limit=1";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);

                        if (array.length() > 0) {
                            JSONObject obj = array.getJSONObject(0);
                            String grupo = obj.optString("grupos", "Sin grupo");
                            tvGrupo.setText("Grupo: " + grupo);
                        } else {
                            tvGrupo.setText("Grupo: Sin grupo");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        tvGrupo.setText("Grupo: Error");
                    }
                },
                error -> {
                    error.printStackTrace();

                    if (error.networkResponse != null) {
                        String body = new String(error.networkResponse.data);
                        System.out.println("ERROR GRUPO: " + body);
                    }

                    tvGrupo.setText("Grupo: Error");
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return getSupabaseHeaders();
            }
        };

        queue.add(request);
    }

    private void cargarEvidenciaPorReporte(int idReporte, LinearLayout tarjeta) {
        String url =  "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/evidencias?id_reporte=eq." + idReporte + "&select=url&limit=1";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        System.out.println("EVIDENCIA REPORTE " + idReporte + ": " + response);

                        JSONArray array = new JSONArray(response);

                        if (array.length() > 0) {
                            JSONObject evidenciaObj = array.getJSONObject(0);
                            String imagenUrl = evidenciaObj.optString("url", "");

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
                        System.out.println("ERROR EVIDENCIA ADMIN " + idReporte + ": " + body);
                    }

                    TextView tvSinImagen = new TextView(this);
                    tvSinImagen.setText("Sin evidencia");
                    tvSinImagen.setTextSize(16);
                    tvSinImagen.setPadding(0, dp(12), 0, 0);
                    tarjeta.addView(tvSinImagen);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return getSupabaseHeaders();
            }
        };

        queue.add(request);
    }

    private void actualizarEstadoReporte(int idReporte, int nuevoEstado) {
        if (idReporte == -1) {
            Toast.makeText(this, "Reporte inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/reportes_i?id=eq." + idReporte;

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.PATCH, url,
                response -> {
                    Toast.makeText(this, "Estado actualizado correctamente", Toast.LENGTH_SHORT).show();
                    cargarReportesAdmin();
                },
                error -> {
                    error.printStackTrace();

                    if (error.networkResponse != null) {
                        String body = new String(error.networkResponse.data);
                        System.out.println("ERROR ACTUALIZAR ESTADO: " + body);
                    }

                    Toast.makeText(this, "Error al actualizar estado", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public byte[] getBody() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("id_estado", nuevoEstado);
                    return json.toString().getBytes("utf-8");
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
                Map<String, String> headers = getSupabaseHeaders();
                headers.put("Content-Type", "application/json");
                headers.put("Prefer", "return=minimal");
                return headers;
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
