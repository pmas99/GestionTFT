package com.gestionTFT.GestionTFT.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tribunal")
public class Tribunal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tribunal", nullable = false)
    private Integer id;

    @Column(name = "fecha_examen")
    private LocalDate fechaExamen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secretario")
    private User secretario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presidente")
    private Profesores presidente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocal")
    private Profesores vocal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suplente")
    private Profesores suplente;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "lugar")
    private String lugar;

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getFechaExamen() {
        return fechaExamen;
    }

    public void setFechaExamen(LocalDate fechaExamen) {
        this.fechaExamen = fechaExamen;
    }

    public User getSecretario() {
        return secretario;
    }

    public void setSecretario(User secretario) {
        this.secretario = secretario;
    }

    public Profesores getPresidente() {
        return presidente;
    }

    public void setPresidente(Profesores presidente) {
        this.presidente = presidente;
    }

    public Profesores getVocal() {
        return vocal;
    }

    public void setVocal(Profesores vocal) {
        this.vocal = vocal;
    }

    public Profesores getSuplente() {
        return suplente;
    }

    public void setSuplente(Profesores suplente) {
        this.suplente = suplente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}