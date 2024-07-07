package com.gestionTFT.GestionTFT.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tft")
public class Tft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tft", nullable = false)
    private Integer id;

    @Column(name = "titulo")
    private String titulo;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @Lob
    @Column(name = "objetivos")
    private String objetivos;

    @Lob
    @Column(name = "metodologia")
    private String metodologia;

    @Lob
    @Column(name = "resultados_esperados")
    private String resultadosEsperados;

    @Column(name = "calificacion")
    private Double calificacion;

    @Lob
    @Column(name = "estado")
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno")
    private User alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor")
    private User tutor;

    @Column(name = "memoria")
    private String memoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tribunal_id")
    private Tribunal tribunal;

    @Column(name = "examen_datetime")
    private Instant examenDatetime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "titulacion")
    private Titulacion titulacion;

    @Column(name = "disponibilidad")
    private String disponibilidad;

    @Column(name = "beca")
    private Boolean beca;

    public Boolean getBeca() {
        return beca;
    }

    public void setBeca(Boolean beca) {
        this.beca = beca;
    }

    public String getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public Titulacion getTitulacion() {
        return titulacion;
    }

    public void setTitulacion(Titulacion titulacion) {
        this.titulacion = titulacion;
    }

    public Instant getExamenDatetime() {
        return examenDatetime;
    }

    public void setExamenDatetime(Instant examenDatetime) {
        this.examenDatetime = examenDatetime;
    }

    public Tribunal getTribunal() {
        return tribunal;
    }

    public void setTribunal(Tribunal tribunal) {
        this.tribunal = tribunal;
    }

    public String getMemoria() {
        return memoria;
    }

    public void setMemoria(String memoria) {
        this.memoria = memoria;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getObjetivos() {
        return objetivos;
    }

    public void setObjetivos(String objetivos) {
        this.objetivos = objetivos;
    }

    public String getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(String metodologia) {
        this.metodologia = metodologia;
    }

    public String getResultadosEsperados() {
        return resultadosEsperados;
    }

    public void setResultadosEsperados(String resultadosEsperados) {
        this.resultadosEsperados = resultadosEsperados;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public User getAlumno() {
        return alumno;
    }

    public void setAlumno(User alumno) {
        this.alumno = alumno;
    }

    public User getTutor() {
        return tutor;
    }

    public void setTutor(User tutor) {
        this.tutor = tutor;
    }


}