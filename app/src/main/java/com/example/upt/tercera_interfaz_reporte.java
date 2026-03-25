package com.example.upt;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class tercera_interfaz_reporte extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;

    ImageView imageView;
    Button btnSeleccionar, btnSubir;

    Uri imagenUri;
    String imagenURL = "";

    int idReporte, idUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tercera_interfaz_reporte);
        imageView = findViewById(R.id.imageView);
        btnSeleccionar = findViewById(R.id.btnSeleccionar);
        btnSubir = findViewById(R.id.btnSubir);

        idReporte = getIntent().getIntExtra("id_reporte", -1);
        idUsuario = getIntent().getIntExtra("id_usuario", -1);

        btnSeleccionar.setOnClickListener(v -> abrirGaleria());
        btnSubir.setOnClickListener(v -> subirImagen());

    }
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            imagenUri = data.getData();
            imageView.setImageURI(imagenUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void subirImagen() {

        if (imagenUri == null) {
            Toast.makeText(this, "Selecciona imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(imagenUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);

            String nombreArchivo = "reporte_" + idReporte + ".jpg";

            String url = "https://pyxuapbpicynfvoytggk.supabase.co/storage/v1/object/evidencias/" + nombreArchivo;

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest request = new StringRequest(Request.Method.PUT, url,
                    response -> {
                        imagenURL = "https://pyxuapbpicynfvoytggk.supabase.co/storage/v1/object/public/evidencias/" + nombreArchivo;
                        guardarEvidencia();
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(this, "Error subiendo imagen", Toast.LENGTH_SHORT).show();
                    }
            ) {
                @Override
                public byte[] getBody() {
                    return bytes;
                }

                @Override
                public String getBodyContentType() {
                    return "image/jpeg";
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB5eHVhcGJwaWN5bmZ2b3l0Z2drIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQzMTA1NjYsImV4cCI6MjA4OTg4NjU2Nn0.dNkk7jmUxxDq88wr7rAKQCekgSIAaP0KjZI8g0TaUKc");
                    headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB5eHVhcGJwaWN5bmZ2b3l0Z2drIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQzMTA1NjYsImV4cCI6MjA4OTg4NjU2Nn0.dNkk7jmUxxDq88wr7rAKQCekgSIAaP0KjZI8g0TaUKc");
                    return headers;
                }
            };

            queue.add(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void guardarEvidencia() {

        System.out.println("ID REPORTE: " + idReporte);
        System.out.println("URL IMAGEN: " + imagenURL);

        String url = "https://pyxuapbpicynfvoytggk.supabase.co/rest/v1/evidencias";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    System.out.println("RESPUESTA EVIDENCIA: " + response);

                    Toast.makeText(this, "Evidencia guardada", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, historial_reportes.class);
                    intent.putExtra("id_usuario", idUsuario);
                    startActivity(intent);
                    finish();
                },
                error -> {

                    error.printStackTrace();

                    if (error.networkResponse != null) {
                        String body = new String(error.networkResponse.data);
                        System.out.println("ERROR EVIDENCIA: " + body);
                    }

                    Toast.makeText(this, "Error guardando evidencia", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public byte[] getBody() {
                try {
                    org.json.JSONObject json = new org.json.JSONObject();

                    json.put("id_reporte", idReporte);
                    json.put("url", imagenURL);

                    String body = json.toString();
                    System.out.println("JSON EVIDENCIA: " + body);

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
                return headers;
            }
        };

        queue.add(request);
    }
}