package com.gestionTFT.GestionTFT.entity;

import javax.persistence.*;

import java.util.Date;

@Entity
@Table(name = "solicitud")
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud", nullable = false)
    private Integer id;

    @Column(name = "fecha")
    private Date fecha;

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private Solicitud.Tipo tipo;

    @Column(name = "cv")
    private String cv;

    public enum Tipo {
        Solicitud, Propuesta, Validacion, Tribunal
    }

    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private Solicitud.Estado estado;
    public enum Estado{
        Pendiente, Aceptada, Rechazada
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno")
    private User alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tft")
    private Tft tft;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor")
    private User tutor;

    @Column(name = "descripcion")
    private String descripcion;

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = Estado.valueOf(estado);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public User getTutor() {
        return tutor;
    }

    public void setTutor(User tutor) {
        this.tutor = tutor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }


    public String getTipo() { return tipo.toString();}

    public void setTipo(Tipo tipo) { this.tipo = tipo;}

    public User getAlumno() {return alumno;}

    public void setAlumno(User alumno) {
        this.alumno = alumno;
    }

    public Tft getTft() {
        return tft;
    }

    public void setTft(Tft tft) {this.tft = tft;}

}