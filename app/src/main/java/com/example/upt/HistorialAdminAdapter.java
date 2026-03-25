package com.example.upt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class HistorialAdminAdapter extends RecyclerView.Adapter<HistorialAdminAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ReporteHistorialAdmin> lista;

    public HistorialAdminAdapter(Context context, ArrayList<ReporteHistorialAdmin> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_historial_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReporteHistorialAdmin reporte = lista.get(position);

        holder.txtMatricula.setText("Matrícula: " + reporte.getMatricula());
        holder.txtNombre.setText("Nombre: " + reporte.getNombreCompleto());
        holder.txtGrupo.setText("Grupo: " + reporte.getGrupo());
        holder.txtEdificio.setText("Edificio: " + reporte.getEdificio());
        holder.txtAula.setText("Salón: " + reporte.getAula());
        holder.txtFecha.setText("Fecha: " + reporte.getFecha());
        holder.txtEstado.setText("Estado: " + reporte.getEstado());
        holder.txtDescripcion.setText("Descripción: " + reporte.getDescripcion());

        if (reporte.getEvidenciaUrl() != null && !reporte.getEvidenciaUrl().isEmpty()) {
            holder.imgEvidencia.setVisibility(View.VISIBLE);
            holder.txtSinImagen.setVisibility(View.GONE);

            Glide.with(context)
                    .load(reporte.getEvidenciaUrl())
                    .into(holder.imgEvidencia);
        } else {
            holder.imgEvidencia.setVisibility(View.GONE);
            holder.txtSinImagen.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtMatricula, txtNombre, txtGrupo, txtEdificio, txtAula, txtFecha, txtEstado, txtDescripcion, txtSinImagen;
        ImageView imgEvidencia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMatricula = itemView.findViewById(R.id.txtMatricula);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtGrupo = itemView.findViewById(R.id.txtGrupo);
            txtEdificio = itemView.findViewById(R.id.txtEdificio);
            txtAula = itemView.findViewById(R.id.txtAula);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            txtSinImagen = itemView.findViewById(R.id.txtSinImagen);
            imgEvidencia = itemView.findViewById(R.id.imgEvidencia);
        }
    }
}
