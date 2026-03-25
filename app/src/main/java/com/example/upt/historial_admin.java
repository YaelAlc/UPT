package com.example.upt;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class historial_admin extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton btnMenu;

    Spinner spinnerFiltro;
    Button btnSeleccionarFechaInicio, btnSeleccionarFechaFin, btnAplicarRango;
    TextView txtRangoSeleccionado;
    RecyclerView recyclerHistorial;

    ArrayList<ReporteHistorialAdmin> listaReportes;
    HistorialAdminAdapter adapter;

    int idUsuario, idTipoUsuario;

    String fechaInicio = "";
    String fechaFin = "";



    RequestQueue queue;

    JSONArray reportesArray;
    JSONArray usuariosArray;
    JSONArray gruposArray;
    JSONArray aulasArray;
    JSONArray edificiosArray;
    JSONArray estadosArray;
    JSONArray evidenciasArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_admin);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);

        spinnerFiltro = findViewById(R.id.spinnerFiltro);
        btnSeleccionarFechaInicio = findViewById(R.id.btnSeleccionarFechaInicio);
        btnSeleccionarFechaFin = findViewById(R.id.btnSeleccionarFechaFin);
        btnAplicarRango = findViewById(R.id.btnAplicarRango);
        txtRangoSeleccionado = findViewById(R.id.txtRangoSeleccionado);
        recyclerHistorial = findViewById(R.id.recyclerHistorial);

        idUsuario = getIntent().getIntExtra("id_usuario", -1);
        idTipoUsuario = getIntent().getIntExtra("id_tipo_usuario", 3);

        MenuHelper.configurarMenu(this, drawerLayout, navigationView, btnMenu, idUsuario, idTipoUsuario);

        queue = Volley.newRequestQueue(this);

        listaReportes = new ArrayList<>();
        adapter = new HistorialAdminAdapter(this, listaReportes);
        recyclerHistorial.setLayoutManager(new LinearLayoutManager(this));
        recyclerHistorial.setAdapter(adapter);

        configurarSpinner();

        btnSeleccionarFechaInicio.setOnClickListener(v -> abrirDatePicker(true));
        btnSeleccionarFechaFin.setOnClickListener(v -> abrirDatePicker(false));
        btnAplicarRango.setOnClickListener(v -> aplicarFiltroPersonalizado());

        cargarTodo("Todos");
    }

    private void configurarSpinner() {
        ArrayList<String> filtros = new ArrayList<>();
        filtros.add("Todos");
        filtros.add("Hoy");
        filtros.add("Ayer");
        filtros.add("Últimos 7 días");
        filtros.add("Últimos 30 días");
        filtros.add("Este mes");
        filtros.add("Personalizado");

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                filtros
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(adapterSpinner);

        spinnerFiltro.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String opcion = parent.getItemAtPosition(position).toString();

                btnSeleccionarFechaInicio.setVisibility(View.GONE);
                btnSeleccionarFechaFin.setVisibility(View.GONE);
                btnAplicarRango.setVisibility(View.GONE);

                if (opcion.equals("Personalizado")) {
                    txtRangoSeleccionado.setText("Selecciona fecha inicio y fin");
                    btnSeleccionarFechaInicio.setVisibility(View.VISIBLE);
                    btnSeleccionarFechaFin.setVisibility(View.VISIBLE);
                    btnAplicarRango.setVisibility(View.VISIBLE);
                } else {
                    cargarTodo(opcion);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });
    }

    private void abrirDatePicker(boolean esInicio) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);

                    if (esInicio) {
                        fechaInicio = fecha;
                        btnSeleccionarFechaInicio.setText("Inicio: " + fecha);
                    } else {
                        fechaFin = fecha;
                        btnSeleccionarFechaFin.setText("Fin: " + fecha);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void aplicarFiltroPersonalizado() {
        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            Toast.makeText(this, "Selecciona ambas fechas", Toast.LENGTH_SHORT).show();
            return;
        }

        txtRangoSeleccionado.setText("Mostrando: " + fechaInicio + " a " + fechaFin);
        cargarReportes(fechaInicio, fechaFin);
    }

    private void cargarTodo(String filtro) {
        Calendar hoy = Calendar.getInstance();
        String inicio = null;
        String fin = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(hoy.getTime());

        switch (filtro) {
            case "Todos":
                txtRangoSeleccionado.setText("Mostrando: todos");
                cargarReportes(null, null);
                return;

            case "Hoy":
                inicio = fin;
                txtRangoSeleccionado.setText("Mostrando: hoy");
                break;

            case "Ayer":
                hoy.add(Calendar.DAY_OF_MONTH, -1);
                inicio = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(hoy.getTime());
                fin = inicio;
                txtRangoSeleccionado.setText("Mostrando: ayer");
                break;

            case "Últimos 7 días":
                hoy = Calendar.getInstance();
                Calendar hace7 = Calendar.getInstance();
                hace7.add(Calendar.DAY_OF_MONTH, -6);
                inicio = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(hace7.getTime());
                fin = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(hoy.getTime());
                txtRangoSeleccionado.setText("Mostrando: últimos 7 días");
                break;

            case "Últimos 30 días":
                hoy = Calendar.getInstance();
                Calendar hace30 = Calendar.getInstance();
                hace30.add(Calendar.DAY_OF_MONTH, -29);
                inicio = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(hace30.getTime());
                fin = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(hoy.getTime());
                txtRangoSeleccionado.setText("Mostrando: últimos 30 días");
                break;

            case "Este mes":
                Calendar inicioMes = Calendar.getInstance();
                inicioMes.set(Calendar.DAY_OF_MONTH, 1);
                inicio = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(inicioMes.getTime());
                fin = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
                txtRangoSeleccionado.setText("Mostrando: este mes");
                break;
        }

        cargarReportes(inicio, fin);
    }

    private void cargarReportes(String inicio, String fin) {

        String BASE_URL = "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/reportes_i";
        String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB5eHVhcGJwaWN5bmZ2b3l0Z2drIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQzMTA1NjYsImV4cCI6MjA4OTg4NjU2Nn0.dNkk7jmUxxDq88wr7rAKQCekgSIAaP0KjZI8g0TaUKc";

        String url;

        if (inicio == null || fin == null) {
            url = BASE_URL + "?select=*&order=fecha.desc";
        } else {
            url = BASE_URL + "?select=*"
                    + "&fecha=gte." + inicio + "T00:00:00"
                    + "&fecha=lte." + fin + "T23:59:59"
                    + "&order=fecha.desc";
        }

        System.out.println("URL FINAL: " + url); // 👈 para debug

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    System.out.println("RESPUESTA REPORTES: " + response.toString());

                    reportesArray = response;
                    cargarUsuarios();
                },
                error -> {
                    error.printStackTrace();

                    if (error.networkResponse != null) {
                        String body = new String(error.networkResponse.data);
                        System.out.println("ERROR DETALLE: " + body);
                    }

                    Toast.makeText(this, "Error al cargar historial", Toast.LENGTH_LONG).show();
                }) {

            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> headers = new HashMap<>();

                headers.put("apikey", API_KEY);
                headers.put("Authorization", "Bearer " + API_KEY);
                headers.put("Content-Type", "application/json");

                return headers;
            }
        };

        queue.add(request);
    }

    private void cargarUsuarios() {
        String url = "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/usuarios?select=*";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    usuariosArray = response;
                    cargarGrupos();
                },
                error -> Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_LONG).show()) {
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

    private void cargarGrupos() {
        String url = "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/grupos?select=*";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    gruposArray = response;
                    cargarAulas();
                },
                error -> Toast.makeText(this, "Error al cargar grupos", Toast.LENGTH_LONG).show()) {
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

    private void cargarAulas() {
        String url = "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/aulas?select=*";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    aulasArray = response;
                    cargarEdificios();
                },
                error -> Toast.makeText(this, "Error al cargar aulas", Toast.LENGTH_LONG).show()) {
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

    private void cargarEdificios() {
        String url = "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/edificios?select=*";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    edificiosArray = response;
                    cargarEstados();
                },
                error -> Toast.makeText(this, "Error al cargar edificios", Toast.LENGTH_LONG).show()) {
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

    private void cargarEstados() {
        String url = "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/estado_reporte?select=*";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    estadosArray = response;
                    cargarEvidencias();
                },
                error -> Toast.makeText(this, "Error al cargar estados", Toast.LENGTH_LONG).show()) {
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

    private void cargarEvidencias() {
        String url = "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/evidencias?select=*";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    evidenciasArray = response;
                    construirListaFinal();
                },
                error -> Toast.makeText(this, "Error al cargar evidencias", Toast.LENGTH_LONG).show()) {
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

    private void construirListaFinal() {

        try {

            listaReportes.clear();

            if (reportesArray == null) {
                Toast.makeText(this, "No hay reportes", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔹 Mapas
            HashMap<Integer, JSONObject> mapaUsuarios = new HashMap<>();
            HashMap<Integer, String> mapaGrupos = new HashMap<>();
            HashMap<Integer, JSONObject> mapaAulas = new HashMap<>();
            HashMap<Integer, String> mapaEdificios = new HashMap<>();
            HashMap<Integer, String> mapaEstados = new HashMap<>();
            HashMap<Integer, String> mapaEvidencias = new HashMap<>();

            // 🔹 USUARIOS
            if (usuariosArray != null) {
                for (int i = 0; i < usuariosArray.length(); i++) {
                    JSONObject obj = usuariosArray.optJSONObject(i);
                    if (obj != null) {
                        int id = obj.optInt("id", -1);
                        mapaUsuarios.put(id, obj);
                    }
                }
            }

            // 🔹 GRUPOS
            if (gruposArray != null) {
                for (int i = 0; i < gruposArray.length(); i++) {
                    JSONObject obj = gruposArray.optJSONObject(i);
                    if (obj != null) {
                        int id = obj.optInt("id", -1);
                        String nombre = obj.optString("grupos", "Sin grupo");
                        mapaGrupos.put(id, nombre);
                    }
                }
            }

            // 🔹 AULAS
            if (aulasArray != null) {
                for (int i = 0; i < aulasArray.length(); i++) {
                    JSONObject obj = aulasArray.optJSONObject(i);
                    if (obj != null) {
                        int id = obj.optInt("id", -1);
                        mapaAulas.put(id, obj);
                    }
                }
            }

            // 🔹 EDIFICIOS
            if (edificiosArray != null) {
                for (int i = 0; i < edificiosArray.length(); i++) {
                    JSONObject obj = edificiosArray.optJSONObject(i);
                    if (obj != null) {
                        int id = obj.optInt("id", -1);
                        String nombre = obj.optString("nombre", "Sin edificio");
                        mapaEdificios.put(id, nombre);
                    }
                }
            }

            // 🔹 ESTADOS
            if (estadosArray != null) {
                for (int i = 0; i < estadosArray.length(); i++) {
                    JSONObject obj = estadosArray.optJSONObject(i);
                    if (obj != null) {
                        int id = obj.optInt("id", -1);
                        String nombre = obj.optString("nombre", "Sin estado");
                        mapaEstados.put(id, nombre);
                    }
                }
            }

            // 🔹 EVIDENCIAS
            if (evidenciasArray != null) {
                for (int i = 0; i < evidenciasArray.length(); i++) {
                    JSONObject obj = evidenciasArray.optJSONObject(i);
                    if (obj != null) {
                        int idReporte = obj.optInt("id_reporte", -1);
                        String url = obj.optString("url", "");
                        mapaEvidencias.put(idReporte, url);
                    }
                }
            }

            // 🔥 ARMAR LISTA FINAL
            for (int i = 0; i < reportesArray.length(); i++) {

                JSONObject reporte = reportesArray.optJSONObject(i);
                if (reporte == null) continue;

                int idReporte = reporte.optInt("id", -1);
                int idUsuarioReporte = reporte.optInt("id_usuario", -1);
                int idEdificioReporte = reporte.optInt("id_edificio", -1);
                int idAulaReporte = reporte.optInt("id_aula", -1);
                int idEstadoReporte = reporte.optInt("id_estado", -1);

                String descripcion = reporte.optString("descripcion", "");
                String fecha = reporte.optString("fecha", "");

                String matricula = "Sin matrícula";
                String nombreCompleto = "Sin nombre";
                String grupo = "Sin grupo";
                String edificio = "Sin edificio";
                String aula = "Sin aula";
                String estado = "Sin estado";
                String evidenciaUrl = "";

                // 🔹 USUARIO
                if (mapaUsuarios.containsKey(idUsuarioReporte)) {
                    JSONObject usuario = mapaUsuarios.get(idUsuarioReporte);

                    matricula = usuario.optString("matricula", "Sin matrícula");

                    String nombre = usuario.optString("nombre", "");
                    String apP = usuario.optString("apellido_paterno", "");
                    String apM = usuario.optString("apellido_materno", "");

                    nombreCompleto = nombre + " " + apP + " " + apM;

                    int idGrupo = usuario.optInt("id_grupo", -1);
                    if (mapaGrupos.containsKey(idGrupo)) {
                        grupo = mapaGrupos.get(idGrupo);
                    }
                }

                // 🔹 EDIFICIO
                if (mapaEdificios.containsKey(idEdificioReporte)) {
                    edificio = mapaEdificios.get(idEdificioReporte);
                }

                // 🔹 AULA
                if (mapaAulas.containsKey(idAulaReporte)) {
                    JSONObject aulaObj = mapaAulas.get(idAulaReporte);
                    if (aulaObj != null) {
                        aula = aulaObj.optString("nombre", "Sin aula");
                    }
                }

                // 🔹 ESTADO
                if (mapaEstados.containsKey(idEstadoReporte)) {
                    estado = mapaEstados.get(idEstadoReporte);
                }

                // 🔹 EVIDENCIA
                if (mapaEvidencias.containsKey(idReporte)) {
                    evidenciaUrl = mapaEvidencias.get(idReporte);
                }

                listaReportes.add(new ReporteHistorialAdmin(
                        idReporte,
                        matricula,
                        nombreCompleto,
                        grupo,
                        edificio,
                        aula,
                        fecha,
                        descripcion,
                        estado,
                        evidenciaUrl
                ));
            }

            adapter.notifyDataSetChanged();

            if (listaReportes.isEmpty()) {
                Toast.makeText(this, "No hay reportes en ese rango", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar historial", Toast.LENGTH_LONG).show();
        }
    }
}