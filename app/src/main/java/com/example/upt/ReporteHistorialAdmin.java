package com.example.upt;

public class ReporteHistorialAdmin {

    private int idReporte;
    private String matricula;
    private String nombreCompleto;
    private String grupo;
    private String edificio;
    private String aula;
    private String fecha;
    private String descripcion;
    private String estado;
    private String evidenciaUrl;

    public ReporteHistorialAdmin(int idReporte, String matricula, String nombreCompleto,
                                 String grupo, String edificio, String aula,
                                 String fecha, String descripcion,
                                 String estado, String evidenciaUrl) {
        this.idReporte = idReporte;
        this.matricula = matricula;
        this.nombreCompleto = nombreCompleto;
        this.grupo = grupo;
        this.edificio = edificio;
        this.aula = aula;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.estado = estado;
        this.evidenciaUrl = evidenciaUrl;
    }

    public int getIdReporte() {
        return idReporte;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getGrupo() {
        return grupo;
    }

    public String getEdificio() {
        return edificio;
    }

    public String getAula() {
        return aula;
    }

    public String getFecha() {
        return fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public String getEvidenciaUrl() {
        return evidenciaUrl;
    }
}