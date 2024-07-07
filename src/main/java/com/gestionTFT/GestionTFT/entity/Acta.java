package com.gestionTFT.GestionTFT.entity;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "acta")
public class Acta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tft")
    private Tft tft;

    @Column(name = "calificacion")
    private Double calificacion;

    @Column(name = "matricula")
    private Boolean matricula;

    @Lob
    @Column(name = "comentario")
    private String comentario;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    @Column(name = "documento")
    private String documento;


    public Tft getTft() { return tft; }

    public void setTft(Tft tft) { this.tft = tft; }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    public Boolean getMatricula() {
        return matricula;
    }

    public void setMatricula(Boolean matricula) {
        this.matricula = matricula;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

}